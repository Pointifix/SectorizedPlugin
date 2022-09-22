package sectorized.faction;

import arc.Events;
import arc.struct.Seq;
import arc.util.CommandHandler;
import arc.util.Timer;
import mindustry.content.UnitTypes;
import mindustry.entities.Units;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.world.blocks.storage.CoreBlock;
import sectorized.Manager;
import sectorized.SectorizedEvents;
import sectorized.constant.DiscordBot;
import sectorized.constant.MessageUtils;
import sectorized.constant.StartingBase;
import sectorized.constant.State;
import sectorized.faction.core.Faction;
import sectorized.faction.core.Member;
import sectorized.faction.logic.FactionLogic;
import sectorized.faction.logic.MemberLogic;
import sectorized.faction.persistence.RankingPersistence;

import java.util.Arrays;
import java.util.Comparator;

import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;

public class FactionManager implements Manager {
    private MemberLogic memberLogic;
    private FactionLogic factionLogic;

    private final RankingPersistence rankingPersistence = new RankingPersistence();

    private final Seq<JoinRequest> joinRequests = new Seq<>();

    @Override
    public void init() {
        rankingPersistence.updateHallfOfFame();

        Events.on(EventType.PlayerJoin.class, event -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            if (state.isPaused()) state.serverPaused = false;

            MessageUtils.sendWelcomeMessage(event.player);

            Member member = memberLogic.playerJoin(event.player);

            if (State.gameState == State.GameState.LOCKED) {
                MessageUtils.sendMessage(member.player, "Spawning is currently " + MessageUtils.cInfo + "locked" + MessageUtils.cDefault + " because a player is dominating the game!", MessageUtils.MessageLevel.WARNING);
            } else if (member.state == Member.MemberState.WAITING) {
                Events.fire(new SectorizedEvents.NewMemberEvent(member));
            } else if (member.state == Member.MemberState.ELIMINATED) {
                MessageUtils.sendMessage(event.player, "You recently got eliminated, you can respawn " + MessageUtils.cInfo + "5 minutes" + MessageUtils.cDefault + " after you got eliminated by rejoining the server!", MessageUtils.MessageLevel.WARNING);
            }
        });

