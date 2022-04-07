package sectorized.faction.persistence;

import arc.struct.Seq;
import arc.util.Strings;
import com.google.gson.Gson;
import sectorized.constant.MessageUtils;
import sectorized.constant.RankIcons;
import sectorized.faction.core.Faction;
import sectorized.faction.core.Member;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.HashMap;

public class RankingPersistence {
    public final Seq<LeaderBoardEntry> leaderboard = new Seq<>();
    private final double k = 10;
    private final double offset = 10;
    private final HashMap<String, Member> rankingData = new HashMap<>();
    public String leaderboardText;
    private Connection connection = null;

    public RankingPersistence() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");

            DBUrl dbUrl = readConfig();

            connection = DriverManager.getConnection(dbUrl.url, dbUrl.user, dbUrl.password);

            getLeaderboard();
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getRanking(Member member) {
        if (rankingData.containsKey(member.player.uuid())) return;

        member.player.name = "." + member.player.name;

        if (connection != null) {
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM (SELECT *, ROW_NUMBER() OVER(PARTITION BY empty ORDER BY score DESC) AS rank FROM ranking) t WHERE uuid = ?");
                statement.setString(1, member.player.uuid());

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    member.rank = resultSet.getInt("rank");
                    member.score = resultSet.getInt("score");
                    member.wins = resultSet.getInt("wins");
                } else {
                    setRanking(member);
                }

                member.player.name = RankIcons.getRankIcon(member.rank) + member.player.name.substring(1);

                this.rankingData.put(member.player.uuid(), member);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void getLeaderboard() {
        if (connection != null) {
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT *, ROW_NUMBER() OVER(PARTITION BY empty ORDER BY score DESC) AS rank FROM ranking ORDER BY score DESC LIMIT 10");

                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    LeaderBoardEntry leaderBoardEntry = new LeaderBoardEntry(rs.getString("name"), rs.getInt("rank"), rs.getInt("score"), rs.getInt("wins"));

                    leaderboard.add(leaderBoardEntry);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        StringBuilder text = new StringBuilder("Leaderboard");
        for (LeaderBoardEntry entry : leaderboard) {
            text.append("\n")
                    .append(entry.rank)
                    .append(". [white]")
                    .append(entry.name)
                    .append(MessageUtils.defaultColor + ", Score: [magenta]")
                    .append(entry.score)
                    .append(MessageUtils.defaultColor + ", Wins: [magenta]")
                    .append(entry.wins)
                    .append(MessageUtils.defaultColor);
        }
        leaderboardText = text.toString();
    }

    public void setRanking(Member member) {
        if (connection != null) {
            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO ranking (uuid, name, score, wins) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE name = ?, score = ?, wins = ?");
                statement.setString(1, member.player.uuid());
                statement.setString(2, Strings.stripColors(member.player.name).substring(1));
                statement.setInt(3, member.score);
                statement.setInt(4, member.wins);
                statement.setString(5, Strings.stripColors(member.player.name).substring(1));
                statement.setInt(6, member.score);
                statement.setInt(7, member.wins);

                statement.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void calculateNewRankings(Faction winnerFaction, Faction looserFaction) {
        Member winner = winnerFaction.members.first();
        Member looser = looserFaction.members.first();

        double expectedValueWinner = 1 / (1 + Math.pow(10, ((double) Math.max(Math.min(looser.score - winner.score, 500), -500) / 500)));
        double expectedValueLooser = 1 / (1 + Math.pow(10, ((double) Math.max(Math.min(winner.score - looser.score, 500), -500) / 500)));

        int winnerScoreDiff = (int) ((looser.faction.maxCores * k + offset) * (1 - expectedValueWinner) * 2);
        int looserScoreDiff = (int) ((looser.faction.maxCores * k + offset) * (0 - expectedValueLooser));

        int win = winnerScoreDiff / winnerFaction.members.size;
        int loss = looserScoreDiff / looserFaction.members.size;

        winnerFaction.members.each(m -> {
            MessageUtils.sendMessage(m.player, "You gained [magenta]" + win + MessageUtils.defaultColor + " points", MessageUtils.MessageLevel.INFO);

            m.score += win;
            if (m.score < 0) m.score = 0;
            this.setRanking(m);
        });

        looserFaction.members.each(m -> {
            MessageUtils.sendMessage(m.player, "You lost [magenta]" + Math.abs(loss) + MessageUtils.defaultColor + " points", MessageUtils.MessageLevel.INFO);

            m.score += loss;
            if (m.score < 0) m.score = 0;
            this.setRanking(m);
        });
    }

    public void calculateNewRankings(Faction looserFaction) {
        Member looser = looserFaction.members.first();

        double expectedValueLooser = 1 / (1 + Math.pow(10, ((double) Math.max(Math.min(100 - looser.score, 500), -500) / 500)));
        int looserScoreDiff = (int) ((looser.faction.maxCores * k + offset) * (0 - expectedValueLooser));

        int loss = looserScoreDiff / looserFaction.members.size;

        looserFaction.members.each(m -> {
            MessageUtils.sendMessage(m.player, "You lost [magenta]" + Math.abs(loss) + MessageUtils.defaultColor + " points", MessageUtils.MessageLevel.INFO);

            m.score += loss;
            if (m.score < 0) m.score = 0;
            this.setRanking(m);
        });
    }

    private DBUrl readConfig() throws IOException {
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get("config/mods/config/dbUrl.json"));
        DBUrl dbUrl = gson.fromJson(reader, DBUrl.class);
        reader.close();
        return dbUrl;
    }

    private static class DBUrl {
        protected String url;
        protected String user;
        protected String password;

        public DBUrl(String url, String user, String password) {
            this.url = url;
            this.user = user;
            this.password = password;
        }
    }

    public class LeaderBoardEntry {
        public final String name;
        public final int rank;
        public final int score;
        public final int wins;

        public LeaderBoardEntry(String name, int rank, int score, int wins) {
            this.name = name;
            this.rank = rank;
            this.score = score;
            this.wins = wins;
        }
    }
}