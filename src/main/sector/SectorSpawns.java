package main.sector;

import arc.Events;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.world;

public class SectorSpawns {
    private final int width, height;

    private final int cellSize = 50;
    private Seq<Point2> spawnSeq = new Seq<>();
    private int spawningSearchStartIndex = 0;
    private GridAccelerator gridAccelerator;

    public SectorSpawns(int width, int height, GridAccelerator gridAccelerator) {
        this.width = width;
        this.height = height;
        this.gridAccelerator = gridAccelerator;

        for (int x = 0; x + cellSize <= this.width; x += cellSize) {
            for (int y = 0; y + cellSize <= this.height; y += cellSize) {
                int rx = Mathf.random(cellSize / 2) - cellSize / 4 + x + cellSize / 2;
                int ry = Mathf.random(cellSize / 2) - cellSize / 4 + y + cellSize / 2;

                BuildPlan buildPlan = new BuildPlan(rx, ry, 0, Blocks.multiplicativeReconstructor);
                if (buildPlan.placeable(Team.sharded) && world.tile(rx, ry).floor().hasSurface())
                    this.spawnSeq.add(new Point2(rx, ry));
            }
        }

        this.spawnSeq.sort((Point2 a, Point2 b) -> Math.max(Math.abs(this.width / 2 - b.x), Math.abs(this.width / 2 - b.y)) - Math.max(Math.abs(this.width / 2 - a.x), Math.abs(this.width / 2 - a.y)));

        for (int i = 0; i < this.spawnSeq.size - 1; i++) {
            Point2 a = this.spawnSeq.get(i);
            int r = Math.min(Mathf.random(this.spawnSeq.size / 10) + i, this.spawnSeq.size - 1);
            Point2 b = this.spawnSeq.get(r);

            this.spawnSeq.set(i, b);
            this.spawnSeq.set(r, a);
        }

        for (int i = 0; i < 3 && this.spawnSeq.size > 0; i++) {
            Tile tile = world.tile(this.spawnSeq.get(this.spawnSeq.size - 1).pack());
            this.spawnSeq.remove(this.spawnSeq.size - 1);
            tile.setOverlay(Blocks.spawn);
        }

        Events.fire(new EventType.WorldLoadEvent());
    }

    public Point2 getNextFreeSpawn() throws NoSpawnPointAvailableException {
        int size = SectorManager.radii.get((CoreBlock) Blocks.coreNucleus);

        for (int i = 0; i < spawnSeq.size; i++) {
            Point2 p = spawnSeq.get((i + spawningSearchStartIndex) % spawnSeq.size);

            SubRectangle[] subRectangles = this.gridAccelerator.getIntersectingRectangles(new Rectangle(Math.max(p.x - size, 0),
                    Math.max(p.y - size, 0),
                    Math.min(p.x + size, this.width - 1),
                    Math.min(p.y + size, this.height - 1),
                    -1,
                    -1));

            if (subRectangles.length == 0) {
                this.spawningSearchStartIndex = (this.spawningSearchStartIndex + 1) % this.spawnSeq.size;
                return p;
            }
        }

        throw new NoSpawnPointAvailableException();
    }

    public class NoSpawnPointAvailableException extends Exception {
        public NoSpawnPointAvailableException() {
            super("No spawn point is available at the moment");
        }
    }
}
