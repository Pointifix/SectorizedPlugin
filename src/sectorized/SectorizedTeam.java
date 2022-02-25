package sectorized;

import mindustry.game.Team;
import mindustry.gen.Player;

import java.util.ArrayList;

public class SectorizedTeam {
    public Team team;
    public ArrayList<Player> players = new ArrayList<>();
    public int tilesCaptured = 0;
    public int cores = 0;
    public String leaderName = "";
    public boolean forceKilled = false;

    public SectorizedTeam(Player player) {
        this.team = player.team();
        this.leaderName = player.name();
        players.add(player);
    }

    public void addCore() {
        cores++;
    }

    public void removeCore() {
        cores--;

        if (cores == 0) SectorizedTeamManager.killTeam(team);
    }
}
