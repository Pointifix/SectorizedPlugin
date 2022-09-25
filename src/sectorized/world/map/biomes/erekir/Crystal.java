package sectorized.world.map.biomes.erekir;

import mindustry.content.Blocks;
import mindustry.world.Tile;
import sectorized.world.map.Biomes;
import sectorized.world.map.biomes.ErekirBiome;
import sectorized.world.map.generator.BlockG;
import sectorized.world.map.generator.Generator;
import sectorized.world.map.generator.SimplexGenerator2D;

public class Crystal extends ErekirBiome {
    public Crystal() {
        super(new SimplexGenerator2D(new Generator[][]{
                {BlockG.crystallineStone, BlockG.crystalFloor, BlockG.crystallineStone},
                {BlockG.crystallineStone, BlockG.crystalFloor, BlockG.crystalFloor},
                {BlockG.crystalFloor, BlockG.crystalFloor, BlockG.cryofluid}
        }, 12, 0.3, 0.05, 1.0, 12, 0.3, 0.05, 1.0), null, Blocks.carbonWall);
    }

    @Override
    public void sample(int x, int y, Tile tile, Biomes.Biome neighbor, double proximity) {
        super.sample(x, y, tile, neighbor, proximity);
    }

    @Override
    public String toString() {
        return "Crystal";
    }
}
