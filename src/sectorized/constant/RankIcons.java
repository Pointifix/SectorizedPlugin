package sectorized.constant;

public class RankIcons {
    public static String getRankIcon(int rank) {
        String icon = "[white]";

        int t1 = Config.c.getInt("discord.rankRoleThreshold.1");
        int t2 = Config.c.getInt("discord.rankRoleThreshold.2");
        int t3 = Config.c.getInt("discord.rankRoleThreshold.3");
        int t4 = Config.c.getInt("discord.rankRoleThreshold.4");
        int t5 = Config.c.getInt("discord.rankRoleThreshold.5");
        int t6 = Config.c.getInt("discord.rankRoleThreshold.6");

        if (rank > t1 || rank == -1) icon += "\uF7AD";
        else if (rank > t2) icon += "\uF7B2";
        else if (rank > t3) icon += "\uF7B0";
        else if (rank > t4) icon += "\uF7B6";
        else if (rank > t5) icon += "\uF7A8";
        else if (rank > t6) icon += "\uF7A7";
        else icon += "\uF7AB";

        return icon;
    }
}
