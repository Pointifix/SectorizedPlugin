package sectorized.update;

import arc.Core;
import arc.Events;
import arc.util.Timer;
import arc.util.*;
import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.BlockUnitUnit;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.net.Administration;
import mindustry.net.Packets;
import mindustry.type.ItemStack;
import sectorized.Manager;
import sectorized.SectorizedEvents;
import sectorized.constant.*;
import sectorized.world.map.Biomes;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static mindustry.Vars.netServer;
import static mindustry.Vars.state;

public class UpdateManager implements Manager {
    private final Interval interval = new Interval(3);

    private final HashSet<String> hideHud = new HashSet<>();

    private int infoMessageIndex = 0;

    private final HashMap<Biomes.Biome, Integer> biomeVotes = new HashMap<>();
    private final ArrayList<String> biomeVotePlayers = new ArrayList();
    private boolean biomeVoteFinished = false;

    @Override
    public void init() {
        Events.run(EventType.Trigger.update, () -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            State.time += Time.delta;

            if (interval.get(0, 60 * 5)) {
                double blockDamageMultiplier = Math.round(state.rules.blockDamageMultiplier * 10.0) / 10.0;
                double unitDamageMultiplier = Math.round(state.rules.unitDamageMultiplier * 10.0) / 10.0;
                double unitHealthMultiplier = Math.round(Units.healthMultiplier * 10.0) / 10.0;

                Groups.player.each(player -> !hideHud.contains(player.uuid()), player -> {
                    int cores = player.team().cores().size - 1;

                    StringBuilder infoPopupText = new StringBuilder(MessageUtils.cInfo + "Costs for next[white] \uF869\n");

                    for (ItemStack itemStack : CoreCost.requirements[Math.max(Math.min(cores, CoreCost.requirements.length - 1), 0)]) {
                        int availableItems = player.team().items().get(itemStack.item);

                        infoPopupText
                                .append(CoreCost.itemUnicodes.get(itemStack.item))
                                .append(availableItems >= itemStack.amount ? itemStack.amount + MessageUtils.cHighlight2 + "\uE800[white]" : MessageUtils.cDanger + availableItems + "[white]/" + itemStack.amount)
                                .append("\n");
                    }

                    infoPopupText.append(MessageUtils.cHighlight3)
                            .append("\nMultipliers:[white]\n")
                            .append("\uF856 ")
                            .append(blockDamageMultiplier)
                            .append(" \uF7F4 ")
                            .append(unitDamageMultiplier)
                            .append(" \uf848 ")
                            .append(unitHealthMultiplier)
                            .append("\n");

                    infoPopupText.append(MessageUtils.cWarning)
                            .append("\nUnit Cap: [white]")
                            .append(mindustry.entities.Units.getCap(player.team()))
                            .append("\n");

                    if (State.gameState == State.GameState.LOCKED) {
                        infoPopupText.append("\n(Re)spawning " + MessageUtils.cInfo + "locked[white]\n");
                    }

                    infoPopupText.append("\nToggle with " + MessageUtils.cHighlight3 + "/hud [white]");

                    Call.infoPopup(player.con, infoPopupText.toString(), 5.01f, Align.topLeft, 90, 5, 0, 0);
                });
            }

            if (interval.get(1, 60 * 60 * 8)) {
                switch (infoMessageIndex) {
                    case 0:
                        MessageUtils.sendMessage("Type " + MessageUtils.cHighlight3 + "/info" + MessageUtils.cDefault + " for information about how to play.\n" +
                                "You can request to join another team using " + MessageUtils.cHighlight3 + "/join" + MessageUtils.cDefault + "\n" +
                                "Join the discord and be part of the community! \n" +
                                MessageUtils.cPlayer + "\uE80D" + MessageUtils.cDefault + " https://discord.gg/AmdMXKkS9Q", MessageUtils.MessageLevel.INFO);
                        break;
                    case 1:
                        MessageUtils.sendMessage("Type " + MessageUtils.cHighlight3 + "/score" + MessageUtils.cDefault + " or " + MessageUtils.cHighlight3 + "/leaderboard" + MessageUtils.cDefault + " to display your rank!\n" +
                                MessageUtils.cPlayer + "\uE80D" + MessageUtils.cDefault + " https://discord.gg/AmdMXKkS9Q", MessageUtils.MessageLevel.INFO);
                        break;
                    case 2:
                        MessageUtils.sendMessage("If you are dominating the game end it as soon as possible, others want to play as well!\n" +
                                "Also, stalling does not increase your score! \n" +
                                MessageUtils.cPlayer + "\uE80D" + MessageUtils.cDefault + " https://discord.gg/AmdMXKkS9Q", MessageUtils.MessageLevel.INFO);
                        break;
                }

                infoMessageIndex = (infoMessageIndex + 1) % 3;
            }

            if (interval.get(2, 60 * 60 * 2)) {
                if (Groups.player.size() < 2) return;

                Team dominatingTeam = null;
                boolean lock = false;

                Team[] teams = Team.all.clone();
                Arrays.sort(teams, Comparator.comparingInt(t -> -t.cores().size));
                if (teams[0].cores().size >= teams[1].cores().size + 3 + (state.wave * 0.1)) {
                    dominatingTeam = teams[0];
                    lock = true;
                }

                if (!lock) {
                    HashMap<Team, Float> healthPerTeam = new HashMap<>();

                    Groups.unit.each(u -> u.team != Team.crux &&
                                    u.type != UnitTypes.mono &&
                                    u.type != UnitTypes.poly
                            , u -> {
                                if (u.team != Team.crux)
                                    healthPerTeam.put(u.team, healthPerTeam.getOrDefault(u.team, 0.0f) + u.health());
                            });

                    List<Map.Entry<Team, Float>> healthPerTeamEntries = new ArrayList<>(healthPerTeam.entrySet());
                    healthPerTeamEntries.sort((Map.Entry<Team, Float> a, Map.Entry<Team, Float> b) -> (int) (b.getValue() - a.getValue()));

                    if (healthPerTeamEntries.size() > 1 && healthPerTeamEntries.get(0).getValue() >= healthPerTeamEntries.get(1).getValue() + 10000 + 1000 * state.wave) {
                        dominatingTeam = healthPerTeamEntries.get(0).getKey();
                        lock = true;
                    }
                }

                if (State.gameState == State.GameState.ACTIVE && lock) {
                    State.gameState = State.GameState.LOCKED;
                    Events.fire(new SectorizedEvents.TeamDominatingEvent(dominatingTeam));
                } else if (State.gameState == State.GameState.LOCKED && !lock) {
                    State.gameState = State.GameState.ACTIVE;
                    Events.fire(new SectorizedEvents.NoTeamDominatingEvent());
                }
            }
        });

