package sectorized.world.map;

import arc.math.Mathf;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.world.Block;
import sectorized.world.map.generator.BlockG;
import sectorized.world.map.generator.Generator;
import sectorized.world.map.generator.SimplexGenerator2D;

public class RiversGenerator {
    private final SimplexGenerator2D generator;
    private final Simplex gapSimplex;

    public RiversGenerator() {
        generator = new SimplexGenerator2D(new Generator[][]{
                {BlockG.placeholder, BlockG.water, BlockG.deepwater, BlockG.water, BlockG.placeholder},
                {BlockG.water, BlockG.water, BlockG.water, BlockG.water, BlockG.water},
                {BlockG.deepwater, BlockG.water, BlockG.water, BlockG.deepwater, BlockG.deepwater},
                {BlockG.water, BlockG.water, BlockG.deepwater, BlockG.deepwater, BlockG.water},
                {BlockG.placeholder, BlockG.water, BlockG.deepwater, BlockG.water, BlockG.placeholder},
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
