package sectorized.world.map.biomes.erekir;

import arc.math.Mathf;
import mindustry.content.Blocks;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import sectorized.world.map.Biomes;
import sectorized.world.map.biomes.ErekirBiome;
import sectorized.world.map.generator.BlockG;
import sectorized.world.map.generator.Generator;
import sectorized.world.map.generator.SimplexGenerator2D;

public class Redstone extends ErekirBiome {
    public Redstone() {
        super(new SimplexGenerator2D(new Generator[][]{
                {BlockG.redStone, BlockG.denseRedStone},
                {BlockG.redStone, BlockG.denseRedStone},
                {BlockG.redStone, BlockG.redIce}
        }, 12, 0.7, 0.05, 1.0, 12, 0.7, 0.05, 1.0), (Floor) Blocks.redStoneVent, Blocks.redStoneWall, 0.15);
    }

    @Override
    public void sample(int x, int y, Tile tile, Biomes.Biome neighbor, double proximity) {
        super.sample(x, y, tile, neighbor, proximity);

        if (tile.block() == Blocks.air) {
            if (tile.floor() == Blocks.redStone && Mathf.chance(0.008)) tile.setBlock(Blocks.redStoneBoulder);
        }
    }

    @Override
    public String toString() {
        return "Redstone";
    }
}
