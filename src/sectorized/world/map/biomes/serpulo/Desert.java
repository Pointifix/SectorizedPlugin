package sectorized.world.map.biomes.serpulo;

import arc.math.Mathf;
import mindustry.content.Blocks;
import mindustry.maps.filters.OreFilter;
import mindustry.world.Tile;
import sectorized.world.map.Biomes;
import sectorized.world.map.biomes.SerpuloBiome;
import sectorized.world.map.generator.BlockG;
import sectorized.world.map.generator.Generator;
import sectorized.world.map.generator.SimplexGenerator2D;

public class Desert extends SerpuloBiome {
    public Desert() {
        super(new SimplexGenerator2D(new Generator[][]{
                {new SimplexGenerator2D(new Generator[][]{
                        {BlockG.tar, BlockG.charr},
                        {BlockG.stone, BlockG.tar},
                }, 12, 0.7, 0.1, 1.15, 12, 0.7, 0.1, 1.15), BlockG.charr, BlockG.darksand, BlockG.darksand, BlockG.darksand},
                {BlockG.stone, BlockG.darksand, BlockG.darksand, BlockG.sand, BlockG.sand},
                {BlockG.darksand, BlockG.darksand, BlockG.sand, BlockG.sand, BlockG.sand},
                {BlockG.darksand, BlockG.sand, BlockG.sand, BlockG.sand, BlockG.sand},
                {BlockG.darksand, BlockG.sand, BlockG.sand, BlockG.sand, new SimplexGenerator2D(new Generator[][]{
                        {BlockG.tar, BlockG.sand},
                        {BlockG.tar, BlockG.shale},
                }, 12, 0.5, 0.1, 1.15, 12, 0.5, 0.1, 1.15)}
        }, 12, 0.7, 0.006, 1.15, 12, 0.6, 0.004, 1.15));


        ores.each(o -> ((OreFilter) o).threshold -= 0.03f);
    }

    @Override
    public void sample(int x, int y, Tile tile, Biomes.Biome neighbor, double proximity) {
        super.sample(x, y, tile, neighbor, proximity);

        if (tile.block() == Blocks.air) {
            if (tile.floor() == Blocks.sand && Mathf.chance(0.01)) tile.setBlock(Blocks.sandBoulder);
            if (tile.floor() == Blocks.darksand && Mathf.chance(0.005)) tile.setBlock(Blocks.basaltBoulder);
            if (tile.floor() == Blocks.grass && Mathf.chance(0.005)) tile.setBlock(Blocks.pine);
        }
    }

    @Override
    public String toString() {
        return "Desert";
    }
}
