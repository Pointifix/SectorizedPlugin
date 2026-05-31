package sectorized.constant;

public class RankIcons {
    public static String getRankIcon(int rank) {
        String icon = "[white]";

        int[] t = Config.c.discord.rankRoleThreshold;

        if (rank > t[0] || rank == -1) icon += "\uF7AD";
        else if (rank > t[1]) icon += "\uF7B2";
        else if (rank > t[2]) icon += "\uF7B0";
        else if (rank > t[3]) icon += "\uF7B6";
        else if (rank > t[4]) icon += "\uF7A8";
        else if (rank > t[5]) icon += "\uF7A7";
        else icon += "\uF7AB";

        return icon;
    }
}
