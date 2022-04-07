package sectorized.sector.core.objects;

public class Rectangle {
    public final int minX, minY, maxX, maxY;
    public final int teamId, zIndex;

    public Rectangle(int minX, int minY, int maxX, int maxY, int teamId, int zIndex) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.teamId = teamId;
        this.zIndex = zIndex;
    }

    public void iterate(Iterator iterator) {
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                iterator.apply(x, y, x == minX || x == maxX, y == minY || y == maxY);
            }
        }
    }

    public SubRectangle intersect(Rectangle rectangle) {
        int minMaxX = Math.min(maxX, rectangle.maxX);
        int maxMinX = Math.max(minX, rectangle.minX);
        int minMaxY = Math.min(maxY, rectangle.maxY);
        int maxMinY = Math.max(minY, rectangle.minY);

        if (maxMinY <= minMaxY && maxMinX <= minMaxX) {
            return new SubRectangle(maxMinX, maxMinY, minMaxX, minMaxY, this);
        }

        return null;
    }

    @FunctionalInterface
    public interface Iterator {
        void apply(int x, int y, boolean borderX, boolean borderY);
    }
}
