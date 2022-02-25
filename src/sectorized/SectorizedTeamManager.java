package sectorized;

import arc.struct.Seq;
import arc.util.Log;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unitc;
import mindustry.net.Packets;
import mindustry.world.blocks.storage.CoreBlock;

import java.util.ArrayList;
import java.util.HashMap;

public class SectorizedTeamManager {
    private static Seq<Team> teams = new Seq<>(Team.all);

    private static HashMap<String, SectorizedTeam> uuidSectorizedTeamMap = new HashMap<>();
    private static HashMap<Integer, SectorizedTeam> teamSectorizedTeamMap = new HashMap<>();

    private static ArrayList<String> deadPlayers = new ArrayList<>();

    public static void reset() {
        teams = new Seq<>(Team.all);

        teams.remove(Team.derelict);
        teams.remove(Team.crux);
        teams.remove(Team.sharded);
        teams.shuffle();

        uuidSectorizedTeamMap = new HashMap<>();
        teamSectorizedTeamMap = new HashMap<>();

        deadPlayers = new ArrayList<>();
    }

    public static Team assignTeam(Player player) {
        Team team = teams.pop();
        player.team(team);
        SectorizedTeam sectorizedTeam = new SectorizedTeam(player);

        uuidSectorizedTeamMap.put(player.uuid(), sectorizedTeam);
        teamSectorizedTeamMap.put(team.id, sectorizedTeam);
        return team;
    }

    public static void killTeam(Team team) {
        SectorizedTeam sectorizedTeam = getTeam(team);
        teams.insert(0, team);

        Groups.unit.each((unit) -> unit.team() == team, (Unitc::kill));

        sectorizedTeam.players.forEach((player) -> {
            player.team(Team.derelict);
            deadPlayers.add(player.uuid());
            String uuid = player.uuid();
            Timer.schedule(() -> {
                deadPlayers.remove(uuid);
            }, 60 * 5);
            uuidSectorizedTeamMap.remove(player.uuid());
        });

        if (!sectorizedTeam.forceKilled)
            Call.sendMessage("[red]\u26A0 [white]Team of player " + sectorizedTeam.leaderName + " [white]has been eliminated!");

        teamSectorizedTeamMap.remove(team.id);

        if (teamSectorizedTeamMap.size() <= 1) {
            if (teamSectorizedTeamMap.size() == 1) {
                Call.infoMessage("[red]GAME OVER[white] \n\n" +
                        "Team of player " + uuidSectorizedTeamMap.values().iterator().next().players.get(0).name() + "[white] won");
            } else {
                Call.infoMessage("[red]GAME OVER[white] \n\n" +
                        "All teams died");
            }

            Timer.schedule(() -> {
                Log.info("Restarting server ...");
                Vars.netServer.kickAll(Packets.KickReason.serverRestarting);
                System.exit(1);
            }, 15);
        }
    }

    public static void forceKillTeam(Team team) {
        if (getTeam(team) != null) {
            getTeam(team).forceKilled = true;

            for (CoreBlock.CoreBuild core : team.cores().copy()) {
                core.kill();
            }
        }
    }

    public static boolean isDead(Player player) {
        return deadPlayers.contains(player.uuid());
    }

    public static boolean hasTeam(Player player) {
        return uuidSectorizedTeamMap.containsKey(player.uuid());
    }

    public static SectorizedTeam getTeam(Player player) {
        return uuidSectorizedTeamMap.get(player.uuid());
    }

    public static SectorizedTeam getTeam(Team team) {
        return teamSectorizedTeamMap.get(team.id);
    }

    public static boolean hasTeam(Team team) {
        return teamSectorizedTeamMap.containsKey(team.id);
    }
}