        Events.on(SectorizedEvents.MemberSpawnedEvent.class, event -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            Faction faction = factionLogic.getNewFaction();
            faction.addMember(event.member);
            faction.maxCores = 1;

            StartingBase.spawnStartingBase(event.spawnPoint.x, event.spawnPoint.y, faction.team);
        });

        Events.on(SectorizedEvents.NoSpawnPointAvailableEvent.class, event -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            MessageUtils.sendMessage(event.member.player, "No spawn point available, please wait!", MessageUtils.MessageLevel.WARNING);
        });

        Events.on(EventType.BlockDestroyEvent.class, event -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            if (event.tile.build instanceof CoreBlock.CoreBuild) {
                CoreBlock.CoreBuild coreBuild = (CoreBlock.CoreBuild) event.tile.build;
                Faction faction = factionLogic.getFaction(event.tile.team());

                Events.fire(new SectorizedEvents.CoreDestroyEvent(faction, coreBuild));

                if (faction.team.cores().isEmpty()) {
                    int radius = 60 * tilesize;

                    final Unit[] mostHealthUnit = {null};
                    Units.nearbyEnemies(faction.team, event.tile.getX() - radius, event.tile.getY() - radius, radius * 2, radius * 2, unit -> {
                        if (!unit.isPlayer() && unit.type() != UnitTypes.mono && unit.team != Team.crux) {
                            if (mostHealthUnit[0] == null) mostHealthUnit[0] = unit;
                            else if (unit.health() > mostHealthUnit[0].health()) mostHealthUnit[0] = unit;
                        }
                    });

                    Faction attacker = null;
                    if (mostHealthUnit[0] != null) attacker = factionLogic.getFaction(mostHealthUnit[0].team);
                    else if (faction.maxCores >= 5) {
                        Team dominatingTeam;
                        Team[] teams = Team.all.clone();
                        Arrays.sort(teams, Comparator.comparingInt(t -> -t.cores().size));
                        dominatingTeam = teams[0];
                        Faction dominatingFaction = factionLogic.getFaction(dominatingTeam);

                        if (dominatingFaction.maxCores >= 5) attacker = dominatingFaction;
                    }

                    Events.fire(new SectorizedEvents.EliminateFactionEvent(faction, attacker));
                }
            }
        });

        Events.on(SectorizedEvents.EliminateFactionEvent.class, event -> {
            factionLogic.removeFaction(event.defender, event.attacker);
        });

        Events.on(SectorizedEvents.CoreBuildEvent.class, event -> {
            Faction faction = factionLogic.getFaction(event.tile.team());

            faction.maxCores = Math.max(faction.team.cores().size, faction.maxCores);
        });

        Events.on(EventType.PlayerLeave.class, event -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            if (!state.isPaused() && Groups.player.size() == 0) state.serverPaused = true;

            Member member = memberLogic.playerLeave(event.player);

            if (member.faction != null) {
                if (!member.faction.members.contains(m -> m.online)) {
                    Timer.schedule(() -> {
                        if (!member.faction.members.contains(m -> m.online)) factionLogic.destroyCores(member.faction);
                    }, 30 * member.faction.maxCores);
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
            MessageUtils.sendMessage("(Re)spawning is now " + MessageUtils.cInfo + "unlocked" + MessageUtils.cDefault + " again!", MessageUtils.MessageLevel.INFO);
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
        handler.<Player>register("leaderboard", "Displays the current leaderboard", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            MessageUtils.sendMessage(player, rankingPersistence.leaderboardText, MessageUtils.MessageLevel.INFO);
        });

        handler.<Player>register("score", "Displays your score and wins", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            Member member = memberLogic.getMember(player);

            if (member.rank == 0) {
                MessageUtils.sendMessage(player, "You are not ranked yet. Play a game to get ranked!", MessageUtils.MessageLevel.INFO);
            } else {
                MessageUtils.sendMessage(player, "Rank: " + MessageUtils.cHighlight1 + (member.rank < 0 ? "unranked" : member.rank) + MessageUtils.cDefault + ", Score: " + MessageUtils.cHighlight2 + member.score + MessageUtils.cDefault + ", Wins: " + MessageUtils.cHighlight3 + member.wins, MessageUtils.MessageLevel.INFO);
            }
        });

        handler.<Player>register("eliminate", "Eliminates your team. (only if you are the team leader)", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE) return;

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

        handler.<Player>register("join", "[id]", "Request to join another faction.", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            Member requester = memberLogic.getMember(player);

            if (args.length == 0) {
                MessageUtils.sendMessage(requester.player, factionLogic.getFactionLeaderInfo(requester), MessageUtils.MessageLevel.INFO);
                return;
            }

            if (joinRequests.contains(j -> j.requester == requester || j.answerer == requester)) {
                MessageUtils.sendMessage(requester.player, "You already have a pending join request!", MessageUtils.MessageLevel.WARNING);
                return;
            }

            try {
                int id = Integer.parseInt(args[0]);

                Member answerer = memberLogic.getMember(Groups.player.getByID(id));

                if (answerer.faction == null || answerer.faction.members.contains(requester) || answerer.faction.members.first() != answerer) {
                    throw new NumberFormatException();
                }

                if (joinRequests.contains(j -> j.requester == answerer || j.answerer == answerer)) {
                    MessageUtils.sendMessage(requester.player, "The player you requested to join already has a pending join request!", MessageUtils.MessageLevel.WARNING);
                    return;
                }

                joinRequests.add(new JoinRequest(requester, answerer));

                Timer.schedule(() -> {
                    if (joinRequests.remove(j -> j.requester == requester)) {
                        MessageUtils.sendMessage(requester.player, "Your join request to " + MessageUtils.cPlayer + answerer.player.name + MessageUtils.cDefault + " was not accepted!", MessageUtils.MessageLevel.INFO);
                        MessageUtils.sendMessage(answerer.player, "Join request of " + MessageUtils.cPlayer + requester.player.name + MessageUtils.cDefault + " expired!", MessageUtils.MessageLevel.INFO);
                    }
                }, 30);

                MessageUtils.sendMessage(answerer.player, MessageUtils.cPlayer + requester.player.name + MessageUtils.cDefault + " requested to join your team! Type " + MessageUtils.cHighlight2 + "/accept" + MessageUtils.cDefault + " to accept or " + MessageUtils.cDanger + "/deny" + MessageUtils.cDefault + " to deny the request!", MessageUtils.MessageLevel.INFO);
                MessageUtils.sendMessage(requester.player, "Join request sent to " + MessageUtils.cPlayer + answerer.player.name + MessageUtils.cDefault + "!", MessageUtils.MessageLevel.INFO);
            } catch (NumberFormatException e) {
                MessageUtils.sendMessage(player, "Invalid ID! Type " + MessageUtils.cHighlight3 + "/join" + MessageUtils.cDefault + " without an ID to see all valid ID's.", MessageUtils.MessageLevel.WARNING);
            }
        });

        handler.<Player>register("accept", "Accept a pending join request.", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            Member answerer = memberLogic.getMember(player);

            if (joinRequests.contains(j -> j.answerer == answerer)) {
                JoinRequest joinRequest = joinRequests.remove(joinRequests.indexOf(j -> j.answerer == answerer));

                if (joinRequest.requester.faction.members.size == 1) {
                    factionLogic.destroyCores(joinRequest.requester.faction);
                }

                factionLogic.changeFaction(joinRequest.requester.faction, answerer.faction, joinRequest.requester);

                MessageUtils.sendMessage(answerer.player, MessageUtils.cPlayer + joinRequest.requester.player.name + MessageUtils.cDefault + " is now part of your team!", MessageUtils.MessageLevel.INFO);
                MessageUtils.sendMessage(joinRequest.requester.player, "Your join request was accepted by " + MessageUtils.cPlayer + answerer.player.name + MessageUtils.cDefault + "!", MessageUtils.MessageLevel.INFO);
            } else {
                MessageUtils.sendMessage(answerer.player, "You do not have a pending join request", MessageUtils.MessageLevel.INFO);
            }
        });

        handler.<Player>register("deny", "Deny a pending join request.", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            Member answerer = memberLogic.getMember(player);

            JoinRequest request = joinRequests.find(j -> j.answerer == answerer);
            if (joinRequests.remove(j -> j.answerer == answerer)) {
                MessageUtils.sendMessage(answerer.player, "Join request denied!", MessageUtils.MessageLevel.INFO);
                MessageUtils.sendMessage(request.requester.player, "Your join request was denied!", MessageUtils.MessageLevel.INFO);
            } else {
                MessageUtils.sendMessage(answerer.player, "You do not have a pending join request", MessageUtils.MessageLevel.INFO);
            }
        });

        handler.<Player>register("register", "[tag]", "link your ingame and discord account to display your rank in discord", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            Member member = memberLogic.getMember(player);

            if (args.length == 0) {
                MessageUtils.sendMessage(member.player, "Usage: /register username#0000", MessageUtils.MessageLevel.INFO);
                return;
            }

            String discordTag = args[0];

            if (DiscordBot.checkIfExists(discordTag)) {
                DiscordBot.register(discordTag, member);
            } else {
                MessageUtils.sendMessage(member.player, "Couln´t find " + MessageUtils.cPlayer + discordTag + MessageUtils.cDefault + " on the Sectorized Discord, please check if you wrote your name and id correctly!", MessageUtils.MessageLevel.INFO);
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
