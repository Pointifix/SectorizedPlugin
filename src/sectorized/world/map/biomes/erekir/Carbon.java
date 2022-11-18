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

public class Carbon extends ErekirBiome {
    public Carbon() {
        super(new SimplexGenerator2D(new Generator[][]{
                {BlockG.arkyciteFloor, BlockG.carbonStone},
                {BlockG.carbonStone, BlockG.carbonStone},
                {BlockG.carbonStone, BlockG.carbonStone},
        }, 12, 0.45, 0.04, 1.0, 12, 0.45, 0.04, 1.0), (Floor) Blocks.carbonVent, Blocks.carbonWall);
    }

    @Override
    public void sample(int x, int y, Tile tile, Biomes.Biome neighbor, double proximity) {
        super.sample(x, y, tile, neighbor, proximity);

        if (tile.block() == Blocks.air) {
            if (tile.floor() == Blocks.carbonStone && Mathf.chance(0.006)) tile.setBlock(Blocks.carbonBoulder);
        }
    }

    @Override
    public String toString() {
        return "Carbon";
    }
}
