package sectorized.faction;

import arc.Events;
import arc.struct.Seq;
import arc.util.CommandHandler;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.ai.types.LogicAI;
import mindustry.content.UnitTypes;
import mindustry.entities.Units;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.world.blocks.storage.CoreBlock;
import sectorized.Manager;
import sectorized.SectorizedEvents;
import sectorized.constant.*;
import sectorized.faction.core.Faction;
import sectorized.faction.core.Member;
import sectorized.faction.logic.FactionLogic;
import sectorized.faction.logic.MemberLogic;
import sectorized.faction.persistence.RankingPersistence;

import java.util.Arrays;
import java.util.Comparator;

import static mindustry.Vars.tilesize;

public class FactionManager implements Manager {
    private MemberLogic memberLogic;
    private FactionLogic factionLogic;

    private final RankingPersistence rankingPersistence = new RankingPersistence();

    private final Seq<JoinRequest> joinRequests = new Seq<>();

    @Override
    public void init() {
        rankingPersistence.updateHallfOfFame();

        MenuUtils.addMenu(0, player -> {
            Member member = memberLogic.getMember(player);

            String message;
            String[][] options;
            int[][] links;
            MenuUtils.Handler[][] handler;

            if (member.state == Member.MemberState.WAITING && State.gameState == State.GameState.LOCKED) {
                message = "A player is currently dominating the game for which reason you can only " + MessageUtils.cHighlight1 + "Spectate [white], please wait until the game is " +
                        "unlocked again or a new game starts, you can still request to join another team (using " + MessageUtils.cHighlight3 + "/join" + "[white]).";

                options = new String[][]{
                        {MessageUtils.cHighlight1 + "\uE88A Spectate"},
                        {MessageUtils.cInfo + "\uE87C Tutorial"},
                        {"[blue]\uE80D [white]Discord"}
                };

                links = new int[][]{
                        {-1},
                        {1},
                        {0}
                };

                handler = new MenuUtils.Handler[][]{
                        {p -> {

                        }},
                        {p -> {

                        }},
                        {p -> {
                            Call.openURI(p.con(), "https://discord.gg/AmdMXKkS9Q");
                        }}
                };
            } else if (member.state == Member.MemberState.ELIMINATED) {
                message = "You recently got eliminated, you can respawn " + MessageUtils.cInfo + "5 minutes" + MessageUtils.cDefault + " after you got eliminated by rejoining the server!\n\n" +
                        "Press " + MessageUtils.cHighlight1 + "Spectate [white]if you want to watch others play or if you want to join another team (using " + MessageUtils.cHighlight3 + "/join" + "[white]).";

                options = new String[][]{
                        {MessageUtils.cHighlight1 + "\uE88A Spectate"},
                        {MessageUtils.cInfo + "\uE87C Tutorial"},
                        {"[blue]\uE80D [white]Discord"}
                };

                links = new int[][]{
                        {-1},
                        {1},
                        {0}
                };

                handler = new MenuUtils.Handler[][]{
                        {p -> {

                        }},
                        {p -> {

                        }},
                        {p -> {
                            Call.openURI(p.con(), "https://discord.gg/AmdMXKkS9Q");
                        }}
                };
            } else {
                message = "Press " + MessageUtils.cHighlight2 + "Play [white]to start your own base.\n\n" +
                        "Press " + MessageUtils.cHighlight1 + "Spectate [white]if you want to watch others play or if you want to join another team (using " + MessageUtils.cHighlight3 + "/join" + "[white]).";

                options = new String[][]{
                        {MessageUtils.cHighlight2 + "\uE829 Play"},
                        {MessageUtils.cHighlight1 + "\uE88A Spectate"},
                        {MessageUtils.cInfo + "\uE87C Tutorial"},
                        {"[blue]\uE80D [white]Discord"}
                };

                links = new int[][]{
                        {-1},
                        {-1},
                        {1},
                        {0}
                };

                handler = new MenuUtils.Handler[][]{
                        {p -> {
                            if (member.state == Member.MemberState.WAITING && State.gameState == State.GameState.LOCKED) {
                                MessageUtils.sendMessage(member.player, "Spawning is currently " + MessageUtils.cInfo + "locked" + MessageUtils.cDefault + " because a player is dominating the game!", MessageUtils.MessageLevel.WARNING);
                            } else if (member.state == Member.MemberState.WAITING) {
                                Events.fire(new SectorizedEvents.NewMemberEvent(member));
                            } else if (member.state == Member.MemberState.ELIMINATED) {
                                MessageUtils.sendMessage(p, "You recently got eliminated, you can respawn " + MessageUtils.cInfo + "5 minutes" + MessageUtils.cDefault + " after you got eliminated by rejoining the server!", MessageUtils.MessageLevel.WARNING);
                            }
                        }},
                        {p -> {

                        }},
                        {p -> {

                        }},
                        {p -> {
                            Call.openURI(p.con(), "https://discord.gg/AmdMXKkS9Q");
                        }}
                };
            }
            message += "\n\nIf it is your first time on sectorized please read the " + MessageUtils.cInfo + "Tutorial [white]first!" +
                    "\n\n[blue]\uE80D" + MessageUtils.cDefault + " https://discord.gg/AmdMXKkS9Q[white]";

            return new MenuUtils.MenuContent(
                    "Welcome to [white]\uF897[#9C4F96]S[#FF6355]E[#FBA949]C[#FAE442]T[#8BD448]O[#2AA8F2]R[#01D93F]I[#F0EC00]Z[#FF8B00]E[#DB2B28]D[white]\uF897",
                    message,
                    options,
                    links,
                    handler
            );
        });
        MenuUtils.addMenu(1, player -> {
            return new MenuUtils.MenuContent(
                    "Tutorial - The Basics",
                    "You start with a small base located at a random location that is not occupied by any other base. " +
                            "Your base is surrounded by " + MessageUtils.cHighlight1 + "shock mines [white]\uF897 highlighting the borders of your sector within which you can build.\n\n" +
                            "To expand your sector, place a " + MessageUtils.cHighlight3 + "vault[white] \uF866 or " + MessageUtils.cHighlight3 + "reinforced vault[white] \uF70C within your current sector. " +
                            "The current cost for expanding your sector is diplayed on the info popup. " +
                            "The building cost and time of a " + MessageUtils.cHighlight3 + "vault[white] is irrelevant for expanding your sector. " +
                            "Placing a vault instantly turns it into a new core and moves your " + MessageUtils.cHighlight1 + "shock mines [white]border, allowing you to build in the newly acquired area.",
                    new String[][]{
                            {"Next"},
                            {"Back"},
                    },
                    new int[][]{
                            {2},
                            {0}
                    },
                    new MenuUtils.Handler[][]{
                            {p -> {

                            }},
                            {p -> {

                            }}
                    }
            );
        });
        MenuUtils.addMenu(2, player -> {
            return new MenuUtils.MenuContent(
                    "Tutorial - Your goal",
                    "This is a FFA server, you win by eliminating all other teams. " +
                            "But be aware of the crux waves which have several spawn points across the map and may also " +
                            "attack your base with air units spawning outside the map!\n\n" +
                            "Unit health \uf848 and damage \uF7F4 increase over time, while turret damage \uF856 decreases. It is easier to defend early into the " +
                            "match while units will break through even the strongest defence at some point! So make sure you win the game before the " +
                            "crux waves conquer the map. Your units will not attack automatically but you can command them. Additionally, you can use the " +
                            "commands " + MessageUtils.cHighlight3 + "/attack [white]and " + MessageUtils.cHighlight3 + "/defend [white]in order to command your units to automatically attack or to defend your base again.",
                    new String[][]{
                            {"Next"},
                            {"Back"},
                    },
                    new int[][]{
                            {3},
                            {1}
                    },
                    new MenuUtils.Handler[][]{
                            {p -> {

                            }},
                            {p -> {

                            }}
                    }
            );
        });
        MenuUtils.addMenu(3, player -> {
            return new MenuUtils.MenuContent(
                    "Tutorial - Commands",
                    "You can use the following commands:\n" +
                            "\n" +
                            MessageUtils.cHighlight3 + "/attack[white] - Units automatically attack \n" +
                            MessageUtils.cHighlight3 + "/defend[white] - Units automatically defend from nearby enemies\n" +
                            MessageUtils.cHighlight3 + "/idle[white] - Units idle\n" +
                            MessageUtils.cHighlight3 + "/score[white] - Display your rank, score and wins\n" +
                            MessageUtils.cHighlight3 + "/leaderboard[white] - Display the leaderboard\n" +
                            MessageUtils.cHighlight3 + "/join[white] - Request to join another team\n" +
                            MessageUtils.cHighlight3 + "/accept[white] - Accept a join request\n" +
                            MessageUtils.cHighlight3 + "/deny[white] - Deny a join request\n" +
                            MessageUtils.cHighlight3 + "/eliminate[white] - eliminate your base, you will loose points if spawned more than 5 minutes ago\n" +
                            MessageUtils.cHighlight3 + "/hud[white] - toggle the info popup for core cost\n" +
                            MessageUtils.cHighlight3 + "/restart[white] - restart the server (only works if you are alone on the server)\n" +
                            MessageUtils.cHighlight3 + "/time[white] - Display the elapsed time",
                    new String[][]{
                            {"Main Menu"},
                            {"Back"},
                    },
                    new int[][]{
                            {0},
                            {2}
                    },
                    new MenuUtils.Handler[][]{
                            {p -> {

                            }},
                            {p -> {

                            }}
                    }
            );
        });

        Events.on(EventType.PlayerJoin.class, event -> {
            if (State.gameState == State.GameState.INACTIVE || State.gameState == State.GameState.GAMEOVER) return;

            Member member = memberLogic.playerJoin(event.player);

            if (member.state != Member.MemberState.ALIVE) {
                MenuUtils.showMenu(0, event.player);
            }
        });

        Events.on(SectorizedEvents.MemberSpawnedEvent.class, event -> {
            Faction faction = factionLogic.getNewFaction();
            faction.addMember(event.member);
            faction.maxCores = 1;

            StartingBase.spawnStartingBase(event.spawnPoint.x, event.spawnPoint.y, faction.team);
        });

        Events.on(SectorizedEvents.NoSpawnPointAvailableEvent.class, event -> {
            MessageUtils.sendMessage(event.member.player, "No spawn point available, please wait!", MessageUtils.MessageLevel.WARNING);
        });

        Events.on(EventType.BlockDestroyEvent.class, event -> {
            if (State.gameState == State.GameState.INACTIVE || State.gameState == State.GameState.GAMEOVER) return;

            if (event.tile.build instanceof CoreBlock.CoreBuild) {
                CoreBlock.CoreBuild coreBuild = (CoreBlock.CoreBuild) event.tile.build;
                Faction faction = factionLogic.getFaction(event.tile.team());

                if (coreBuild.equals(Building.bulletDamageEvent.build) && Building.bulletDamageEvent.source.team() != faction.team) {
                    faction.lastAttacker = Building.bulletDamageEvent.source.team();
                }

                Events.fire(new SectorizedEvents.CoreDestroyEvent(coreBuild, faction));

                if (faction.team.cores().isEmpty()) {
                    Faction attacker = faction.lastAttacker == null ? null : factionLogic.getFaction(faction.lastAttacker);

                    boolean fallback = false;
                    if (attacker == null) {
                        final int radius = 60 * tilesize;

                        final Unit[] mostHealthUnit = {null};
                        Units.nearbyEnemies(faction.team, event.tile.getX() - radius, event.tile.getY() - radius, radius * 2, radius * 2, unit -> {
                            if (!unit.isPlayer() && unit.team != faction.team && unit.type() != UnitTypes.mono && unit.team != Team.crux) {
                                if (mostHealthUnit[0] == null) mostHealthUnit[0] = unit;
                                else if (unit.health() > mostHealthUnit[0].health()) mostHealthUnit[0] = unit;
                            }
                        });

                        if (mostHealthUnit[0] != null) attacker = factionLogic.getFaction(mostHealthUnit[0].team);
                        else if (faction.maxCores >= 5) {
                            Team dominatingTeam;
                            Team[] teams = Team.all.clone();
                            Arrays.sort(teams, Comparator.comparingInt(t -> -t.cores().size));
                            dominatingTeam = teams[0];
                            Faction dominatingFaction = factionLogic.getFaction(dominatingTeam);

                            if (dominatingFaction != null && dominatingFaction.maxCores >= 5) {
                                fallback = true;
                                attacker = dominatingFaction;
                            }
                        }
                    }

                    Events.fire(new SectorizedEvents.EliminateFactionEvent(faction, attacker, fallback));
                }
            }
        });

        Events.on(SectorizedEvents.EliminateFactionEvent.class, event -> {
            factionLogic.removeFaction(event.defender, event.attacker, event.fallback);
        });

        Events.on(SectorizedEvents.CoreBuildEvent.class, event -> {
            Faction faction = factionLogic.getFaction(event.tile.team());

            faction.maxCores = Math.max(faction.team.cores().size, faction.maxCores);
        });

        Events.on(EventType.PlayerLeave.class, event -> {
            if (State.gameState == State.GameState.INACTIVE || State.gameState == State.GameState.GAMEOVER) return;

            Member member = memberLogic.playerLeave(event.player);

            if (member.faction != null) {
                if (!member.faction.members.contains(m -> m.online)) {
                    Faction f = member.faction;

                    Timer.schedule(() -> {
                        if (!f.members.contains(m -> m.online))
                            factionLogic.destroyCores(f);
                    }, 30 * f.maxCores);
                }
            }
        });

        Events.on(SectorizedEvents.ShutdownEvent.class, event -> {
            rankingPersistence.closeConnection();
        });

        Events.on(SectorizedEvents.TeamDominatingEvent.class, event -> {
            Faction faction = factionLogic.getFaction(event.team);

            MessageUtils.sendMessage("(Re)spawning is now " + MessageUtils.cInfo + "locked" + MessageUtils.cDefault + " because " + MessageUtils.cPlayer + faction.members.first().player.name + MessageUtils.cDefault + " is dominating the game!", MessageUtils.MessageLevel.WARNING);
        });

        Events.on(SectorizedEvents.NoTeamDominatingEvent.class, event -> {
            if (State.gameState == State.GameState.INACTIVE || State.gameState == State.GameState.GAMEOVER) return;

            MessageUtils.sendMessage("(Re)spawning is now " + MessageUtils.cInfo + "unlocked" + MessageUtils.cDefault + " again!", MessageUtils.MessageLevel.INFO);
        });

        Events.on(EventType.WaveEvent.class, event -> {
            if (Vars.state.wave == 45) {
                MessageUtils.sendMessage("Everyone with more than 5 cores will loose 1% of their score per wave after wave 50! Stop camping, start fighting!", MessageUtils.MessageLevel.WARNING);
            }

            if (Vars.state.wave == 50) {
                MessageUtils.sendMessage("Everyone with more than 5 cores will now loose 1% of their score per wave! Stop camping, start fighting!", MessageUtils.MessageLevel.WARNING);
            }

            if (Vars.state.wave > 50) {
                for (Player player : Groups.player) {
                    Member member = memberLogic.getMember(player);

                    if (member.faction.team.cores().size >= 5) {
                        int loss = (int) (member.score * 0.01f);
                        member.score *= 0.99f;

                        MessageUtils.sendMessage(member.player, "You lost" + loss + " points! Stop camping, start fighting!", MessageUtils.MessageLevel.WARNING);
                    }
                }
            }
        });
    }

