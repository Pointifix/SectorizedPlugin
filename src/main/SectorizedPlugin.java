package main;

import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.ObjectSet;
import arc.util.*;
import main.generation.MapGenerator;
import main.sector.SectorManager;
import main.sector.SectorSpawns;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.core.GameState;
import mindustry.game.EventType;
import mindustry.game.Rules;
import mindustry.game.Team;
import mindustry.game.Waves;
import mindustry.gen.BlockUnitUnit;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.mod.Plugin;
import mindustry.net.Packets;
import mindustry.world.blocks.storage.CoreBlock;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static mindustry.Vars.*;

public class SectorizedPlugin extends Plugin {
    public static boolean restarting = false;

    private final Rules rules = new Rules();
    private final int mapSize = 700;

    private SectorManager sectorManager;
    private SectorizedTeamAssigner teamAssigner;
    private double elapsedTime;

    private final Interval interval = new Interval(2);
    private final ArrayList<String> hudHidden = new ArrayList<>();

    @Override
    public void init() {
        initRules();

        Events.run(EventType.Trigger.update, () -> {
            if (!active() || state.serverPaused || restarting) return;

            elapsedTime += Time.delta;

            if (interval.get(0, 60 * 5)) {
                Groups.player.each((player) -> this.teamAssigner.hasTeam(player) && !hudHidden.contains(player.uuid()), (player) -> Call.infoPopup(player.con, CoreCost.getRequirementsText(player.team()), 5.01f, Align.topLeft, 90, 5, 0, 0));
            }

            if (interval.get(1, 60 * 60 * 10)) {
                MessageUtils.sendMessage("Type [teal]/info [white]for information about the gamemode.\n" +
                        "Did you know that placing a [pink]vault[white] \uF866 next to a core will not transform it into a new core?\n" +
                        "Also join the discord and be part of the community! \n" +
                        "[blue]\uE80D [lightgray]https://discord.gg/AmdMXKkS9Q[white]", MessageUtils.MessageLevel.INFO);

                Groups.player.forEach((player) -> {
                    if (player.team() == Team.derelict) {
                        MessageUtils.sendMessage(player, "Try rejoining the game, there might be a new spawning spot available!", MessageUtils.MessageLevel.INFO);
                    }
                });
            }
        });

        Events.on(EventType.PlayerJoin.class, event -> {
            if (!active()) return;

            MessageUtils.sendWelcomeMessage(event.player);

            if (state.serverPaused) state.serverPaused = false;

            if (restarting) {
                MessageUtils.sendMessage(event.player, "Server is restarting shortly, please wait for the new game!", MessageUtils.MessageLevel.INFO);
                return;
            }

            if (this.teamAssigner.isEliminated(event.player)) {
                event.player.team(Team.derelict);
                MessageUtils.sendMessage(event.player, "You recently got eliminated, you can respawn [teal]5 minutes [white]after you got eliminated by rejoining the server!", MessageUtils.MessageLevel.WARNING);
            } else if (this.teamAssigner.hasTeam(event.player)) {
                event.player.team(this.teamAssigner.getTeam(event.player));
            } else if (event.player.team() == rules.defaultTeam) {
                try {
                    Point2 spawnPoint = this.sectorManager.sectorSpawns.getNextFreeSpawn();
                    this.teamAssigner.assignNewTeam(event.player);
                    this.sectorManager.addArea(spawnPoint.x, spawnPoint.y, (CoreBlock) Blocks.coreFoundation, event.player.team().id);
                    Loadout.spawnStartingBase(spawnPoint.x, spawnPoint.y, event.player.team());
                } catch (SectorSpawns.NoSpawnPointAvailableException e) {
                    MessageUtils.sendMessage(event.player, "No spawn point available", MessageUtils.MessageLevel.WARNING);
                }
            }
        });

        Events.on(EventType.PlayerLeave.class, event -> {
            if (Groups.player.size() == 1) state.serverPaused = true;

            if (this.teamAssigner.hasTeam(event.player)) {
                String uuid = event.player.uuid();
                Team team = event.player.team();
                Timer.schedule(() -> {
                    if (!Groups.player.contains((player) -> player.uuid().equals(uuid))) {
                        this.teamAssigner.forceEliminateTeam(team);
                    }
                }, 30 * event.player.team().cores().size);
            }
        });

        Events.on(EventType.BlockBuildBeginEvent.class, event -> {
            if (event.tile.block() == Blocks.coreFoundation || event.tile.block() == Blocks.coreNucleus) {
                this.sectorManager.upgradeArea(event.tile.x, event.tile.y, (CoreBlock) event.tile.block(), event.team.id);
            }
        });

        Events.on(EventType.BlockDestroyEvent.class, event -> {
            if (!active()) return;

            if (event.tile.build instanceof CoreBlock.CoreBuild) {
                Team team = event.tile.team();

                this.sectorManager.removeArea(event.tile.x, event.tile.y);

                if (team.cores().isEmpty()) {
                    this.teamAssigner.eliminateTeam(team, event.tile);
                }
            } else if (event.tile.block() == Blocks.shockMine) {
                Team team = event.tile.team();

                Timer.schedule(() -> {
                    if (this.sectorManager.validPlace(event.tile.x, event.tile.y, -1)) {
                        Call.effectReliable(Fx.tapBlock, event.tile.getX(), event.tile.getY(), 0, Color.red);
                        event.tile.setNet(Blocks.shockMine, team, 0);
                    }
                }, Mathf.random(7.0f, 12.0f));
            }
        });

        Events.on(EventType.WaveEvent.class, event -> state.rules.loadout = Loadout.getLoadout(state.wave));

        Vars.netServer.admins.addActionFilter((action) -> {
            if (!active()) return true;

            switch (action.type) {
                case command:
                    return false;
                case control:
                    if (!(action.unit instanceof BlockUnitUnit)) return false;
                    break;
                case placeBlock:
                    if (!this.sectorManager.validPlace(action.tile.x, action.tile.y, action.block, action.player.team().id)) {
                        MessageUtils.sendCannotBuildHereMessage(action.player);
                        return false;
                    }

                    if (action.block == Blocks.vault && action.tile.cblock() == Blocks.air && !VaultLogic.adjacentToCore(action.tile.x, action.tile.y, action.block)) {
                        if (CoreCost.checkAndConsumeFunds(action.player.team())) {
                            action.tile.setNet(Blocks.coreShard, action.player.team(), 0);
                            this.sectorManager.addArea(action.tile.x, action.tile.y, (CoreBlock) Blocks.coreShard, action.player.team().id);
                        } else {
                            MessageUtils.sendMessage(action.player, "Insufficient resources for a new core", MessageUtils.MessageLevel.WARNING);
                            action.tile.setNet(Blocks.air);
                        }

                        return false;
                    }
                    break;
                case breakBlock:
                    if (action.block == Blocks.shockMine) {
                        return false;
                    }
                    break;
            }

            return true;
        });

        Log.info("Sectorized Plugin loaded.");
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.register("sectorized", "Host sectorized game mode.", args -> {
            if (!state.is(GameState.State.menu)) {
                Log.err("Stop the server first");
                return;
            }

            Log.info("Hosting sectorized game mode ...");
            logic.reset();
            MapGenerator mapGenerator = new MapGenerator(mapSize, mapSize);
            world.loadGenerator(mapSize, mapSize, mapGenerator);
            this.sectorManager = new SectorManager(mapSize, mapSize);
            this.teamAssigner = new SectorizedTeamAssigner();
            state.rules = rules.copy();
            elapsedTime = 0;
            logic.play();
            netServer.openServer();
        });

        handler.register("restart", "Restart the server.", args -> {
            if (!active() || restarting) return;

            restarting = true;
            int seconds = 10;
            AtomicInteger countdown = new AtomicInteger(seconds);
            Timer.schedule(() -> {
                MessageUtils.sendMessage("Server restarting in [gold]" + (countdown.getAndDecrement()) + "[white] seconds.", MessageUtils.MessageLevel.INFO);

                if (countdown.get() == 0) {
                    Log.info("Restarting server ...");
                    netServer.kickAll(Packets.KickReason.serverRestarting);
                    System.exit(1);
                }
            }, 0, 1, seconds);
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("time", "Display elapsed time.", (args, player) -> {
            if (!active()) return;

            int hour = (int) (elapsedTime / 60 / 60 / 60);
            int min = (int) (elapsedTime / 60 / 60 % 60);
            int sec = (int) (elapsedTime / 60 % 60);

            MessageUtils.sendMessage(player, "Elapsed time: " +
                    (hour > 0 ? hour + "h " : "") +
                    (min > 0 ? min + "m " : "") +
                    sec + "s", MessageUtils.MessageLevel.INFO);
        });

        handler.<Player>register("info", "Display information about the gamemode.", (args, player) -> {
            if (!active()) return;

            MessageUtils.sendMessage(player, "To expand your sector place a [pink]vaults[white] \uF866 within the borders of your sector!\n" +
                    "Eliminate all other teams to win, but also be aware of the crux waves!", MessageUtils.MessageLevel.INFO);
        });

        handler.<Player>register("discord", "Display the discord invitation link.", (args, player) -> {
            if (!active()) return;

            player.sendMessage("[blue]\uE80D [lightgray]https://discord.gg/AmdMXKkS9Q[white]");
        });

        handler.<Player>register("eliminate", "Eliminates your team. (only if you are the team leader)", (args, player) -> {
            if (!active()) return;

            if (this.teamAssigner.hasTeam(player)) {
                if (this.teamAssigner.isTeamLead(player)) {
                    this.teamAssigner.forceEliminateTeam(player.team());
                } else {
                    MessageUtils.sendMessage(player, "You are not the team leader!", MessageUtils.MessageLevel.WARNING);
                }
            } else {
                MessageUtils.sendMessage(player, "You currently do not have a team!", MessageUtils.MessageLevel.WARNING);
            }
        });

        handler.<Player>register("hud", "Toggles the visibility of the hud.", (args, player) -> {
            if (!active()) return;

            String uuid = player.uuid();
            if (hudHidden.contains(uuid)) {
                hudHidden.remove(uuid);
                MessageUtils.sendMessage(player, "Hud will be shown again shortly!", MessageUtils.MessageLevel.INFO);
            } else {
                hudHidden.add(uuid);
                MessageUtils.sendMessage(player, "Hud will be hidden shortly!", MessageUtils.MessageLevel.INFO);
            }
        });
    }

    private boolean active() {
        return state.rules.tags.getBool("sectorized") && !state.is(GameState.State.menu);
    }

    private void initRules() {
        rules.tags.put("sectorized", "true");
        rules.enemyCoreBuildRadius = 0.0f;
        rules.canGameOver = false;
        rules.defaultTeam = Team.sharded;
        rules.waves = true;
        rules.spawns = new Waves().get();
        rules.waveSpacing = 60 * 150;
        rules.buildSpeedMultiplier = 2.0f;
        rules.dropZoneRadius = 100f;
        rules.logicUnitBuild = false;
        rules.loadout = Loadout.getLoadout(1);
        rules.bannedBlocks = ObjectSet.with(
                Blocks.shockMine,
                Blocks.switchBlock,
                Blocks.hyperProcessor,
                Blocks.logicProcessor,
                Blocks.microProcessor,
                Blocks.memoryCell,
                Blocks.memoryBank,
                Blocks.logicDisplay,
                Blocks.largeLogicDisplay);
    }
}
