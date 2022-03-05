package main.sector;

public class Rectangle {
    protected final int minX, minY, maxX, maxY;
    protected final int teamId, zIndex;

    public Rectangle(int minX, int minY, int maxX, int maxY, int teamId, int zIndex) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.teamId = teamId;
        this.zIndex = zIndex;
    }

    public String toString() {
        return "Rectangle [minX=" + this.minX + ", minY=" + this.minY + ", maxX=" + this.maxX + ", maxY=" + this.maxY + ", zIndex=" + this.zIndex + "]";
    }

    @FunctionalInterface
    public interface Iterator {
        void apply(int x, int y, boolean borderX, boolean borderY);
    }

    public void iterate(Iterator iterator) {
        for (int x = this.minX; x <= this.maxX; x++) {
            for (int y = this.minY; y <= this.maxY; y++) {
                iterator.apply(x, y, x == this.minX || x == this.maxX, y == this.minY || y == this.maxY);
            }
        }
    }

    // https://stackoverflow.com/questions/306316/determine-if-two-rectangles-overlap-each-other#:~:text=It%20works%20by%20calculating%20the,the%20top%20and%20bottom%20borders.
    public SubRectangle intersect(Rectangle rectangle) {
        int minMaxX = Math.min(this.maxX, rectangle.maxX);
        int maxMinX = Math.max(this.minX, rectangle.minX);
        int minMaxY = Math.min(this.maxY, rectangle.maxY);
        int maxMinY = Math.max(this.minY, rectangle.minY);

        if (maxMinY <= minMaxY && maxMinX <= minMaxX) {
            return new SubRectangle(maxMinX, maxMinY, minMaxX, minMaxY, this);
        }

        return null;
    }
}