    @Override
    public void reset() {
        memberLogic = new MemberLogic(rankingPersistence);
        factionLogic = new FactionLogic(rankingPersistence);
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {

    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        for (int i = 0; i < rankingPersistence.leaderBoardPages; i++) {
            int finalI = i;
            MenuUtils.addMenu(40 + i, player -> {
                return new MenuUtils.MenuContent(
                        "LEADERBOARD - Page " + (finalI + 1) + "/" + rankingPersistence.leaderBoardPages,
                        rankingPersistence.leaderboardTexts[finalI],
                        new String[][]{
                                {"\uE802", "\uE803"},
                                {"Close"}
                        },
                        new int[][]{
                                {40 + Math.max(finalI - 1, 0), 40 + Math.min(finalI + 1, rankingPersistence.leaderBoardPages - 1)},
                                {-1}
                        },
                        new MenuUtils.Handler[][]{
                                {p -> {
                                }, p -> {
                                }},
                                {p -> {
                                }}
                        }
                );
            });
        }

        handler.<Player>register("leaderboard", "Displays the current leaderboard", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE || State.gameState == State.GameState.GAMEOVER) return;

            MenuUtils.showMenu(40, player);
        });

        handler.<Player>register("score", "Displays your score and wins", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE || State.gameState == State.GameState.GAMEOVER) return;

