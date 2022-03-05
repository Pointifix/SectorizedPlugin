package main;

import arc.struct.Seq;
import arc.util.Log;
import arc.util.Timer;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.net.Packets;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static mindustry.Vars.netServer;
import static mindustry.Vars.tilesize;

public class SectorizedTeamAssigner {
    private final Seq<Team> availableTeams = new Seq<>(Team.all);
    private final Seq<Team> usedTeams = new Seq<>();

    private final HashMap<String, Team> sectorizedTeamsUUIDMap = new HashMap<>();
    private final HashMap<Team, List<String>> sectorizedTeamsTeamMap = new HashMap<>();
    private final HashMap<Team, Player> sectorizedTeamsLeader = new HashMap<>();

    private final ArrayList<String> eliminatedPlayers = new ArrayList<>();

    public SectorizedTeamAssigner() {
        availableTeams.remove(Team.derelict);
        availableTeams.remove(Team.crux);
        availableTeams.remove(Team.sharded);
        availableTeams.shuffle();
    }

    public boolean hasTeam(Player player) {
        return sectorizedTeamsUUIDMap.get(player.uuid()) != null;
    }

    public Team getTeam(Player player) {
        return sectorizedTeamsUUIDMap.get(player.uuid());
    }

    public boolean isTeamLead(Player player) {
        return sectorizedTeamsTeamMap.get(player.team()).get(0).equals(player.uuid());
    }

    public boolean isEliminated(Player player) {
        return eliminatedPlayers.contains(player.uuid());
    }

    public void assignNewTeam(Player player) {
        Team team = availableTeams.pop();
        usedTeams.add(team);

        player.team(team);

        sectorizedTeamsUUIDMap.put(player.uuid(), team);
        sectorizedTeamsTeamMap.put(team, new ArrayList<>(Collections.singleton(player.uuid())));
        sectorizedTeamsLeader.put(team, player);
    }

    public void eliminateTeam(Team team, Tile tile) {
        Unit closestUnit = Units.closestEnemy(team, tile.getX(), tile.getY(), tilesize * 20, (unit) -> true);

        Player leader = sectorizedTeamsLeader.get(team);
        if (closestUnit != null) {
            if (closestUnit.team() == Team.crux)
                MessageUtils.sendMessage("Team of player [gold]" + leader.name() + "[white] got eliminated by the crux waves!", MessageUtils.MessageLevel.WARNING);
            else if (sectorizedTeamsLeader.get(closestUnit.team()) != null)
                MessageUtils.sendMessage("Team of player [gold]" + leader.name() + "[white] got eliminated by the team of player [gold]" + sectorizedTeamsLeader.get(closestUnit.team()).name() + "[white]!", MessageUtils.MessageLevel.WARNING);
        } else {
            MessageUtils.sendMessage("Team of player [gold]" + leader.name() + "[white] got eliminated!", MessageUtils.MessageLevel.WARNING);
        }

        Groups.unit.each((unit) -> unit.team() == team, (Unitc::kill));

        for (String uuid : sectorizedTeamsTeamMap.get(team)) {
            sectorizedTeamsUUIDMap.remove(uuid);

            eliminatedPlayers.add(uuid);
            Timer.schedule(() -> eliminatedPlayers.remove(uuid), 60 * 5);

            Player player = Groups.player.find(p -> p.uuid().equals(uuid));
            if (player != null) {
                player.team(Team.derelict);
            }
        }
        sectorizedTeamsTeamMap.remove(team);
        sectorizedTeamsLeader.remove(team);

        availableTeams.add(team);

        if (sectorizedTeamsTeamMap.size() <= 1) {
            SectorizedPlugin.restarting = true;

            if (sectorizedTeamsTeamMap.size() == 0) {
                Call.infoMessage("\uF7A7[red] GAME OVER [white]\uF7A7\n\n" +
                        "All teams got eliminated!");
            } else {
                Call.infoMessage("\uF7A7[red] GAME OVER [white]\uF7A7\n\n" +
                        "Team of player [gold]" + sectorizedTeamsLeader.values().iterator().next().name() + "[white] won!");
            }

            int seconds = 10;
            AtomicInteger countdown = new AtomicInteger(seconds);
            Timer.schedule(() -> {
                MessageUtils.sendMessage("Server restarting in [gold]" + (countdown.getAndDecrement()) + "[white] seconds.", MessageUtils.MessageLevel.INFO);

                if (countdown.get() == 0) {
                    Log.info("Restarting server ...");
                    netServer.kickAll(Packets.KickReason.serverRestarting);
                    System.exit(1);
                }
            }, 5, 1, seconds);
        }
    }

    public void forceEliminateTeam(Team team) {
        for (CoreBlock.CoreBuild coreBuild : team.cores()) {
            coreBuild.kill();
        }
    }
}
