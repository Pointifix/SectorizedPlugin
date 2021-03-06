package sectorized.faction.logic;

import arc.Events;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Call;
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
        available.shuffle();
    }

    public Faction getFaction(Team team) {
        Faction faction = factions.find(f -> f.team == team);

        assert faction != null;

        return faction;
    }

    public Faction getNewFaction() {
        Faction faction = new Faction(available.pop(), State.time);

        factions.add(faction);

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

    public void removeFaction(Faction defender, Faction attacker) {
        factions.remove(defender);
        available.insert(0, defender.team);

        defender.members.each(m -> {
            if (m.player.unit() != null) m.player.unit().kill();
        });

        double timeSinceSpawned = State.time - defender.time;
        if (defender.maxCores <= 3 && timeSinceSpawned < 60 * 60 * 5) {
            defender.members.each(m -> {
                MessageUtils.sendMessage(m.player, "No points lost because you spawned less than [magenta]5 minutes" + MessageUtils.defaultColor + " ago", MessageUtils.MessageLevel.INFO);
            });

            if (attacker != null) {
                attacker.members.each(m -> {
                    MessageUtils.sendMessage(m.player, "No points gained because the faction you killed spawned less than [magenta]5 minutes" + MessageUtils.defaultColor + " ago", MessageUtils.MessageLevel.INFO);
                });
            }
        } else {
            if (attacker == null) persistence.calculateNewRankings(defender);
            else persistence.calculateNewRankings(attacker, defender);
        }

        Groups.unit.each(u -> u.team == defender.team, Unit::kill);

        if (attacker == null) {
            MessageUtils.sendMessage("[gold]" + defender.members.first().player.name + MessageUtils.defaultColor + " got eliminated!", MessageUtils.MessageLevel.ELIMINATION);
        } else {
            MessageUtils.sendMessage("[gold]" + defender.members.first().player.name + MessageUtils.defaultColor + " got eliminated by [red]" + attacker.members.first().player.name + MessageUtils.defaultColor + "!", MessageUtils.MessageLevel.ELIMINATION);
        }

        defender.members.each(m -> {
            m.state = Member.MemberState.ELIMINATED;
            m.player.team(Team.derelict);
            m.player.unit().kill();

            Timer.schedule(() -> {
                m.state = Member.MemberState.WAITING;

                if (m.online)
                    MessageUtils.sendMessage(m.player, "[teal]5 minutes " + MessageUtils.defaultColor + " have passed, you can reconnect to spawn again!", MessageUtils.MessageLevel.INFO);
            }, 60 * 5);
        });

        if (Vars.state.wave < 5) return;

        if (factions.size == 1 && State.gameState != State.GameState.GAMEOVER) {
            State.gameState = State.GameState.GAMEOVER;

            Member winner = factions.first().members.first();
            winner.wins++;
            winner.score += Vars.state.wave * 2;
            MessageUtils.sendMessage(winner.player, "You gained [magenta]" + Vars.state.wave * 2 + MessageUtils.defaultColor + " for winning the game!", MessageUtils.MessageLevel.INFO);
            persistence.setRanking(winner);

            Call.infoMessage("\uF7A7[red] GAME OVER [white]\uF7A7\n\n[gold]" + winner.player.name + "[white] won the game in [magenta]" + Vars.state.wave + "[white] waves!");

            DiscordBot.sendMessageWithScreenshot("**Game Over!** Player *" + Strings.stripColors(winner.player.name).substring(1).replace("@", "at") + "* won the game in " + Vars.state.wave + " waves.");

            Events.fire(new SectorizedEvents.RestartEvent("Game over! [gold]" + winner.player.name + MessageUtils.defaultColor + " won"));
        } else if (factions.size == 0 && State.gameState != State.GameState.GAMEOVER) {
            State.gameState = State.GameState.GAMEOVER;

            Call.infoMessage("\uF7A7[red] GAME OVER [white]\uF7A7\n\n[red]" + "crux[white] won the game in [magenta]" + Vars.state.wave + "[white] waves!");

            DiscordBot.sendMessageWithScreenshot("**Game Over!** Crux won the game in " + Vars.state.wave + " waves.");

            Events.fire(new SectorizedEvents.RestartEvent("Game over! [red]crux" + MessageUtils.defaultColor + " won."));
        }
    }

    public String getFactionLeaderInfo(Member member) {
        final StringBuilder info = new StringBuilder("You can request to join the following players:");

        factions.each(f -> !f.members.contains(member), f -> {
            info.append("\n[white]").append(f.members.first().player.name).append(MessageUtils.defaultColor).append(" - [violet]").append(f.members.first().player.id);
        });

        return info.toString();
    }
}
