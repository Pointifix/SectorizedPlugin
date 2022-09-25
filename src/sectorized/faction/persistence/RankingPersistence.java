package sectorized.faction.persistence;

import arc.Core;
import arc.struct.Seq;
import arc.util.Strings;
import com.google.gson.Gson;
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

public class RankingPersistence {
    public final Seq<LeaderBoardEntry> leaderboard = new Seq<>();
    private final double k = 10;
    private final double offset = 10;
    public String leaderboardText;
    private Connection connection = null;

    public RankingPersistence() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");

            DBUrl dbUrl = readConfig();

            connection = DriverManager.getConnection(dbUrl.url, dbUrl.user, dbUrl.password);

            // updateScoreDecay(); TODO UNCOMMENT
            getLeaderboard();
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateScoreDecay() {
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
        Core.settings.manualSave();

        if (lastScoreDecayDate.before(today)) {
            try {
                PreparedStatement statement = connection.prepareStatement("UPDATE ranking SET score = score * 0.99 WHERE score > 100");
                statement.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateHallfOfFame() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();
        calendar.add(Calendar.DATE, -2);
        Date twoDaysAgo = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date threeDaysAgo = calendar.getTime();

        Date lastHallOfFameDate = new Date((long) Core.settings.get("lastHallOfFameDate", threeDaysAgo.getTime()));

        String zone = ZoneOffset.systemDefault().toString();
        String currentDate = DateTimeFormatter.ofPattern("uuuu/MM/dd - HH:mm").format(ZonedDateTime.now());

        StringBuilder text = new StringBuilder(":trophy: :regional_indicator_l: :regional_indicator_e: :regional_indicator_a: :regional_indicator_d: :regional_indicator_e: :regional_indicator_r: :regional_indicator_b: :regional_indicator_o: :regional_indicator_a: :regional_indicator_r: :regional_indicator_d: :trophy:\n");
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
                    .append(entry.wins);
        }
        text.append("\n\n:date: *").append(currentDate).append(" - ").append(zone).append("* :clock3:");

        if (lastHallOfFameDate.before(twoDaysAgo)) {
            Core.settings.put("lastHallOfFameDate", today.getTime());
            Core.settings.manualSave();

            DiscordBot.sendMessageToHallOfFame(text.toString());
        } else {
            DiscordBot.editLastMessageInHallOfFame(text.toString());
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
                    member.discordTag = resultSet.getString("discordTag");
                } else {
                    setRanking(member);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        member.player.name = RankIcons.getRankIcon(member.rank) + member.player.name.substring(1);
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
                    .append(MessageUtils.cDefault + ", Score: " + MessageUtils.cHighlight2)
                    .append(entry.score)
                    .append(MessageUtils.cDefault + ", Wins: " + MessageUtils.cHighlight3)
                    .append(entry.wins)
                    .append(MessageUtils.cDefault);
        }
        leaderboardText = text.toString();
    }

    public void setRanking(Member member) {
        if (connection != null) {
            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO ranking (uuid, name, score, wins, discordTag) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name = ?, score = ?, wins = ?, discordTag = ?");
                statement.setString(1, member.player.uuid());
                statement.setString(2, Strings.stripColors(member.player.name).substring(1));
                statement.setInt(3, member.score);
                statement.setInt(4, member.wins);
                statement.setString(5, member.discordTag);
                statement.setString(6, Strings.stripColors(member.player.name).substring(1));
                statement.setInt(7, member.score);
                statement.setInt(8, member.wins);
                statement.setString(9, member.discordTag);

                statement.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void calculateNewRankings(Faction winnerFaction, Faction looserFaction) {
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
            MessageUtils.sendMessage(m.player, "You lost " + MessageUtils.cDanger + Math.abs(loss) + MessageUtils.cDefault + " points", MessageUtils.MessageLevel.INFO);

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

    private static class LeaderBoardEntry {
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