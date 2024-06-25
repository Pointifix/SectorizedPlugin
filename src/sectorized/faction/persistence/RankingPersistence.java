package sectorized.faction.persistence;

import arc.Core;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Strings;
import com.google.gson.Gson;
import sectorized.constant.Config;
import sectorized.constant.DiscordBot;
import sectorized.constant.MessageUtils;
import sectorized.constant.RankIcons;
import sectorized.faction.core.Faction;
import sectorized.faction.core.Member;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

public class RankingPersistence {
    private final Seq<LeaderBoardEntry> leaderboard = new Seq<>();
    private final double k = 10;
    private final double offset = 10;
    private Connection connection = null;

    public final String[] leaderboardTexts = new String[10];
    public int leaderBoardPages = 0;

    public RankingPersistence() {
        if (Config.c.databaseEnabled) {
            try {
                Class.forName("org.mariadb.jdbc.Driver");

                DBUrl dbUrl = readConfig();

                connection = DriverManager.getConnection(dbUrl.url, dbUrl.user, dbUrl.password);

                updateScoreDecay();
                getLeaderboard();
            } catch (SQLException | IOException | ClassNotFoundException e) {
                Log.err(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void updateScoreDecay() {
        if (Config.c.updateScoreDecay) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date today = calendar.getTime();
            calendar.add(Calendar.DATE, -1);
            Date yesterday = calendar.getTime();

            Date lastScoreDecayDate = new Date((long) Core.settings.get("lastScoreDecayDate", yesterday.getTime()));

            Core.settings.put("lastScoreDecayDate", today.getTime());

            if (lastScoreDecayDate.before(today)) {
                try {
                    PreparedStatement statement = connection.prepareStatement("UPDATE ranking SET score = score * 0.99 WHERE score > 100");
                    statement.executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateHallfOfFame() {
        if (Config.c.databaseEnabled) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date today = calendar.getTime();
            calendar.add(Calendar.DATE, -6);
            Date sixDaysAgo = calendar.getTime();
            calendar.add(Calendar.DATE, -1);
            Date sevenDaysAgo = calendar.getTime();

            Date lastHallOfFameDate = new Date((long) Core.settings.get("lastHallOfFameDate", sevenDaysAgo.getTime()));

            String zone = ZoneOffset.systemDefault().toString();
            String currentDate = DateTimeFormatter.ofPattern("uuuu/MM/dd - HH:mm").format(ZonedDateTime.now());

            StringBuilder text = new StringBuilder(":trophy: :regional_indicator_l: :regional_indicator_e: :regional_indicator_a: :regional_indicator_d: :regional_indicator_e: :regional_indicator_r: :regional_indicator_b: :regional_indicator_o: :regional_indicator_a: :regional_indicator_r: :regional_indicator_d: :trophy:\n");
            int i = 0;
            for (LeaderBoardEntry entry : leaderboard) {
                text.append("\n").append(entry.rank).append(" - ");

                String medal;
                switch (entry.rank) {
                    case 1:
                        medal = ":first_place:";
                        break;
                    case 2:
                        medal = ":second_place:";
                        break;
                    case 3:
                        medal = ":third_place:";
                        break;
                    default:
                        medal = ":medal:";
                }

                text.append(medal)
                        .append("**")
                        .append(entry.name)
                        .append("**")
                        .append(" - Score: ")
                        .append(entry.score)
                        .append(" - Wins: ")
                        .append(entry.wins)
                        .append(" - Losses: ")
                        .append(entry.losses);

                i++;
                if (i == 10) break;
            }
            text.append("\n\n:date: *").append(currentDate).append(" - ").append(zone).append("* :clock3:");

            if (lastHallOfFameDate.before(sixDaysAgo)) {
                Core.settings.put("lastHallOfFameDate", today.getTime());

                DiscordBot.sendMessageToHallOfFame(text.toString());
            } else {
                DiscordBot.editLastMessageInHallOfFame(text.toString());
            }
        }
    }

    public void closeConnection() {
        if (Config.c.databaseEnabled) {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void getRanking(Member member) {
        if (Config.c.databaseEnabled) {
            member.player.name = "." + member.player.name;

            if (connection != null) {
                try {
                    PreparedStatement statement = connection.prepareStatement("SELECT * FROM (SELECT *, ROW_NUMBER() OVER(PARTITION BY empty ORDER BY score DESC) AS rank FROM ranking) t WHERE uuid = ?");
                    statement.setString(1, member.player.uuid());

                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        member.score = resultSet.getInt("score");
                        member.wins = resultSet.getInt("wins");
                        member.losses = resultSet.getInt("losses");
                        member.rank = member.score > 0 ? resultSet.getInt("rank") : -1;
                        member.discordTag = resultSet.getString("discordTag");

                        member.ratio = (float) member.wins / Math.max((float) member.losses, 1);
                    } else {
                        setRanking(member);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            member.player.name = RankIcons.getRankIcon(member.rank) + member.player.name.substring(1);
        }
    }

    private void getLeaderboard() {
        if (Config.c.databaseEnabled) {
            if (connection != null) {
                try {
                    PreparedStatement statement = connection.prepareStatement("SELECT *, ROW_NUMBER() OVER(PARTITION BY empty ORDER BY score DESC) AS rank FROM ranking WHERE score > 0 LIMIT 100");

                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {
                        LeaderBoardEntry leaderBoardEntry = new LeaderBoardEntry(rs.getString("name"), rs.getInt("rank"), rs.getInt("score"), rs.getInt("wins"), rs.getInt("losses"));

                        leaderboard.add(leaderBoardEntry);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            Iterator<LeaderBoardEntry> it = leaderboard.iterator();

            int elements = 0;
            StringBuilder text = new StringBuilder();

            while (it.hasNext()) {
                RankingPersistence.LeaderBoardEntry entry = it.next();

                text.append(entry.rank)
                        .append(". [white]")
                        .append(entry.name)
                        .append(MessageUtils.cDefault + ", Score: " + MessageUtils.cHighlight2)
                        .append(entry.score)
                        .append(MessageUtils.cDefault + ", Wins: " + MessageUtils.cHighlight3)
                        .append(entry.wins)
                        .append(MessageUtils.cDefault + ", Losses: " + MessageUtils.cDanger)
                        .append(entry.losses)
                        .append(MessageUtils.cDefault)
                        .append("\n");

                elements++;

                if (elements == 10 || !it.hasNext()) {
                    leaderboardTexts[leaderBoardPages] = text.toString();
                    text = new StringBuilder();
                    leaderBoardPages++;
                    elements = 0;
                }
            }
        }
    }

    public void setRanking(Member member) {
        if (Config.c.databaseEnabled) {
            if (connection != null) {
                try {
                    PreparedStatement statement = connection.prepareStatement("INSERT INTO ranking (uuid, name, score, wins, losses, discordTag) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name = ?, score = ?, wins = ?, losses = ?, discordTag = ?");
                    statement.setString(1, member.player.uuid());
                    statement.setString(2, Strings.stripColors(member.player.name).substring(1));
                    statement.setInt(3, member.score);
                    statement.setInt(4, member.wins);
                    statement.setInt(5, member.losses);
                    statement.setString(6, member.discordTag);
                    statement.setString(7, Strings.stripColors(member.player.name).substring(1));
                    statement.setInt(8, member.score);
                    statement.setInt(9, member.wins);
                    statement.setInt(10, member.losses);
                    statement.setString(11, member.discordTag);

                    statement.executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void calculateNewRankings(Faction winnerFaction, Faction looserFaction) {
        if (Config.c.databaseEnabled) {
            Member winner = winnerFaction.members.copy().sort(Comparator.comparingInt(a -> -a.score)).first();
            Member looser = looserFaction.members.copy().sort(Comparator.comparingInt(a -> -a.score)).first();

            double expectedValueWinner = 1 / (1 + Math.pow(10, ((double) Math.max(Math.min(looser.score - winner.score, 2000), -2000) / 2000)));
            double expectedValueLooser = 1 / (1 + Math.pow(10, ((double) Math.max(Math.min(winner.score - looser.score, 2000), -2000) / 2000)));

            int winnerScoreDiff = (int) ((looser.faction.maxCores * k + offset) * (1 - expectedValueWinner) * 1.5);
            int looserScoreDiff = (int) ((looser.faction.maxCores * k + offset) * (0 - expectedValueLooser));

            int win = winnerScoreDiff / winnerFaction.members.size;
            int loss = looserScoreDiff / looserFaction.members.size;

            winnerFaction.members.each(m -> {
                MessageUtils.sendMessage(m.player, "You gained " + MessageUtils.cHighlight2 + win + MessageUtils.cDefault + " points", MessageUtils.MessageLevel.INFO);

                m.score += win;
                if (m.score < 0) m.score = 0;
                this.setRanking(m);
            });

            looserFaction.members.each(m -> {
                MessageUtils.sendMessage(m.player, "You lost " + MessageUtils.cDanger + Math.abs(loss) + MessageUtils.cDefault + " points", MessageUtils.MessageLevel.INFO);

                m.losses++;
                m.score += loss;
                if (m.score < 0) m.score = 0;
                this.setRanking(m);
            });
        }
    }

    public void calculateNewRankings(Faction looserFaction) {
        if (Config.c.databaseEnabled) {
            Member looser = looserFaction.members.first();

            double expectedValueLooser = 1 / (1 + Math.pow(10, ((double) Math.max(Math.min(-looser.score, 2000), -2000) / 2000)));
            int looserScoreDiff = (int) ((looser.faction.maxCores * k + offset) * (0 - expectedValueLooser));

            int loss = looserScoreDiff / looserFaction.members.size;

            looserFaction.members.each(m -> {
                MessageUtils.sendMessage(m.player, "You lost " + MessageUtils.cDanger + Math.abs(loss) + MessageUtils.cDefault + " points", MessageUtils.MessageLevel.INFO);

                m.losses++;
                m.score += loss;
                if (m.score < 0) m.score = 0;
                this.setRanking(m);
            });
        }
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

    private static class LeaderBoardEntry {
        public final String name;
        public final int rank;
        public final int score;
        public final int wins;
        public final int losses;

        public LeaderBoardEntry(String name, int rank, int score, int wins, int losses) {
            this.name = name;
            this.rank = rank;
            this.score = score;
            this.wins = wins;
            this.losses = losses;
        }
    }
}