package sectorized.update;

import arc.Core;
import arc.Events;
import arc.struct.Seq;
import arc.util.Timer;
import arc.util.*;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.net.Administration;
import mindustry.net.Packets;
import mindustry.type.ItemSeq;
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
    private boolean biomeVoteFinished = false;

    private int coreDominationDifference = 3;

    @Override
    public void init() {
        Events.run(EventType.Trigger.update, () -> {
            if (State.gameState == State.GameState.INACTIVE || State.gameState == State.GameState.GAMEOVER) return;

            State.time += Time.delta;

            if (interval.get(0, 60 * 5)) {
                double blockDamageMultiplier = Math.round(state.rules.blockDamageMultiplier * 10.0) / 10.0;
                double unitDamageMultiplier = Math.round(state.rules.unitDamageMultiplier * 10.0) / 10.0;
                double unitHealthMultiplier = Math.round(Units.healthMultiplier * 10.0) / 10.0;

                Groups.player.each(player -> !hideHud.contains(player.uuid()), player -> {
                    int cores = player.team().cores().size - 1;

                    StringBuilder infoPopupText = State.planet.equals(Planets.serpulo.name) ? new StringBuilder(MessageUtils.cInfo + "Costs for next[white] \uF869\n") : new StringBuilder(MessageUtils.cInfo + "Costs for next[white] \uF725\n");

                    ItemSeq[] requirements = State.planet.equals(Planets.serpulo.name) ? CoreCost.requirementsSerpulo : CoreCost.requirementsErekir;

                    for (ItemStack itemStack : requirements[Math.max(Math.min(cores, requirements.length - 1), 0)]) {
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

                    Call.infoPopupReliable(player.con, infoPopupText.toString(), 5.01f, Align.topLeft, 90, 5, 0, 0);
                });
            }

            if (interval.get(1, 60 * 60 * 8)) {
                switch (infoMessageIndex) {
                    case 0:
                        MessageUtils.sendMessage("In addition to commanding your units you can also use " + MessageUtils.cHighlight3 + "/attack /defend /idle" + MessageUtils.cDefault + ".\n" +
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
                                MessageUtils.cPlayer + "\uE80D" + MessageUtils.cDefault + " https://discord.gg/AmdMXKkS9Q", MessageUtils.MessageLevel.INFO);
                        break;
                }

                infoMessageIndex = (infoMessageIndex + 1) % 3;
            }

            if (interval.get(2, 60 * 60 * 2)) {
                if (Groups.player.size() < 2 || state.wave < 15) return;

                Team dominatingTeam = null;
                boolean lock = false;

                Team[] teams = Team.all.clone();
                Arrays.sort(teams, Comparator.comparingInt(t -> -t.cores().size));
                if (teams[0].cores().size >= teams[1].cores().size + coreDominationDifference + (state.wave * 0.1)) {
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

        Events.on(SectorizedEvents.BiomesGeneratedEvent.class, event -> {
            coreDominationDifference = State.planet.equals(Planets.serpulo.name) ? 3 : 5;
        });

        Events.on(SectorizedEvents.GamemodeStartEvent.class, event -> {
            setServerDescription();
        });

        Events.on(EventType.WaveEvent.class, event -> {
            if (State.gameState == State.GameState.INACTIVE || State.gameState == State.GameState.GAMEOVER) return;

            state.rules.loadout = Loadout.getLoadout(state.wave);

            state.rules.blockDamageMultiplier = (float) (1 + (2 / (Math.pow(state.wave * 0.05, 5) + 1)));
            state.rules.unitDamageMultiplier = (float) (3 - (2.5 / (Math.pow(state.wave * 0.05, 5) + 1)));

            Units.setUnitHealthMultiplier((float) (6 - (5 / (Math.pow(state.wave * 0.05, 5) + 1))));

            if (state.teams.active.size < 2 && state.wave >= 5) {
                DiscordBot.sendMessage("**Game Over!** Crux won the game in " + Vars.state.wave + " waves.");

                Events.fire(new SectorizedEvents.RestartEvent("No teams left"));
            }

            setServerDescription();
        });

        Events.on(EventType.PlayerJoin.class, event -> {
            setServerDescription();
        });

        Events.on(EventType.PlayerLeave.class, event -> {
            setServerDescription();
        });

        MenuUtils.addMenu(20, player -> {
            return new MenuUtils.MenuContent(
                    "GAME OVER - Planet Vote",
                    (State.winner == null ? MessageUtils.cDanger + "Crux[white] won the game in " + MessageUtils.cInfo + Vars.state.wave + "[white] waves!\n\n" : MessageUtils.cPlayer + State.winner.player.name + "[white] won the game in " + MessageUtils.cInfo + Vars.state.wave + "[white] waves!\n\n") +
                            "Vote for a planet or biome you want to play next game.\n\n" +
                            "You have 25 seconds to submit your vote!",
                    new String[][]{
                            {MessageUtils.cHighlight2 + "Serpulo"},
                            {MessageUtils.cHighlight3 + "Erekir"},
                            {MessageUtils.cInfo + "Vote for a specific biome"},
                            {MessageUtils.cDanger + "Cancel"}
                    },
                    new int[][]{
                            {-1},
                            {-1},
                            {21},
                            {-1}
                    },
                    new MenuUtils.Handler[][]{
                            {p -> {
                                if (!biomeVoteFinished) {
                                    for (int i = 0; i < Biomes.all.size(); i++) {
                                        Biomes.Biome biome = Biomes.all.get(i);
                                        if (biome.getPlanet().equals(Planets.serpulo.name)) {
                                            biomeVotes.put(biome, biomeVotes.getOrDefault(biome, 0) + 1);
                                        }
                                    }
                                    MessageUtils.sendMessage(player, "Voted for " + MessageUtils.cInfo + "Serpulo" + MessageUtils.cDefault, MessageUtils.MessageLevel.INFO);
                                } else {
                                    MessageUtils.sendMessage(player, "Biome vote over!", MessageUtils.MessageLevel.WARNING);
                                }
                            }},
                            {p -> {
                                if (!biomeVoteFinished) {
                                    for (int i = 0; i < Biomes.all.size(); i++) {
                                        Biomes.Biome biome = Biomes.all.get(i);
                                        if (biome.getPlanet().equals(Planets.erekir.name)) {
                                            biomeVotes.put(biome, biomeVotes.getOrDefault(biome, 0) + 1);
                                        }
                                    }
                                    MessageUtils.sendMessage(player, "Voted for " + MessageUtils.cInfo + "Erekir" + MessageUtils.cDefault, MessageUtils.MessageLevel.INFO);
                                } else {
                                    MessageUtils.sendMessage(player, "Biome vote over!", MessageUtils.MessageLevel.WARNING);
                                }
                            }},
                            {p -> {

                            }},
                            {p -> {

                            }}
                    }
            );
        });

        MenuUtils.addMenu(21, player -> {
            String[][] options = new String[Biomes.all.size() + 1][1];
            int[][] links = new int[Biomes.all.size() + 1][1];
            MenuUtils.Handler[][] handlers = new MenuUtils.Handler[Biomes.all.size() + 1][1];

            final int[] i = {0};
            Biomes.all.stream().filter(e -> e.getPlanet().equals(Planets.serpulo.name)).sorted((e1, e2) -> e1.toString().compareToIgnoreCase(e2.toString())).forEach(biome -> {
                options[i[0]][0] = (biome.getPlanet().equals(Planets.serpulo.name) ? MessageUtils.cHighlight2 : MessageUtils.cHighlight3) + biome;
                links[i[0]][0] = -1;
                handlers[i[0]][0] = p -> {
                    if (!biomeVoteFinished) {
                        biomeVotes.put(biome, biomeVotes.getOrDefault(biome, 0) + 1);
                        MessageUtils.sendMessage(player, "Voted for " + MessageUtils.cInfo + biome + MessageUtils.cDefault, MessageUtils.MessageLevel.INFO);
                    } else {
                        MessageUtils.sendMessage(player, "Biome vote over!", MessageUtils.MessageLevel.WARNING);
                    }
                };

                i[0]++;
            });

            Biomes.all.stream().filter(e -> e.getPlanet().equals(Planets.erekir.name)).sorted((e1, e2) -> e1.toString().compareToIgnoreCase(e2.toString())).forEach(biome -> {
                options[i[0]][0] = (biome.getPlanet().equals(Planets.serpulo.name) ? MessageUtils.cHighlight2 : MessageUtils.cHighlight3) + biome;
                links[i[0]][0] = -1;
                handlers[i[0]][0] = p -> {
                    if (!biomeVoteFinished) {
                        biomeVotes.put(biome, biomeVotes.getOrDefault(biome, 0) + 1);
                        MessageUtils.sendMessage(player, "Voted for " + MessageUtils.cInfo + biome + MessageUtils.cDefault, MessageUtils.MessageLevel.INFO);
                    } else {
                        MessageUtils.sendMessage(player, "Biome vote over!", MessageUtils.MessageLevel.WARNING);
                    }
                };

                i[0]++;
            });

            options[Biomes.all.size()][0] = MessageUtils.cDanger + "Cancel";
            links[Biomes.all.size()][0] = -1;
            handlers[Biomes.all.size()][0] = p -> {

            };

            return new MenuUtils.MenuContent(
                    "GAME OVER - Biome Vote",
                    (State.winner == null ? MessageUtils.cDanger + "Crux[white] won the game in " + MessageUtils.cInfo + Vars.state.wave + "[white] waves!\n\n" : MessageUtils.cPlayer + State.winner.player.name + "[white] won the game in " + MessageUtils.cInfo + Vars.state.wave + "[white] waves!\n\n") +
                            "Vote for a biome you want to play next game.\n\n" +
                            "You have 25 seconds to submit your vote!",
                    options,
                    links,
                    handlers
            );
        });

        Events.on(SectorizedEvents.RestartEvent.class, event -> {
            State.gameState = State.GameState.GAMEOVER;

            Log.info("Restarting: " + event.reason);
            MessageUtils.sendMessage(event.reason + "\nServer is restarting in " + MessageUtils.cInfo + "30" + MessageUtils.cDefault + " seconds", MessageUtils.MessageLevel.INFO);

            for (Player player : Groups.player) {
                MenuUtils.showMenu(20, player);

                Timer.schedule(() -> {
                    if (biomeVotes.isEmpty() || biomeVoteFinished) return;

                    StringBuilder votes = new StringBuilder();
                    int maxValueInMap = (Collections.max(biomeVotes.values()));

                    if (biomeVotes.entrySet().stream().anyMatch(e -> e.getKey().getPlanet().equals(Planets.serpulo.name)))
                        votes.append(MessageUtils.cHighlight2 + "\n\nSerpulo");
                    biomeVotes.entrySet().stream().filter(e -> e.getKey().getPlanet().equals(Planets.serpulo.name)).sorted((e1, e2) -> e1.getKey().toString().compareToIgnoreCase(e2.getKey().toString())).forEach(e -> {
                        votes.append("\n")
                                .append(e.getValue() == maxValueInMap ? MessageUtils.cInfo : MessageUtils.cDefault)
                                .append(e.getKey())
                                .append(" (")
                                .append(e.getValue())
                                .append(")")
                                .append(MessageUtils.cDefault);
                    });

                    if (biomeVotes.entrySet().stream().anyMatch(e -> e.getKey().getPlanet().equals(Planets.erekir.name)))
                        votes.append(MessageUtils.cHighlight3 + "\n\nErekir");
                    biomeVotes.entrySet().stream().filter(e -> e.getKey().getPlanet().equals(Planets.erekir.name)).sorted((e1, e2) -> e1.getKey().toString().compareToIgnoreCase(e2.getKey().toString())).forEach(e -> {
                        votes.append("\n")
                                .append(e.getValue() == maxValueInMap ? MessageUtils.cInfo : MessageUtils.cDefault)
                                .append(e.getKey())
                                .append(" (")
                                .append(e.getValue())
                                .append(")")
                                .append(MessageUtils.cDefault);
                    });

                    Call.infoPopupReliable(MessageUtils.cHighlight1 + "Votes:" + MessageUtils.cDefault + votes, 3.01f, Align.topLeft, 90, 5, 0, 0);
                }, 0, 3);
            }

            Timer.schedule(() -> {
                if (!biomeVotes.isEmpty()) {
                    Seq<Map.Entry<Biomes.Biome, Integer>> maxEntries = new Seq<>();
                    int maxValueInMap = (Collections.max(biomeVotes.values()));
                    for (Map.Entry<Biomes.Biome, Integer> entry : biomeVotes.entrySet()) {
                        if (entry.getValue() == maxValueInMap) {
                            maxEntries.add(entry);
                        }
                    }
                    Map.Entry<Biomes.Biome, Integer> voteWinnerBiomeEntry = maxEntries.random();

                    MessageUtils.sendMessage(MessageUtils.cHighlight1 + voteWinnerBiomeEntry.getKey() + " (" + Strings.capitalize(voteWinnerBiomeEntry.getKey().getPlanet()) + ")" + MessageUtils.cDefault + " won with " + MessageUtils.cHighlight2 + voteWinnerBiomeEntry.getValue() + MessageUtils.cDefault + " vote(s)!", MessageUtils.MessageLevel.INFO);
                    Call.announce(MessageUtils.cHighlight1 + voteWinnerBiomeEntry.getKey() + " (" + Strings.capitalize(voteWinnerBiomeEntry.getKey().getPlanet()) + ")" + MessageUtils.cDefault + " won with " + MessageUtils.cHighlight2 + voteWinnerBiomeEntry.getValue() + MessageUtils.cDefault + " vote(s)!");

                    Core.settings.put("biomeVote", voteWinnerBiomeEntry.getKey().toString());
                } else {
                    Core.settings.put("biomeVote", "");
                }
                Core.settings.manualSave();
                biomeVoteFinished = true;
            }, 21);

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
            if (State.gameState == State.GameState.INACTIVE || State.gameState == State.GameState.GAMEOVER) return;

            String uuid = player.uuid();
            if (hideHud.contains(uuid)) {
                hideHud.remove(uuid);
                MessageUtils.sendMessage(player, "Hud will be shown again shortly!", MessageUtils.MessageLevel.INFO);
            } else {
                hideHud.add(uuid);
                MessageUtils.sendMessage(player, "Hud will be hidden shortly!", MessageUtils.MessageLevel.INFO);
            }
        });

        handler.<Player>register("restart", "Restarts the server, only available if only one player is online.", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE || State.gameState == State.GameState.GAMEOVER) return;

            int sec = (int) (State.time / 60);
            if (sec < 30) {
                MessageUtils.sendMessage(player, "You cannot call a restart at the start of a game, please wait " + MessageUtils.cInfo + "30 seconds" + MessageUtils.cDefault + "!", MessageUtils.MessageLevel.WARNING);
                return;
            }

            if (Groups.player.size() == 1) {
                Events.fire(new SectorizedEvents.RestartEvent("Called by player"));
            } else {
                MessageUtils.sendMessage(player, "You can only call a restart if no one else is online.", MessageUtils.MessageLevel.WARNING);
            }
        });

        handler.<Player>register("time", "Display elapsed time.", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE || State.gameState == State.GameState.GAMEOVER) return;

            int hour = (int) (State.time / 60 / 60 / 60);
            int min = (int) (State.time / 60 / 60 % 60);
            int sec = (int) (State.time / 60 % 60);

            MessageUtils.sendMessage(player, "Elapsed time: " +
                    (hour > 0 ? hour + "h " : "") +
                    (min > 0 ? min + "m " : "") +
                    sec + "s", MessageUtils.MessageLevel.INFO);
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
