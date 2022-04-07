package sectorized.sector.core.objects;

import java.util.Objects;

public class Border {
    public final int x, y, team1Id, team2Id;
    public final boolean borderX, borderY;

    public Border(int x, int y, boolean borderX, boolean borderY, int team1Id, int team2Id) {
        this.x = x;
        this.y = y;
        this.borderX = borderX;
        this.borderY = borderY;
        this.team1Id = team1Id;
        this.team2Id = team2Id;
    }

    public Border(int x, int y, boolean borderX, boolean borderY, int team1Id) {
        this(x, y, borderX, borderY, team1Id, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Border border = (Border) o;
        return x == border.x && y == border.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
