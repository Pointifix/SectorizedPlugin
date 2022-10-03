package sectorized.world.map.biomes.erekir;

import mindustry.content.Blocks;
import mindustry.world.Tile;
import sectorized.world.map.Biomes;
import sectorized.world.map.biomes.ErekirBiome;
import sectorized.world.map.generator.BlockG;
import sectorized.world.map.generator.Generator;
import sectorized.world.map.generator.SimplexGenerator2D;

public class Beryllic extends ErekirBiome {
    public Beryllic() {
        super(new SimplexGenerator2D(new Generator[][]{
                {BlockG.arkyciteFloor, BlockG.beryllicStone, BlockG.darksand},
                {BlockG.beryllicStone, BlockG.beryllicStone, BlockG.beryllicStone},
                {BlockG.beryllicStone, BlockG.darksand, BlockG.beryllicStone}
        }, 12, 0.7, 0.006, 1.15, 12, 0.6, 0.004, 1.15), null, Blocks.beryllicStoneWall);
    }

    @Override
    public void sample(int x, int y, Tile tile, Biomes.Biome neighbor, double proximity) {
        super.sample(x, y, tile, neighbor, proximity);
    }

    @Override
    public String toString() {
        return "Beryllic";
    }
}
