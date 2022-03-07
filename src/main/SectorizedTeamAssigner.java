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
import java.util.concurrent.atomic.AtomicInteger;

import static mindustry.Vars.netServer;
import static mindustry.Vars.tilesize;

public class SectorizedTeamAssigner {
    private final Seq<Team> availableTeams = new Seq<>(Team.all);

    private final Seq<SectorizedTeam> teams = new Seq<>();
    private final ArrayList<String> eliminatedPlayers = new ArrayList<>();

    public SectorizedTeamAssigner() {
        availableTeams.remove(Team.derelict);
        availableTeams.remove(Team.crux);
        availableTeams.remove(Team.sharded);
        availableTeams.shuffle();
    }

    public boolean hasTeam(Player player) {
        return teams.contains(t -> t.playerUUIDs.contains(player.uuid()));
    }

    public Team getTeam(Player player) {
        return teams.find(t -> t.playerUUIDs.contains(player.uuid())).team;
    }

    public boolean isTeamLead(Player player) {
        return teams.contains(t -> t.getTeamLead().equals(player.uuid()));
    }

    public boolean isEliminated(Player player) {
        return eliminatedPlayers.contains(player.uuid());
    }

    public int getPlayerCount(Team team) {
        return teams.find(t -> t.team == team).playerUUIDs.size();
    }

    public void assignNewTeam(Player player) {
        Team team = availableTeams.pop();

        player.team(team);

        this.teams.add(new SectorizedTeam(team, player));
    }

    public void joinTeam(Player request, Player target) {
        if (hasTeam(request)) {
            SectorizedTeam requestTeam = teams.find(t -> t.playerUUIDs.contains(request.uuid()));

            this.forceEliminateTeam(requestTeam.team);
        }

        SectorizedTeam targetTeam = teams.find(t -> t.playerUUIDs.contains(target.uuid()));

        request.team(targetTeam.team);
        targetTeam.playerUUIDs.add(request.uuid());
    }

    public void eliminateTeam(Team team, Tile tile) {
        Unit closestUnit = Units.closestEnemy(team, tile.getX(), tile.getY(), tilesize * 20, (unit) -> true);

        SectorizedTeam sectorizedTeam = teams.find(t -> t.team == team);
        String leaderName = sectorizedTeam.leaderName;
        if (closestUnit != null) {
            if (closestUnit.team() == Team.crux) {
                MessageUtils.sendMessage("[gold]" + leaderName + "[lightgray] got eliminated by the crux waves!", MessageUtils.MessageLevel.ELIMINATION);
            } else {
                String eliminatorName = teams.find(t -> t.team == closestUnit.team).leaderName;
                MessageUtils.sendMessage("[gold]" + leaderName + "[lightgray] got eliminated by [red]" + eliminatorName + "[white]!", MessageUtils.MessageLevel.ELIMINATION);
            }
        } else {
            MessageUtils.sendMessage("[gold]" + leaderName + "[lightgray] got eliminated!", MessageUtils.MessageLevel.ELIMINATION);
        }

        Groups.unit.each((unit) -> unit.team() == team, (Unitc::kill));

        for (String uuid : sectorizedTeam.playerUUIDs) {
            eliminatedPlayers.add(uuid);
            Timer.schedule(() -> {
                eliminatedPlayers.remove(uuid);
                Player freePlayer = Groups.player.find(p -> p.uuid().equals(uuid));
                if (freePlayer != null) {
                    MessageUtils.sendMessage(freePlayer, "[teal]5 minutes [lightgray] have passed, you can reconnect to spawn again!", MessageUtils.MessageLevel.INFO);
                }
            }, 60 * 5);

            Player player = Groups.player.find(p -> p.uuid().equals(uuid));
            if (player != null) {
                player.team(Team.derelict);
            }
        }
        teams.remove(sectorizedTeam);
        availableTeams.insert(0, team);

        if (teams.size <= 1) {
            SectorizedPlugin.restarting = true;

            if (teams.size == 0) {
                Call.infoMessage("\uF7A7[red] GAME OVER [white]\uF7A7\n\n" +
                        "All teams got eliminated!");
            } else {
                Call.infoMessage("\uF7A7[red] GAME OVER [white]\uF7A7\n\n" +
                        "[gold]" + teams.get(0).leaderName + "[white] won!");
            }

            int seconds = 10;
            AtomicInteger countdown = new AtomicInteger(seconds);
            Timer.schedule(() -> {
                if (countdown.get() == 0) {
                    Log.info("Restarting server ...");
                    netServer.kickAll(Packets.KickReason.serverRestarting);
                    System.exit(1);
                }

                MessageUtils.sendMessage("Server restarting in [gold]" + (countdown.getAndDecrement()) + "[lightgray] seconds.", MessageUtils.MessageLevel.INFO);
            }, 5, 1, seconds);
        }
    }

    public void forceEliminateTeam(Team team) {
        for (CoreBlock.CoreBuild coreBuild : team.cores().copy()) {
            coreBuild.kill();
        }
    }

    private class SectorizedTeam {
        protected String leaderName;
        protected Team team;
        protected ArrayList<String> playerUUIDs = new ArrayList<>();

        public SectorizedTeam(Team team, Player player) {
            this.team = team;
            this.playerUUIDs.add(player.uuid());
            this.leaderName = player.name();
        }

        public String getTeamLead() {
            return playerUUIDs.get(0);
        }
    }
}
