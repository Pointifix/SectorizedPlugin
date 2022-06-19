package sectorized.world.map.generator;

import mindustry.world.Block;

public class SimpleGenerator extends Generator {
    public final Block block;

    public SimpleGenerator(Block block) {
        this.block = block;
    }

    @Override
    public Block sample(int x, int y) {
        return this.block;
    }
}
