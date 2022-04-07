package sectorized.sector.core;

import sectorized.sector.core.objects.Rectangle;
import sectorized.sector.core.objects.SubRectangle;

import java.util.*;

import static mindustry.Vars.world;

public class GridAccelerator {
    private final static int cellSize = 100;

    private final ArrayList<Rectangle>[][] grid;

    public GridAccelerator() {
        grid = new ArrayList[world.width()][world.height()];

        Arrays.stream(grid).forEach(array -> Arrays.fill(array, new ArrayList<Rectangle>()));
    }

    private void iterate(Rectangle rectangle, Iterator iterator) {
        for (int x = Math.max(rectangle.minX / cellSize, 0); x <= Math.min(rectangle.maxX / cellSize, world.width() - 1); x++) {
            for (int y = Math.max(rectangle.minY / cellSize, 0); y <= Math.min(rectangle.maxY / cellSize, world.height() - 1); y++) {
                iterator.apply(x, y);
            }
        }
    }

    protected void addRectangle(Rectangle rectangle) {
        iterate(rectangle, (x, y) -> grid[x][y].add(rectangle));
    }

    protected void removeRectangle(Rectangle rectangle) {
        iterate(rectangle, (x, y) -> grid[x][y].remove(rectangle));
    }

    protected SubRectangle[] getIntersectingRectangles(Rectangle rectangle) {
        HashSet<SubRectangle> intersectingRectangles = new HashSet<>();

        LinkedHashSet<Rectangle> rectangles = new LinkedHashSet<>();
        iterate(rectangle, (x, y) -> rectangles.addAll(grid[x][y]));

        for (Rectangle rect : rectangles) {
            SubRectangle intersectingRectangle = rect.intersect(rectangle);
            if (intersectingRectangle != null) intersectingRectangles.add(intersectingRectangle);
        }

        SubRectangle[] subRectangles = intersectingRectangles.toArray(new SubRectangle[0]);
        Arrays.sort(subRectangles, Comparator.comparingInt(a -> -a.zIndex));

        return subRectangles;
    }

    @FunctionalInterface
    private interface Iterator {
        void apply(int x, int y);
    }
}
