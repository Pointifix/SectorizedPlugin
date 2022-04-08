package sectorized.world.map;

import arc.math.Mathf;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.world.Block;
import sectorized.world.map.generator.SimplexGenerator2D;

public class RiversGenerator {
    private final SimplexGenerator2D<Block> generator;
    private final Simplex gapSimplex;

    public RiversGenerator() {
        generator = new SimplexGenerator2D<>(new Block[][]{
                {null, Blocks.water, Blocks.deepwater, Blocks.water, null},
                {Blocks.water, Blocks.water, Blocks.water, Blocks.water, Blocks.water},
                {Blocks.deepwater, Blocks.water, Blocks.water, Blocks.deepwater, Blocks.deepwater},
                {Blocks.water, Blocks.water, Blocks.deepwater, Blocks.deepwater, Blocks.water},
                {null, Blocks.water, Blocks.deepwater, Blocks.water, null},
        }, 12, 0.5, 0.0008, 32, 12, 0.5, 0.0008, 32);

        gapSimplex = new Simplex(Mathf.random(99999999));
    }

    public Block sample(int x, int y) {
        Block sample = generator.sample(x, y);

        if (sample == Blocks.deepwater) {
            double gap = gapSimplex.octaveNoise2D(12, 0.5, 0.01f, x, y);

            if (gap < 0.4) sample = Blocks.water;
        }

        return sample;
    }
}
