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

public class Savanna extends SerpuloBiome {
    public Savanna() {
        super(new SimplexGenerator2D(new Generator[][]{
                {BlockG.sand, BlockG.darksand, BlockG.darksand, BlockG.darksand, BlockG.grass},
                {BlockG.sand, BlockG.sand, BlockG.grass, BlockG.darksand, BlockG.stone},
                {BlockG.water, BlockG.sand, BlockG.sand, BlockG.darksand, BlockG.darksand},
                {BlockG.water, BlockG.water, BlockG.sand, BlockG.sand, BlockG.darksand},
                {BlockG.water, BlockG.salt, BlockG.sand, BlockG.sand, BlockG.sand}
        }, 12, 0.55, 0.005, 1.2, 12, 0.6, 0.003, 1.2));

        ores.each(o -> ((OreFilter) o).threshold -= 0.03f);
        ores.each(o -> {
            OreFilter oreFilter = ((OreFilter) o);
            if (oreFilter.ore == Blocks.oreThorium) oreFilter.threshold += 0.01f;
        });
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
        return "Savanna";
    }
}
