package sectorized.world.map.biomes.simple;

import arc.math.Mathf;
import mindustry.content.Blocks;
import mindustry.maps.filters.OreFilter;
import mindustry.world.Tile;
import sectorized.world.map.biomes.SimpleBiome;
import sectorized.world.map.generator.BlockG;
import sectorized.world.map.generator.Generator;
import sectorized.world.map.generator.SimplexGenerator2D;

public class Swamp extends SimpleBiome {
    public Swamp() {
        super(new SimplexGenerator2D(new Generator[][]{
                {BlockG.tar, BlockG.shale, BlockG.taintedWater, BlockG.grass, BlockG.grass, BlockG.grass},
                {new SimplexGenerator2D(new Generator[][]{
                        {BlockG.tar, BlockG.shale},
                        {BlockG.shale, BlockG.tar},
                }, 12, 0.5, 0.1, 1.15, 12, 0.5, 0.1, 1.15), BlockG.shale, BlockG.darksand, BlockG.grass, BlockG.grass, BlockG.grass},
                {BlockG.shale, BlockG.darksand, BlockG.darksand, BlockG.grass, BlockG.grass, BlockG.dirt},
                {BlockG.water, BlockG.darksand, BlockG.darksand, BlockG.moss, BlockG.dirt, BlockG.dirt},
                {BlockG.deepwater, BlockG.water, BlockG.moss, BlockG.sporeMoss, BlockG.mud, BlockG.mud}
        }, 12, 0.63, 0.01, 1.2, 12, 0.63, 0.008, 1.2));

        ores.each(o -> ((OreFilter) o).threshold -= 0.04f);
        ores.insert(0, new OreFilter() {{
            ore = Blocks.oreScrap;
            scl *= 2f;
            threshold = 0.87f;
        }});
    }

    @Override
    public void sample(int x, int y, Tile tile) {
        super.sample(x, y, tile);

        if (tile.block() == Blocks.air) {
            if (tile.floor() == Blocks.darksand && Mathf.chance(0.005)) tile.setBlock(Blocks.basaltBoulder);
            if (tile.floor() == Blocks.grass && Mathf.chance(0.005)) tile.setBlock(Blocks.pine);
            if (tile.floor() == Blocks.shale && Mathf.chance(0.003)) tile.setBlock(Blocks.shaleBoulder);
            if (tile.floor() == Blocks.moss && Mathf.chance(0.009)) tile.setBlock(Blocks.sporePine);
            if (tile.floor() == Blocks.sporeMoss && Mathf.chance(0.01)) tile.setBlock(Blocks.sporeCluster);
        }
    }

    @Override
    public String toString() {
        return "Swamp";
    }
}
