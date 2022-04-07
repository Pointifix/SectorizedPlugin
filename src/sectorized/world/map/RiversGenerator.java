package sectorized.world.map;

import mindustry.content.Blocks;
import mindustry.world.Block;
import sectorized.world.map.generator.SimplexGenerator2D;

public class RiversGenerator {
    private final SimplexGenerator2D<Block> generator;

    public RiversGenerator() {
        generator = new SimplexGenerator2D<>(new Block[][]{
                {null, Blocks.water, Blocks.deepwater, Blocks.water, null},
                {Blocks.water, Blocks.water, Blocks.water, Blocks.water, Blocks.water},
                {Blocks.deepwater, Blocks.water, Blocks.water, Blocks.deepwater, Blocks.deepwater},
                {Blocks.water, Blocks.water, Blocks.deepwater, Blocks.deepwater, Blocks.water},
                {null, Blocks.water, Blocks.deepwater, Blocks.water, null},
        }, 12, 0.5, 0.0008, 32, 12, 0.5, 0.0008, 32);
    }

    public Block sample(int x, int y) {
        return generator.sample(x, y);
    }
}