        Events.on(SectorizedEvents.GamemodeStartEvent.class, event -> {
            setServerDescription();
        });

        Events.on(EventType.WaveEvent.class, event -> {
            state.rules.loadout = Loadout.getLoadout(state.wave);

            state.rules.blockDamageMultiplier = (float) (1 + (1 / (Math.pow(state.wave * 0.05, 2) + 1)));
            state.rules.unitDamageMultiplier = (float) (1.5 - (1 / (Math.pow(state.wave * 0.05, 2) + 1)));

            Units.setUnitHealthMultiplier((float) (3 - (2 / (Math.pow(state.wave * 0.05, 2) + 1))));

            if (state.teams.active.size < 2 && state.wave >= 5) {
                Events.fire(new SectorizedEvents.RestartEvent("No teams left"));
            }

            setServerDescription();
        });

        Events.on(SectorizedEvents.RestartEvent.class, event -> {
            State.gameState = State.GameState.GAMEOVER;

            Log.info("Restarting: " + event.reason);
            MessageUtils.sendMessage(event.reason + "\nServer is restarting in " + MessageUtils.cInfo + "30" + MessageUtils.cDefault + " seconds", MessageUtils.MessageLevel.INFO);

            StringBuilder biomeNames = new StringBuilder();

            String prefix = "";
            for (Biomes.Biome biome : Biomes.all) {
                biomeNames.append(prefix).append(biome.toString().toLowerCase());
                prefix = ", ";
            }

            MessageUtils.sendMessage("Vote for the next map with " + MessageUtils.cHighlight3 + "/vote <biome>" + MessageUtils.cDefault + ".\n You can vote for the following biomes: " + biomeNames, MessageUtils.MessageLevel.INFO);

            Timer.schedule(() -> {
                if (!biomeVotes.isEmpty()) {
                    Map.Entry<Biomes.Biome, Integer> voteWinnerBiomeEntry = biomeVotes.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).get();

                    MessageUtils.sendMessage(MessageUtils.cHighlight1 + voteWinnerBiomeEntry.getKey() + MessageUtils.cDefault + " won with " + MessageUtils.cHighlight2 + voteWinnerBiomeEntry.getValue() + MessageUtils.cDefault + " vote(s)!", MessageUtils.MessageLevel.INFO);

                    Core.settings.put("biomeVote", voteWinnerBiomeEntry.getKey().toString());
                } else {
                    Core.settings.put("biomeVote", "");
                }
                Core.settings.manualSave();
                biomeVoteFinished = true;
            }, 24);

            final int seconds = 5;
            AtomicInteger countdown = new AtomicInteger(seconds);
            Timer.schedule(() -> {
                if (countdown.get() == 0) {
                    Log.info("Restarting server ...");
                    netServer.kickAll(Packets.KickReason.serverRestarting);
                    Events.fire(new SectorizedEvents.ShutdownEvent());
                    System.exit(1);
                }

                MessageUtils.sendMessage("Server is restarting in " + MessageUtils.cInfo + (countdown.getAndDecrement()) + MessageUtils.cDefault + " second(s).", MessageUtils.MessageLevel.INFO);
            }, 25, 1, seconds);
        });

        netServer.admins.addActionFilter((action) -> {
            if (State.gameState == State.GameState.INACTIVE) return true;

            switch (action.type) {
                case command:
                    return false;
                case control:
                    if (!(action.unit instanceof BlockUnitUnit) &&
                            action.unit.type != UnitTypes.oct) return false;
                    break;
            }

            return true;
        });
    }

    @Override
    public void reset() {
        State.time = 0;

        interval.clear();
        hideHud.clear();
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.register("restart", "Restarts the server.", args -> {
            Events.fire(new SectorizedEvents.RestartEvent("Called from console"));
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("hud", "Toggles the visibility of the hud.", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            String uuid = player.uuid();
            if (hideHud.contains(uuid)) {
                hideHud.remove(uuid);
                MessageUtils.sendMessage(player, "Hud will be shown again shortly!", MessageUtils.MessageLevel.INFO);
            } else {
                hideHud.add(uuid);
                MessageUtils.sendMessage(player, "Hud will be hidden shortly!", MessageUtils.MessageLevel.INFO);
            }
        });

        handler.<Player>register("time", "Display elapsed time.", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            int hour = (int) (State.time / 60 / 60 / 60);
            int min = (int) (State.time / 60 / 60 % 60);
            int sec = (int) (State.time / 60 % 60);

            MessageUtils.sendMessage(player, "Elapsed time: " +
                    (hour > 0 ? hour + "h " : "") +
                    (min > 0 ? min + "m " : "") +
                    sec + "s", MessageUtils.MessageLevel.INFO);
        });

        handler.<Player>register("info", "Display information about the gamemode.", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            MessageUtils.sendWelcomeMessage(player);
        });

        handler.<Player>register("vote", "[biome]", "Vote for a biome you want to play next round.", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            if (State.gameState == State.GameState.ACTIVE) {
                MessageUtils.sendMessage(player, "You can only vote for a biome after the round is over!", MessageUtils.MessageLevel.WARNING);
                return;
            }

            if (biomeVoteFinished) {
                MessageUtils.sendMessage(player, "Biome vote over!", MessageUtils.MessageLevel.WARNING);
                return;
            }

            if (State.gameState == State.GameState.GAMEOVER) {
                if (args.length == 0) {
                    MessageUtils.sendMessage(player, "Vote invalid! Biome does not exists!", MessageUtils.MessageLevel.WARNING);
                    return;
                }

                if (biomeVotePlayers.contains(player.uuid())) {
                    MessageUtils.sendMessage(player, "Vote invalid! You already voted!", MessageUtils.MessageLevel.WARNING);
                    return;
                }

                String biomeName = args[0];

                Biomes.Biome biomeVote = Biomes.all.stream().filter(biome -> biome.toString().equalsIgnoreCase(biomeName)).findFirst().orElse(null);

                if (biomeVote == null) {
                    MessageUtils.sendMessage(player, "Vote invalid! Biome does not exists!", MessageUtils.MessageLevel.WARNING);
                    return;
                }

                biomeVotePlayers.add(player.uuid());
                biomeVotes.put(biomeVote, biomeVotes.getOrDefault(biomeVote, 0) + 1);

                StringBuilder votes = new StringBuilder();
                String prefix = "";
                Iterator<Map.Entry<Biomes.Biome, Integer>> it = biomeVotes.entrySet().stream().sorted((b1, b2) -> Integer.compare(b2.getValue(), b1.getValue())).iterator();
                boolean first = true;
                while (it.hasNext()) {
                    Map.Entry<Biomes.Biome, Integer> biomeIntegerEntry = it.next();
                    votes.append(prefix).append(biomeIntegerEntry.getKey()).append(first ? MessageUtils.cHighlight2 : MessageUtils.cInfo).append("(").append(biomeIntegerEntry.getValue()).append(")").append(MessageUtils.cDefault);
                    prefix = ", ";
                    first = false;
                }

                MessageUtils.sendMessage(player, "Voted for " + MessageUtils.cInfo + biomeVote + MessageUtils.cDefault, MessageUtils.MessageLevel.INFO);

                MessageUtils.sendMessage("Current votes: " + votes, MessageUtils.MessageLevel.INFO);
            }
        });
    }

    private void setServerDescription() {
        if (state.wave < 2) {
            DiscordBot.setStatus("Wave " + state.wave + " | Time elapsed: Just started! | Players: " + Groups.player.size());

            Administration.Config.desc.set("[white]Wave [red]" + state.wave + "[gray] |[white] Time elapsed: [green]Just started!");
        } else {
            int hour = (int) (State.time / 60 / 60 / 60);
            int min = (int) (State.time / 60 / 60 % 60);

            DiscordBot.setStatus("Wave " + state.wave + " | Time elapsed: " + (hour > 0 ? hour + "h " : "") + min + "m" + (State.gameState == State.GameState.LOCKED ? " | LOCKED" : "") + " | Players: " + Groups.player.size());

            Administration.Config.desc.set("[white]Wave [red]" + state.wave + "[gray] |[white] Time elapsed: [goldenrod]" + (hour > 0 ? hour + "h " : "") +
                    min + "m" + (State.gameState == State.GameState.LOCKED ? "[gray] | [purple]LOCKED" : ""));
        }
    }
}
