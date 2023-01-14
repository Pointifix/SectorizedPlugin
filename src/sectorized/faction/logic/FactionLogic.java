package sectorized.faction.logic;

import arc.Events;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.ai.types.FlyingAI;
import mindustry.ai.types.GroundAI;
import mindustry.core.GameState;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.world.blocks.storage.CoreBlock;
import sectorized.SectorizedEvents;
import sectorized.constant.DiscordBot;
import sectorized.constant.MessageUtils;
import sectorized.constant.State;
import sectorized.faction.core.Faction;
import sectorized.faction.core.Member;
import sectorized.faction.persistence.RankingPersistence;

import static mindustry.Vars.state;

public class FactionLogic {
    private final RankingPersistence persistence;

    private final Seq<Faction> factions = new Seq<>();

    private final Seq<Team> available = new Seq<>();

    public FactionLogic(RankingPersistence persistence) {
        this.persistence = persistence;

        available.addAll(Team.all);
        available.remove(Team.derelict);
        available.remove(Team.sharded);
        available.remove(Team.crux);
        available.remove(Team.malis);
        available.shuffle();

        Team.crux.data().unitCap = Integer.MAX_VALUE;

        Events.run(EventType.Trigger.update, () -> {
            if (state.rules.waves && state.rules.waveTimer) {
                if (!Vars.logic.isWaitingWave()) {
                    state.wavetime = Math.max(state.wavetime - Time.delta, 0);
                }
            }
        });

        Events.on(EventType.UnitSpawnEvent.class, event -> {
            event.unit.controller(!event.unit.isFlying() ? new GroundAI() : new FlyingAI());
        });

        for (Team team : available) {
            team.rules().aiCoreSpawn = false;
            team.rules().rtsAi = false;
            team.rules().rtsMinSquad = Integer.MAX_VALUE;
            team.rules().rtsMinWeight = Float.MAX_VALUE;
        }
    }

    public Faction getFaction(Team team) {
        Faction faction = factions.find(f -> f.team == team);

        return faction;
    }

    public Faction getNewFaction() {
        Faction faction = new Faction(available.pop(), State.time);

        factions.add(faction);
        state.set(GameState.State.playing);

        return faction;
    }

    public void destroyCores(Faction faction) {
        for (CoreBlock.CoreBuild core : faction.team.cores().copy()) {
            core.kill();
        }
    }

    public void changeFaction(Faction oldFaction, Faction newFaction, Member member) {
        oldFaction.removeMember(member);
        newFaction.addMember(member);

        member.player.unit().kill();
    }

    public void addToFaction(Faction newFaction, Member member) {
        newFaction.addMember(member);

        member.player.unit().kill();
    }

    public void removeFaction(Faction defender, Faction attacker, boolean fallback) {
        factions.remove(defender);
        if (factions.size == 0) state.set(GameState.State.paused);
        available.insert(0, defender.team);

        defender.members.each(m -> {
            if (m.player.unit() != null) m.player.unit().kill();
        });

        double timeSinceSpawned = State.time - defender.time;
        if (defender.maxCores <= 3 && timeSinceSpawned < 60 * 60 * 5) {
            defender.members.each(m -> {
                MessageUtils.sendMessage(m.player, "No points lost because you spawned less than " + MessageUtils.cInfo + "5 minutes" + MessageUtils.cDefault + " ago", MessageUtils.MessageLevel.INFO);
            });

            if (attacker != null) {
                attacker.members.each(m -> {
                    MessageUtils.sendMessage(m.player, "No points gained because the faction you killed spawned less than " + MessageUtils.cInfo + "5 minutes" + MessageUtils.cDefault + " ago", MessageUtils.MessageLevel.INFO);
                });
            }
        } else {
            if (attacker == null) persistence.calculateNewRankings(defender);
            else persistence.calculateNewRankings(attacker, defender);
        }

        Groups.unit.each(u -> u.team == defender.team, Unit::kill);

        if (defender.members.size > 0) {
            if (attacker == null) {
                MessageUtils.sendMessage(MessageUtils.cPlayer + defender.members.first().player.name + MessageUtils.cDefault + " got eliminated!", MessageUtils.MessageLevel.ELIMINATION);
            } else if (fallback) {
                MessageUtils.sendMessage(MessageUtils.cPlayer + defender.members.first().player.name + MessageUtils.cDefault + " got eliminated! Points awarded to " + MessageUtils.cDanger + attacker.members.first().player.name + MessageUtils.cDefault + "!", MessageUtils.MessageLevel.ELIMINATION);
            } else {
                MessageUtils.sendMessage(MessageUtils.cPlayer + defender.members.first().player.name + MessageUtils.cDefault + " got eliminated by " + MessageUtils.cDanger + attacker.members.first().player.name + MessageUtils.cDefault + "!", MessageUtils.MessageLevel.ELIMINATION);
            }
        }

        defender.members.each(m -> {
            if (m.faction == defender) {
                m.state = Member.MemberState.ELIMINATED;
                m.faction = null;
                m.player.team(Team.derelict);
                m.player.unit().kill();

                Timer.schedule(() -> {
                    if (m.faction == null) {
                        m.state = Member.MemberState.WAITING;

                        if (m.online) {
                            MessageUtils.sendMessage(m.player, MessageUtils.cInfo + "5 minutes" + MessageUtils.cDefault + " have passed, you can reconnect to spawn again!", MessageUtils.MessageLevel.INFO);
                        }
                    }
                }, 60 * 5);
            }
        });

        if (Vars.state.wave < 5) return;

        if (factions.size == 1 && State.gameState != State.GameState.GAMEOVER) {
            Member winner = factions.first().members.first();
            State.winner = winner;
            winner.wins++;
            persistence.setRanking(winner);

            DiscordBot.sendMessage("**Game Over!** Player *" + Strings.stripColors(winner.player.name).substring(1).replace("@", "at") + "* won the game in " + Vars.state.wave + " waves.");

            Events.fire(new SectorizedEvents.RestartEvent("Game over! " + MessageUtils.cPlayer + winner.player.name + MessageUtils.cDefault + " won"));
        } else if (factions.size == 0 && State.gameState != State.GameState.GAMEOVER) {
            DiscordBot.sendMessage("**Game Over!** Crux won the game in " + Vars.state.wave + " waves.");

            Events.fire(new SectorizedEvents.RestartEvent("Game over! " + MessageUtils.cDanger + "crux" + MessageUtils.cDefault + " won."));
        }
    }

    public Seq<Member> getJoinableFactionLeaders(Member member) {
        Seq<Member> leaders = new Seq<>();

        factions.each(f -> !f.members.contains(member), f -> {
            leaders.add(f.members.first());
        });

        return leaders;
    }

    public boolean isFactionLeaders(Member possibleLeader, Member requester) {
        return factions.contains(f -> f.members.first().equals(possibleLeader) && !f.members.first().equals(requester));
    }
}
