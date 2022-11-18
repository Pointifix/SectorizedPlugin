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

public class Rhyolite extends ErekirBiome {
    public Rhyolite() {
        super(new SimplexGenerator2D(new Generator[][]{
                {BlockG.roughRhyolite, BlockG.rhyolite, BlockG.rhyolite},
                {BlockG.rhyolite, BlockG.rhyolite, BlockG.roughRhyolite},
                {BlockG.roughRhyolite, BlockG.rhyoliteCrater, BlockG.slag}
        }, 12, 0.3, 0.05, 1.0, 12, 0.3, 0.05, 1.0), (Floor) Blocks.rhyoliteVent, Blocks.rhyoliteWall);
    }

    @Override
    public void sample(int x, int y, Tile tile, Biomes.Biome neighbor, double proximity) {
        super.sample(x, y, tile, neighbor, proximity);

        if (tile.block() == Blocks.air) {
            if (tile.floor() == Blocks.rhyolite && Mathf.chance(0.008)) tile.setBlock(Blocks.rhyoliteBoulder);
            if (tile.floor() == Blocks.redIce && Mathf.chance(0.01)) tile.setBlock(Blocks.redIceBoulder);
        }
    }

    @Override
    public String toString() {
        return "Rhyolite";
    }
}