            Member member = memberLogic.getMember(player);

            if (member.rank == 0) {
                MessageUtils.sendMessage(player, "You are not ranked yet. Play a game to get ranked!", MessageUtils.MessageLevel.INFO);
            } else {
                MessageUtils.sendMessage(player, "Rank: " + MessageUtils.cHighlight1 + (member.rank < 0 ? "unranked" : member.rank) + MessageUtils.cDefault + ", Score: " + MessageUtils.cHighlight2 + member.score + MessageUtils.cDefault + ", Wins: " + MessageUtils.cHighlight3 + member.wins, MessageUtils.MessageLevel.INFO);
            }
        });

        handler.<Player>register("idle", "Units will idle and not move even when attacked unlike when in defend mode", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE || State.gameState == State.GameState.GAMEOVER) return;

            Member member = memberLogic.getMember(player);

            if (member.state == Member.MemberState.ALIVE) {
                member.faction.team.rules().rtsAi = false;
                member.faction.team.rules().rtsMinSquad = Integer.MAX_VALUE;
                member.faction.team.rules().rtsMinWeight = Float.MAX_VALUE;
                for (Member m : member.faction.members) {
                    MessageUtils.sendMessage(m.player, "Units commanded to " + MessageUtils.cInfo + "IDLE" + MessageUtils.cDefault + "!", MessageUtils.MessageLevel.IDLE);
                }

                Groups.unit.each(u -> !u.isPlayer() && u.team.id == member.faction.team.id && !(u.controller() instanceof LogicAI), Unitc::resetController);
            }
        });

        handler.<Player>register("defend", "Defend your base but do not automatically attack enemy bases", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE || State.gameState == State.GameState.GAMEOVER) return;

            Member member = memberLogic.getMember(player);

            if (member.state == Member.MemberState.ALIVE) {
                member.faction.team.rules().rtsAi = true;
                member.faction.team.rules().rtsMinSquad = Integer.MAX_VALUE;
                member.faction.team.rules().rtsMinWeight = Float.MAX_VALUE;
                for (Member m : member.faction.members) {
                    MessageUtils.sendMessage(m.player, "Units commanded to " + MessageUtils.cWarning + "DEFEND" + MessageUtils.cDefault + "!", MessageUtils.MessageLevel.DEFEND);
                }

                Groups.unit.each(u -> !u.isPlayer() && u.team.id == member.faction.team.id && !(u.controller() instanceof LogicAI), Unitc::resetController);
            }
        });

        handler.<Player>register("attack", "[min-squad-size]", "Automatically attack enemy bases", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE || State.gameState == State.GameState.GAMEOVER) return;

            Member member = memberLogic.getMember(player);

            int minSquadSize = 1;
            if (args.length == 1) {
                try {
                    int minSquadSizeParam = Integer.parseInt(args[0]);

                    if (minSquadSizeParam > 0 && minSquadSizeParam <= 100) {
                        minSquadSize = minSquadSizeParam;
                    } else {
                        MessageUtils.sendMessage(player, "Min Squad Size must be in the range 1 and 100.", MessageUtils.MessageLevel.WARNING);
                    }
                } catch (NumberFormatException e) {
                    MessageUtils.sendMessage(player, "Min Squad Size must be a number in the range 1 and 100.", MessageUtils.MessageLevel.WARNING);
                }
            }

            if (member.state == Member.MemberState.ALIVE) {
                member.faction.team.rules().rtsAi = true;
                member.faction.team.rules().rtsMinSquad = minSquadSize;
                member.faction.team.rules().rtsMinWeight = 1.2f;
                for (Member m : member.faction.members) {
                    MessageUtils.sendMessage(m.player, "Units commanded to " + MessageUtils.cDanger + "ATTACK" + MessageUtils.cDefault + "!", MessageUtils.MessageLevel.ATTACK);
                }
            }
        });

        handler.<Player>register("eliminate", "Eliminates your team. (only if you are the team leader)", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE || State.gameState == State.GameState.GAMEOVER) return;

            Member member = memberLogic.getMember(player);

            if (member.state != Member.MemberState.ALIVE) {
                MessageUtils.sendMessage(player, "You currently do not have a team!", MessageUtils.MessageLevel.WARNING);
            } else if (member.faction.members.first() != member) {
                MessageUtils.sendMessage(player, "You are not the team leader!", MessageUtils.MessageLevel.WARNING);
            } else {
                factionLogic.destroyCores(member.faction);

                MessageUtils.sendMessage(player, "Your team was eliminated!", MessageUtils.MessageLevel.WARNING);
            }
        });

        MenuUtils.addMenu(10, player -> {
            Member requester = memberLogic.getMember(player);
            Seq<Member> leaders = factionLogic.getJoinableFactionLeaders(requester);

            String[][] options = new String[leaders.size + 1][1];
            int[][] links = new int[leaders.size + 1][1];
            MenuUtils.Handler[][] handlers = new MenuUtils.Handler[leaders.size + 1][1];

            for (int i = 0; i < leaders.size; i++) {
                Member leader = leaders.get(i);

                options[i][0] = MessageUtils.cPlayer + leader.player.name;
                links[i][0] = -1;
                handlers[i][0] = p -> {
                    if (joinRequests.contains(j -> j.requester == leader || j.answerer == leader)) {
                        MessageUtils.sendMessage(requester.player, "The player you requested to join already has a pending join request!", MessageUtils.MessageLevel.WARNING);
                    } else {
                        joinRequests.add(new JoinRequest(requester, leader));

                        Timer.schedule(() -> {
                            if (joinRequests.remove(j -> j.requester == requester)) {
                                MessageUtils.sendMessage(requester.player, "Your join request to " + MessageUtils.cPlayer + leader.player.name + MessageUtils.cDefault + " was not accepted!", MessageUtils.MessageLevel.INFO);
                                MessageUtils.sendMessage(leader.player, "Join request of " + MessageUtils.cPlayer + requester.player.name + MessageUtils.cDefault + " expired!", MessageUtils.MessageLevel.INFO);
                            }
                        }, 30);

                        MessageUtils.sendMessage(leader.player, MessageUtils.cPlayer + requester.player.name + MessageUtils.cDefault + " requested to join your team! Type " + MessageUtils.cHighlight2 + "/accept" + MessageUtils.cDefault + " to accept or " + MessageUtils.cDanger + "/deny" + MessageUtils.cDefault + " to deny the request!", MessageUtils.MessageLevel.INFO);
                        MessageUtils.sendMessage(requester.player, "Join request sent to " + MessageUtils.cPlayer + leader.player.name + MessageUtils.cDefault + "!", MessageUtils.MessageLevel.INFO);
                    }
                };
            }
            options[leaders.size][0] = MessageUtils.cDefault + "Cancel";
            links[leaders.size][0] = -1;
            handlers[leaders.size][0] = p -> {
            };

            return new MenuUtils.MenuContent(
                    "Join another Faction",
                    (leaders.size == 0 ? "There are currently no other factions you can request to join!" : "You can request to join the following faction leader:"),
                    options,
                    links,
                    handlers
            );
        });

        handler.<Player>register("join", "Request to join another faction.", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE || State.gameState == State.GameState.GAMEOVER) return;

            Member requester = memberLogic.getMember(player);

            if (joinRequests.contains(j -> j.requester == requester || j.answerer == requester)) {
                MessageUtils.sendMessage(requester.player, "You already have a pending join request!", MessageUtils.MessageLevel.WARNING);
                return;
            }

            MenuUtils.showMenu(10, player);
        });

        handler.<Player>register("accept", "Accept a pending join request.", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE || State.gameState == State.GameState.GAMEOVER) return;

            Member answerer = memberLogic.getMember(player);

            if (joinRequests.contains(j -> j.answerer == answerer)) {
                JoinRequest joinRequest = joinRequests.remove(joinRequests.indexOf(j -> j.answerer == answerer));

                Faction oldFaction = joinRequest.requester.faction;
                if (oldFaction != null) {
                    int oldFactionMembersSize = oldFaction.members.size;
                    if (oldFactionMembersSize == 1) {
                        factionLogic.destroyCores(oldFaction);
                    }
                    factionLogic.changeFaction(oldFaction, answerer.faction, joinRequest.requester);
                } else {
                    factionLogic.addToFaction(answerer.faction, joinRequest.requester);
                }

                MessageUtils.sendMessage(answerer.player, MessageUtils.cPlayer + joinRequest.requester.player.name + MessageUtils.cDefault + " is now part of your team!", MessageUtils.MessageLevel.INFO);
                MessageUtils.sendMessage(joinRequest.requester.player, "Your join request was accepted by " + MessageUtils.cPlayer + answerer.player.name + MessageUtils.cDefault + "!", MessageUtils.MessageLevel.INFO);
            } else {
                MessageUtils.sendMessage(answerer.player, "You do not have a pending join request", MessageUtils.MessageLevel.INFO);
            }
        });

        handler.<Player>register("deny", "Deny a pending join request.", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE || State.gameState == State.GameState.GAMEOVER) return;

            Member answerer = memberLogic.getMember(player);

            JoinRequest request = joinRequests.find(j -> j.answerer == answerer);
            if (joinRequests.remove(j -> j.answerer == answerer)) {
                MessageUtils.sendMessage(answerer.player, "Join request denied!", MessageUtils.MessageLevel.INFO);
                MessageUtils.sendMessage(request.requester.player, "Your join request was denied!", MessageUtils.MessageLevel.INFO);
            } else {
                MessageUtils.sendMessage(answerer.player, "You do not have a pending join request", MessageUtils.MessageLevel.INFO);
            }
        });

        handler.<Player>register("register", "[tag1] [tag2] [tag3] [tag4] [tag5]", "link your ingame and discord account to display your rank in discord", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE || State.gameState == State.GameState.GAMEOVER) return;

            Member member = memberLogic.getMember(player);

            if (args.length == 0) {
                MessageUtils.sendMessage(member.player, "Usage: /register username#0000", MessageUtils.MessageLevel.INFO);
                return;
            }

            String discordTag = String.join(" ", args);

            try {
                if (DiscordBot.checkIfExists(discordTag)) {
                    DiscordBot.register(discordTag, member);
                } else {
                    MessageUtils.sendMessage(member.player, "Couln´t find " + MessageUtils.cPlayer + discordTag + MessageUtils.cDefault + " on the Sectorized Discord, please check if you wrote your name and id correctly!", MessageUtils.MessageLevel.INFO);
                }
            } catch (IllegalArgumentException e) {
                MessageUtils.sendMessage(member.player, "Invalid Tag format, a Discord Tag looks like this: username#0000", MessageUtils.MessageLevel.INFO);
            }

        });
    }

    private class JoinRequest {
        public Member requester;
        public Member answerer;

        public JoinRequest(Member requester, Member answerer) {
            this.requester = requester;
            this.answerer = answerer;
        }
    }
}
