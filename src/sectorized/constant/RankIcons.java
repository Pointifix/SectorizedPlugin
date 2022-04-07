package sectorized.constant;

public class RankIcons {
    public static String getRankIcon(int rank) {
        String icon = "[white]";

        if (rank > 500 || rank == -1) icon += "\uF7AD";
        else if (rank > 200) icon += "\uF7B2";
        else if (rank > 100) icon += "\uF7B0";
        else if (rank > 50) icon += "\uF7B6";
        else if (rank > 25) icon += "\uF7A8";
        else if (rank > 10) icon += "\uF7A7";
        else icon += "\uF7AB";

        return icon;
    }
}
