package sectorized.sector.core;

import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.storage.CoreBlock;
import sectorized.constant.Constants;
import sectorized.sector.core.objects.Rectangle;
import sectorized.sector.core.objects.SubRectangle;

import static mindustry.Vars.*;

public class SectorSpawns {
    private final Seq<Point2> spawnSeq = new Seq<>();
    private final GridAccelerator gridAccelerator;
    private int spawningSearchStartIndex = 0;

    public SectorSpawns(GridAccelerator gridAccelerator) {
        this.gridAccelerator = gridAccelerator;

        int size = Constants.radii.get((CoreBlock) Blocks.coreNucleus);

        for (int x = 0; x + Constants.spawnCellSize <= world.width(); x += Constants.spawnCellSize) {
            for (int y = 0; y + Constants.spawnCellSize <= world.height(); y += Constants.spawnCellSize) {
                int rx = Mathf.random(Constants.spawnCellSize / 2) - Constants.spawnCellSize / 4 + x + Constants.spawnCellSize / 2;
                int ry = Mathf.random(Constants.spawnCellSize / 2) - Constants.spawnCellSize / 4 + y + Constants.spawnCellSize / 2;

                BuildPlan buildPlan = new BuildPlan(rx, ry, 0, Blocks.multiplicativeReconstructor);
                if (!buildPlan.placeable(Team.sharded))
                    continue;

                int hasSurfaceCount = 0;
                for (int i = Math.max(rx - size, 0); i < Math.min(rx + size, world.width() - 1); i += 10) {
                    for (int j = Math.max(ry - size, 0); j < Math.min(ry + size, world.height() - 1); j += 10) {
                        if (world.tile(i, j).floor().hasSurface()) hasSurfaceCount++;
                    }
                }

                if (hasSurfaceCount > size * size / 300) {
                    spawnSeq.add(new Point2(rx, ry));
                }
            }
        }

        int radius = (int) (state.rules.dropZoneRadius / tilesize);
        Simplex simplex = new Simplex(Mathf.random(100_000));

        int offset = Mathf.random(5) + 1;
        for (int i = 0; i < 4; i++) {
            Point2 center = new Point2(i % 4 >= 2 ? world.width() : 0, i % 2 == 1 ? world.height() : 0);

            spawnSeq.sort((Point2 a, Point2 b) -> (Math.abs(center.x - b.x) + Math.abs(center.y - b.y)) - (Math.abs(center.x - a.x) + Math.abs(center.y - a.y)));

            Tile tile = world.tile(spawnSeq.get(offset).pack());
            spawnSeq.remove(offset);

            for (int x = Math.max(0, tile.x - radius); x <= Math.min(world.width() - 1, tile.x + radius); x++) {
                for (int y = Math.max(0, tile.y - radius); y <= Math.min(world.height() - 1, tile.y + radius); y++) {
                    double t = simplex.octaveNoise2D(10, 0.5, 0.1, x, y);

                    if (world.tile(x, y).floor().hasSurface() ? t < 0.6 : t < 0.3) {
                        int dist = (int) tile.dst(x * tilesize, y * tilesize) / tilesize;

                        if (dist < radius) {
                            int b = Mathf.random(4);

                            switch (b) {
                                case 0:
                                    world.tile(x, y).setFloor((Floor) Blocks.darkPanel1);
                                    break;
                                case 1:
                                    world.tile(x, y).setFloor((Floor) Blocks.darkPanel2);
                                    break;
                                case 2:
                                    world.tile(x, y).setFloor((Floor) Blocks.darkPanel3);
                                    break;
                                case 3:
                                    world.tile(x, y).setFloor((Floor) Blocks.darkPanel4);
                                    break;
                                case 4:
                                    world.tile(x, y).setFloor((Floor) Blocks.darkPanel5);
                                    break;
                            }
                        } else if (dist == radius) {
                            world.tile(x, y).setFloor((Floor) Blocks.metalFloor5);
                        }
                    }
                }
            }

            tile.setOverlay(Blocks.spawn);
        }

        spawnSeq.sort((Point2 a, Point2 b) -> Math.max(Math.abs(world.width() / 2 - b.x), Math.abs(world.width() / 2 - b.y)) - Math.max(Math.abs(world.width() / 2 - a.x), Math.abs(world.width() / 2 - a.y)));

        for (int i = 0; i < spawnSeq.size - 1; i++) {
            Point2 a = spawnSeq.get(i);
            int r = Math.min(Mathf.random(spawnSeq.size / 10) + i, spawnSeq.size - 1);
            Point2 b = spawnSeq.get(r);

            spawnSeq.set(i, b);
            spawnSeq.set(r, a);
        }
    }

    public Point2 getNextFreeSpawn() throws NoSpawnPointAvailableException {
        int size = Constants.radii.get((CoreBlock) Blocks.coreNucleus);

        for (int i = 0; i < spawnSeq.size; i++) {
            Point2 p = spawnSeq.get((i + spawningSearchStartIndex) % spawnSeq.size);

            SubRectangle[] subRectangles = gridAccelerator.getIntersectingRectangles(new Rectangle(Math.max(p.x - size, 0),
                    Math.max(p.y - size, 0),
                    Math.min(p.x + size, world.width() - 1),
                    Math.min(p.y + size, world.height() - 1),
                    -1,
                    -1));

            if (subRectangles.length == 0) {
                spawningSearchStartIndex = (i + spawningSearchStartIndex + 1) % this.spawnSeq.size;
                return p;
            }
        }

        throw new NoSpawnPointAvailableException();
    }

    public static class NoSpawnPointAvailableException extends Exception {
        public NoSpawnPointAvailableException() {
            super("No spawn point is available at the moment");
        }
    }
}
