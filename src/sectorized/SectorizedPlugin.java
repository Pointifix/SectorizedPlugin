package sectorized;

import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.ObjectSet;
import arc.util.*;
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
import mindustry.net.NetConnection;
import mindustry.net.Packets;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

import java.util.LinkedList;

import static mindustry.Vars.*;

public class SectorizedPlugin extends Plugin {
    private final Rules rules = new Rules();
    private final int mapSize = 600;

    private final Interval interval = new Interval(2);

    private double elapsedTime;

    private LinkedList<Integer> cannotBuildHereCooldown = new LinkedList<>();

    @Override
    public void init() {
        rules.tags.put("sectorized", "true");
        rules.enemyCoreBuildRadius = 0.0f;
        rules.canGameOver = false;
        rules.waves = true;
        rules.spawns = new Waves().get();
        rules.waveSpacing = 60 * 150;
        rules.buildSpeedMultiplier = 2.0f;
        rules.dropZoneRadius = 100f;
        rules.logicUnitBuild = false;
        rules.loadout = SectorizedLoadout.getLoadout(1);
        rules.bannedBlocks = ObjectSet.with(
                Blocks.shockMine,
                Blocks.coreFoundation,
                Blocks.coreNucleus,
                Blocks.switchBlock,
                Blocks.hyperProcessor,
                Blocks.logicProcessor,
                Blocks.microProcessor,
                Blocks.memoryCell,
                Blocks.memoryBank,
                Blocks.logicDisplay,
                Blocks.largeLogicDisplay);

        Vars.netServer.admins.addActionFilter((action) -> {
            if (!active()) return true;

            switch (action.type) {
                case command:
                    return false;
                case control:
                    if (!(action.unit instanceof BlockUnitUnit)) return false;
                    break;
                case placeBlock:
                    int offsetX = -(action.block.size - 1) / 2;
                    int offsetY = -(action.block.size - 1) / 2;

                    for (int dx = 0; dx < action.block.size; dx++) {
                        for (int dy = 0; dy < action.block.size; dy++) {
                            int wx = dx + offsetX + action.tile.x, wy = dy + offsetY + action.tile.y;

                            if (!SectorManager.validPlace(action.player.team(), wx, wy)) {
                                int id = action.player.id;
                                if (!cannotBuildHereCooldown.contains(id)) {
                                    action.player.sendMessage("[red]\u26A0 [white]You cannot build outside your sector. To expand your sector place a [pink]vault [white]within the borders of your sector!");
                                    cannotBuildHereCooldown.add(id);
                                    Timer.schedule(() -> cannotBuildHereCooldown.remove(Integer.valueOf(id)), 5);
                                }
                                return false;
                            }
                        }
                    }

                    if (action.block == Blocks.vault && action.tile.cblock() == Blocks.air) {
                        Point2[] nearby = Edges.getEdges(action.block.size);

                        boolean nearbyCore = false;
                        for (Point2 point2 : nearby) {
                            Tile neighbor = world.tile(action.tile.x + point2.x, action.tile.y + point2.y);

                            if (neighbor.block() == Blocks.coreShard || neighbor.block() == Blocks.coreFoundation || neighbor.block() == Blocks.coreNucleus)
                                nearbyCore = true;
                        }

                        if (!nearbyCore) {
                            if (SectorizedCoreCost.consumeIfHas(action.player.team())) {
                                SectorManager.assignArea(action.player.team(), (CoreBlock) Blocks.coreShard, action.tile.x, action.tile.y, false);
                            } else {
                                action.player.sendMessage("[red]\u26A0 [white]Insufficient resources for a new core");
                                action.tile.setNet(Blocks.air);
                            }
                            return false;
                        }
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

        Events.run(EventType.Trigger.update, () -> {
            if (!active() || state.serverPaused) return;

            elapsedTime += Time.delta;

            if (interval.get(0, 60 * 5)) {
                Groups.player.each(SectorizedTeamManager::hasTeam, (player) -> {
                    Call.infoPopup(player.con, SectorizedCoreCost.getRequirementsText(player.team()), 5.01f, Align.topLeft, 90, 5, 0, 0);
                });
            }

            if (interval.get(1, 60 * 60 * 5)) {
                Call.sendMessage("[gold]\uE837 [white]Type [teal]/info [white]for information about the gamemode.\n" +
                        "And join our discord! \n" +
                        "[blue]\uE80D [lightgray]https://discord.gg/AmdMXKkS9Q[white]");

                for (NetConnection netConnection : net.getConnections()) {
                    if (netConnection.player.team() == Team.derelict) {
                        netConnection.player.sendMessage("[gold]\uE837 [white]Try rejoining the game, there might be a new spawning spot available!");
                    }
                }
            }
        });

        Events.on(EventType.WaveEvent.class, event -> {
            state.rules.loadout = SectorizedLoadout.getLoadout(state.wave);
        });

        Events.on(EventType.BlockDestroyEvent.class, event -> {
            if (!active()) return;

            Team team = event.tile.team();
            if (event.tile.build instanceof CoreBlock.CoreBuild) {
                CoreBlock.CoreBuild core = (CoreBlock.CoreBuild) event.tile.build;

                SectorManager.detachArea(core);
            }

            if (event.tile.block() == Blocks.shockMine) {
                Timer.schedule(() -> {
                    if (SectorManager.isBorder(event.tile.x, event.tile.y)) {
                        Call.effectReliable(Fx.tapBlock, event.tile.getX(), event.tile.getY(), 0, Color.red);
                        event.tile.setNet(Blocks.shockMine, team, 0);
                    }
                }, Mathf.random(7.0f, 12.0f));
            }
        });

        Events.on(EventType.PlayerJoin.class, event -> {
            if (!active() || event.player.team() == Team.derelict) return;

            Call.infoMessage(event.player.con, "[cyan]Welcome to\n [#9C4F96]S[#FF6355]E[#FBA949]C[#FAE442]T[#8BD448]O[#2AA8F2]R[#01D93F]I[#F0EC00]Z[#FF8B00]E[#DB2B28]D[white] \n\n" +
                    "[gold]\uE87C How it works \uE87C[white]\n" +
                    "You can only build within the bounds of your teams sector, highlighted by shock mines.\n" +
                    "Expand your sector by placing vaults, you can see the expansion costs on the info popup.\n" +
                    "Kill all other teams to win and be aware of the crux team.\n\n" +
                    "[red]\u26A0 ALPHA \u26A0[white]\n" +
                    "Please report any bugs you encounter on the discord server, the plugin is still under development!\n" +
                    "Have fun playing :)\n\n" +
                    "[yellow]\uE80D https://discord.gg/AmdMXKkS9Q[white]");

            if (state.serverPaused) state.serverPaused = false;

            if (SectorizedTeamManager.isDead(event.player)) {
                event.player.team(Team.derelict);
                event.player.unit().kill();
                event.player.sendMessage("[gold]\uE837 [white]Your base was recently destroyed this game, reconnect 5 minutes after you got eliminated to get a new spawning spot!");
                return;
            }

            if (SectorizedTeamManager.hasTeam(event.player)) {
                SectorizedTeam sectorizedTeam = SectorizedTeamManager.getTeam(event.player);
                event.player.team(sectorizedTeam.team);
                sectorizedTeam.players.add(event.player);
                return;
            }

            Point2 spawn = SectorManager.getNextFreeSpawn();

            if (spawn != null) {
                Team team = SectorizedTeamManager.assignTeam(event.player);

                SectorManager.assignArea(team, (CoreBlock) Blocks.coreNucleus, spawn.x, spawn.y, true);
            } else {
                event.player.sendMessage("[gold]\uE837 [white]No spawn available, please wait for the next game or rejoin when new spawning spots are available again!");
                event.player.team(Team.derelict);
            }
        });

        Events.on(EventType.PlayerLeave.class, event -> {
            if (SectorizedTeamManager.hasTeam(event.player)) {
                SectorizedTeam sectorizedTeam = SectorizedTeamManager.getTeam(event.player);

                Timer.schedule(() -> {
                    for (Player player : sectorizedTeam.players) {
                        if (player == null) continue;

                        for (NetConnection netConnection : net.getConnections()) {
                            if (netConnection.player != null && netConnection.player.uuid().equals(player.uuid())) {
                                return;
                            }
                        }
                    }

                    if (sectorizedTeam.cores > 1)
                        Call.sendMessage("[red]\u26A0 [white]Team of player " + sectorizedTeam.leaderName + " [white]was eliminated due to inactivity!");
                    SectorizedTeamManager.forceKillTeam(event.player.team());
                }, 30 * sectorizedTeam.cores);
            }
        });

        Log.info("Sectorized plugin loaded");
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
            SectorizedMapGenerator sectorizedMapGenerator = new SectorizedMapGenerator(mapSize, mapSize);
            world.loadGenerator(mapSize, mapSize, sectorizedMapGenerator);
            SectorManager.reset();
            SectorizedTeamManager.reset();
            state.rules = rules.copy();
            elapsedTime = 0;
            state.serverPaused = true;
            cannotBuildHereCooldown = new LinkedList<>();
            logic.play();
            netServer.openServer();
        });

        handler.register("restart", "Restart the server.", args -> {
            if (!active()) return;
            Log.info("Restarting server ...");
            netServer.kickAll(Packets.KickReason.serverRestarting);
            System.exit(1);
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("time", "Display elapsed time.", (args, player) -> {
            if (!active()) return;
            int min = (int) (elapsedTime / 60 / 60);
            int sec = (int) (elapsedTime / 60 % 60);

            player.sendMessage("[gold]\uE837 [white]Elapsed time: " + min + "m " + sec + "s");
        });

        handler.<Player>register("state", "Display the state of your team.", (args, player) -> {
            if (!active()) return;
            if (SectorizedTeamManager.hasTeam(player) && !SectorizedTeamManager.isDead(player)) {
                player.sendMessage("[gold]\uE837 [white]Team state: " + SectorizedTeamManager.getTeam(player).cores + " cores, " + SectorizedTeamManager.getTeam(player).tilesCaptured + " tiles");
            } else {
                player.sendMessage("[gold]\uE837 [white]You currently don´t have a team!");
            }
        });

        handler.<Player>register("info", "Display information about the gamemode.", (args, player) -> {
            if (!active()) return;
            player.sendMessage("[gold]\uE837 [white]To expand your sector place a [pink]vault [white]where you want a new core to be placed!\n" +
                    "Kill all other teams to win, but also be aware of the crux team attacking you!");
        });

        handler.<Player>register("discord", "Display the discord link.", (args, player) -> {
            if (!active()) return;
            player.sendMessage("[blue]\uE80D [lightgray]https://discord.gg/AmdMXKkS9Q[white]");
        });
    }

    public boolean active() {
        return state.rules.tags.getBool("sectorized") && !state.is(GameState.State.menu);
    }
}
