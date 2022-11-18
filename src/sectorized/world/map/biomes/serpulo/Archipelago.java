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

public class Archipelago extends SerpuloBiome {
    public Archipelago() {
        super(new SimplexGenerator2D(new Generator[][]{
                {BlockG.grass, new SimplexGenerator2D(new Generator[][]{
                        {BlockG.tar, BlockG.sand},
                        {BlockG.sand, BlockG.darksand},
                }, 12, 0.5, 0.03, 1.15, 12, 0.5, 0.03, 1.15), BlockG.sand, BlockG.water, BlockG.deepwater},
                {BlockG.sand, BlockG.darksand, BlockG.water, BlockG.deepwater, BlockG.water},
                {BlockG.darksand, BlockG.water, BlockG.deepwater, BlockG.water, BlockG.sand},
                {BlockG.water, BlockG.deepwater, BlockG.water, BlockG.sand, BlockG.grass},
                {BlockG.deepwater, BlockG.water, BlockG.darksand, BlockG.darksand, BlockG.grass}
        }, 12, 0.45, 0.01, 2, 12, 0.45, 0.01, 2));

        ores.each(o -> ((OreFilter) o).scl -= 10f);
        ores.each(o -> ((OreFilter) o).falloff -= 0.2f);

        ores.each(o -> ((OreFilter) o).threshold -= 0.05f);
    }

    @Override
    public void sample(int x, int y, Tile tile, Biomes.Biome neighbor, double proximity) {
        super.sample(x, y, tile, neighbor, proximity);

        if (tile.block() == Blocks.air) {
            if (tile.floor() == Blocks.sand && Mathf.chance(0.01)) tile.setBlock(Blocks.sandBoulder);
            if (tile.floor() == Blocks.darksand && Mathf.chance(0.005)) tile.setBlock(Blocks.basaltBoulder);
            if (tile.floor() == Blocks.grass && Mathf.chance(0.01)) tile.setBlock(Blocks.pine);
        }
    }

    @Override
    public String toString() {
        return "Archipelago";
    }
}
