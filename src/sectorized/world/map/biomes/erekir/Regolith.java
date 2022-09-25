package sectorized.world.map.biomes.erekir;

import mindustry.content.Blocks;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import sectorized.world.map.Biomes;
import sectorized.world.map.biomes.ErekirBiome;
import sectorized.world.map.generator.BlockG;
import sectorized.world.map.generator.Generator;
import sectorized.world.map.generator.SimplexGenerator2D;

public class Regolith extends ErekirBiome {
    public Regolith() {
        super(new SimplexGenerator2D(new Generator[][]{
                {BlockG.regolith, BlockG.regolith},
                {BlockG.yellowStone, BlockG.yellowStonePlates}
        }, 12, 0.7, 0.006, 1.15, 12, 0.6, 0.004, 1.15), (Floor) Blocks.yellowStoneVent, Blocks.regolithWall);
    }

    @Override
    public void sample(int x, int y, Tile tile, Biomes.Biome neighbor, double proximity) {
        super.sample(x, y, tile, neighbor, proximity);
    }

    @Override
    public String toString() {
        return "Regolith";
    }
}
