package main.sector;

public class SubRectangle extends Rectangle {
    public final Rectangle parent;

    public SubRectangle(int minX, int minY, int maxX, int maxY, Rectangle parent) {
        super(minX, minY, maxX, maxY, parent.teamId, parent.zIndex);
        this.parent = parent;
    }

    public void iterate(Iterator iterator) {
        for (int x = this.minX; x <= this.maxX; x++) {
            for (int y = this.minY; y <= this.maxY; y++) {
                iterator.apply(x, y, x == parent.minX || x == parent.maxX, y == parent.minY || y == parent.maxY);
            }
        }
    }
}
