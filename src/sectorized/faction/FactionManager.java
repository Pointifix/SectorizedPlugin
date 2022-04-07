package sectorized.faction;

import arc.Events;
import arc.struct.Seq;
import arc.util.CommandHandler;
import arc.util.Timer;
import mindustry.content.UnitTypes;
import mindustry.entities.Units;
import mindustry.game.EventType;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.world.blocks.storage.CoreBlock;
import sectorized.Manager;
import sectorized.SectorizedEvents;
import sectorized.constant.Constants;
import sectorized.constant.MessageUtils;
import sectorized.constant.StartingBase;
import sectorized.constant.State;
import sectorized.faction.core.Faction;
import sectorized.faction.core.Member;
import sectorized.faction.logic.FactionLogic;
import sectorized.faction.logic.MemberLogic;
import sectorized.faction.persistence.RankingPersistence;

import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;

public class FactionManager implements Manager {
    private MemberLogic memberLogic;
    private FactionLogic factionLogic;

    private final RankingPersistence rankingPersistence = new RankingPersistence();

    private final Seq<JoinRequest> joinRequests = new Seq<>();

    @Override
    public void init() {
        Events.on(EventType.PlayerJoin.class, event -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            if (state.isPaused()) state.serverPaused = false;

            MessageUtils.sendWelcomeMessage(event.player);

            Member member = memberLogic.playerJoin(event.player);

            if (State.gameState == State.GameState.LOCKED)
                MessageUtils.sendMessage(member.player, "Spawning is currently [purple]locked" + MessageUtils.defaultColor + " because a player is dominating the game!", MessageUtils.MessageLevel.WARNING);
            if (member.state == Member.MemberState.WAITING) {
                Events.fire(new SectorizedEvents.NewMemberEvent(member));
            }
            if (member.state == Member.MemberState.ELIMINATED)
                MessageUtils.sendMessage(event.player, "You recently got eliminated, you can respawn [teal]5 minutes" + MessageUtils.defaultColor + " after you got eliminated by rejoining the server!", MessageUtils.MessageLevel.WARNING);
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

                int radius = Constants.radii.get((CoreBlock) coreBuild.block);
                Unit closestUnit = Units.closestEnemy(faction.team, event.tile.getX(), event.tile.getY(), tilesize * radius * 2, (unit) -> !unit.isPlayer() && unit.type() != UnitTypes.mono);

                Faction attacker = null;
                if (closestUnit != null) attacker = factionLogic.getFaction(closestUnit.team);

                Events.fire(new SectorizedEvents.CoreDestroyEvent(faction, coreBuild));

                if (faction.team.cores().isEmpty()) {
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

            MessageUtils.sendMessage("(Re)spawning is now [purple]locked" + MessageUtils.defaultColor + " because [gold]" + faction.members.first().player.name + MessageUtils.defaultColor + " is dominating the game!", MessageUtils.MessageLevel.WARNING);
        });

        Events.on(SectorizedEvents.NoTeamDominatingEvent.class, event -> {
            MessageUtils.sendMessage("(Re)spawning is now [forest]unlocked" + MessageUtils.defaultColor + " again!", MessageUtils.MessageLevel.INFO);
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
                MessageUtils.sendMessage(player, "Rank: [magenta]" + (member.rank < 0 ? "unranked" : member.rank) + MessageUtils.defaultColor + ", Score: [magenta]" + member.score + MessageUtils.defaultColor + ", Wins: [magenta]" + member.wins, MessageUtils.MessageLevel.INFO);
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
                        MessageUtils.sendMessage(requester.player, "Your join request to [white]" + answerer.player.name + MessageUtils.defaultColor + " was not accepted!", MessageUtils.MessageLevel.INFO);
                        MessageUtils.sendMessage(answerer.player, "Join request of [white]" + requester.player.name + MessageUtils.defaultColor + " expired!", MessageUtils.MessageLevel.INFO);
                    }
                }, 30);

                MessageUtils.sendMessage(answerer.player, "[white]" + requester.player.name + MessageUtils.defaultColor + " requested to join your team! Type [green]/accept" + MessageUtils.defaultColor + " to accept or [red]/deny" + MessageUtils.defaultColor + " to deny the request!", MessageUtils.MessageLevel.INFO);
                MessageUtils.sendMessage(requester.player, "Join request sent to [white]" + answerer.player.name + MessageUtils.defaultColor + "!", MessageUtils.MessageLevel.INFO);
            } catch (NumberFormatException e) {
                MessageUtils.sendMessage(player, "Invalid ID! Type [teal]/join" + MessageUtils.defaultColor + " without an ID to see all valid ID's.", MessageUtils.MessageLevel.WARNING);
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

                MessageUtils.sendMessage(answerer.player, "[white]" + joinRequest.requester.player.name + MessageUtils.defaultColor + " is now part of your team!", MessageUtils.MessageLevel.INFO);
                MessageUtils.sendMessage(joinRequest.requester.player, "Your join request was accepted by [white]" + answerer.player.name + MessageUtils.defaultColor + "!", MessageUtils.MessageLevel.INFO);
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
