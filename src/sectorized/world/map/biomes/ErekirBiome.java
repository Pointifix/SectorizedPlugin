package sectorized.world.map.biomes;

import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.noise.Noise;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.content.Planets;
import mindustry.maps.filters.GenerateFilter;
import mindustry.maps.filters.OreFilter;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import sectorized.world.map.Biomes;
import sectorized.world.map.generator.SimplexGenerator2D;

import static mindustry.Vars.world;

public class ErekirBiome implements Biomes.Biome {
    private final SimplexGenerator2D generator;

    protected final Seq<GenerateFilter> ores;
    private final GenerateFilter.GenerateInput in;

    protected final Floor vent;

    private final int seed1 = Mathf.random(99999999);
    private final int seed2 = Mathf.random(99999999);
    private final int seed3 = Mathf.random(99999999);
    private final int seed4 = Mathf.random(99999999);
    private final int seed5 = Mathf.random(99999999);

    private final double wallThreshold = 0.6;

    private final double ventThreshold;

    private final Block wall;

    public ErekirBiome(SimplexGenerator2D generator, Floor vent, Block wall) {
        this(generator, vent, wall, 0.3);
    }

    public ErekirBiome(SimplexGenerator2D generator, Floor vent, Block wall, double ventThreshold) {
        this.generator = generator;
        this.vent = vent;
        this.wall = wall;
        this.ventThreshold = ventThreshold;

        ores = new Seq<>();
        Seq<Block> oreBlocks = Seq.with(Blocks.oreBeryllium, Blocks.oreTungsten, Blocks.oreCrystalThorium);
        for (Block block : oreBlocks) {
            OreFilter filter = new OreFilter();
            filter.threshold = block.asFloor().oreThreshold += 0.02f;
            filter.scl = block.asFloor().oreScale -= 1f;
            filter.ore = block;
            ores.add(filter);
        }
        ores.each(o -> {
            OreFilter oreFilter = ((OreFilter) o);
            if (oreFilter.ore == Blocks.oreBeryllium) {
                oreFilter.threshold -= 0.04f;
                oreFilter.scl -= 2f;
            }
        });
        ores.each(o -> {
            OreFilter oreFilter = ((OreFilter) o);
            if (oreFilter.ore == Blocks.oreTungsten) {
                oreFilter.threshold -= 0.04f;
                oreFilter.scl -= 2f;
            }
        });

        ores.each(GenerateFilter::randomize);

        in = new GenerateFilter.GenerateInput();
    }

    @Override
    public void sample(int x, int y, Tile tile, Biomes.Biome neighbor, double proximity) {
        Block floor = generator.sample(x, y);

        tile.setFloor((Floor) floor);

        for (GenerateFilter f : ores) {
            in.floor = floor;
            in.block = Blocks.air;
            in.overlay = Blocks.air;
            in.x = x;
            in.y = y;
            in.width = world.width();
            in.height = world.height();
            f.apply(in);
            if (in.overlay != Blocks.air) {
                tile.setOverlay(in.overlay);
            }
        }

        boolean wall = normalizeSimplex(Simplex.noise2d(seed1, 10, 0.4, 0.03, x, y)) < 0.5 && proximity < wallThreshold;
        if (wall) {
            boolean carbon = normalizeSimplex(Simplex.noise2d(seed2, 10, 0.4, 0.05, x, y)) < 0.45;

            tile.setBlock(carbon ? Blocks.carbonWall : this.wall);

            if (proximity > wallThreshold - 0.05) {
                if (carbon)
                    tile.setBlock(Blocks.graphiticWall);
                else if (normalizeSimplex(Simplex.noise2d(seed3, 10, 0.4, 0.05, x, y)) < 0.6)
                    tile.setOverlay(Blocks.wallOreBeryllium);
                else if (normalizeSimplex(Simplex.noise2d(seed4, 10, 0.4, 0.05, x, y)) < 0.4)
                    tile.setOverlay(Blocks.wallOreThorium);
                else if (normalizeSimplex(Simplex.noise2d(seed5, 10, 0.4, 0.05, x, y)) < 0.3)
                    tile.setOverlay(Blocks.wallOreTungsten);
            }
        } else if (vent != null && ((Floor) floor).hasSurface()) {
            int sx = (x / 5), sy = (y / 5);
            int mx = x % 5, my = y % 5;

            double n = Noise.rawNoise(sx * Math.E, sy * Math.E);
            int offset = n < 0.2 ? n < -0.2 ? -1 : 0 : 1;

            if (mx > offset && mx < 4 + offset && my > offset && my < 4 + offset && Noise.rawNoise(sx * Math.PI, sy * Math.PI) > ventThreshold) {
                tile.setFloor(vent);
            }
        }
    }

    @Override
    public String getPlanet() {
        return Planets.erekir.name;
    }

    private double normalizeSimplex(double value) {
        return (value - 0.1) * 1.25;
    }
}
