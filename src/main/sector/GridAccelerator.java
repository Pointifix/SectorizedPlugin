package main.sector;

import java.util.*;

public class GridAccelerator {
    private final int cellSize;
    private final int width, height;
    private final ArrayList<Rectangle>[][] grid;

    public GridAccelerator(int width, int height, int cellSize) {
        this.width = width / cellSize + 1;
        this.height = height / cellSize + 1;
        this.grid = new ArrayList[this.width][this.height];
        this.cellSize = cellSize;

        Arrays.stream(this.grid).forEach(array -> Arrays.fill(array, new ArrayList<Rectangle>()));
    }

    public GridAccelerator(int width, int height) {
        this(width, height, 100);
    }

    @FunctionalInterface
    private interface Iterator {
        void apply(int x, int y);
    }

    private void iterate(Rectangle rectangle, Iterator iterator) {
        for (int x = Math.max(rectangle.minX / this.cellSize, 0); x <= Math.min(rectangle.maxX / this.cellSize, this.width - 1); x++) {
            for (int y = Math.max(rectangle.minY / this.cellSize, 0); y <= Math.min(rectangle.maxY / this.cellSize, this.height - 1); y++) {
                iterator.apply(x, y);
            }
        }
    }

    protected void addRectangle(Rectangle rectangle) {
        this.iterate(rectangle, (x, y) -> this.grid[x][y].add(rectangle));
    }

    protected void removeRectangle(Rectangle rectangle) {
        this.iterate(rectangle, (x, y) -> this.grid[x][y].remove(rectangle));
    }

    protected SubRectangle[] getIntersectingRectangles(Rectangle rectangle) {
        HashSet<SubRectangle> intersectingRectangles = new HashSet<>();

        LinkedHashSet<Rectangle> rectangles = new LinkedHashSet<>();
        this.iterate(rectangle, (x, y) -> rectangles.addAll(this.grid[x][y]));

        for (Rectangle rect : rectangles) {
            SubRectangle intersectingRectangle = rect.intersect(rectangle);
            if (intersectingRectangle != null) intersectingRectangles.add(intersectingRectangle);
        }

        SubRectangle[] subRectangles = intersectingRectangles.toArray(new SubRectangle[0]);
        Arrays.sort(subRectangles, Comparator.comparingInt(a -> -a.zIndex));

        return subRectangles;
    }
}
