package sectorized.world.map.biomes.simple;

import arc.math.Mathf;
import mindustry.content.Blocks;
import mindustry.maps.filters.OreFilter;
import mindustry.world.Block;
import mindustry.world.Tile;
import sectorized.world.map.biomes.SimpleBiome;
import sectorized.world.map.generator.SimplexGenerator2D;

public class Savanna extends SimpleBiome {
    public Savanna() {
        super(new SimplexGenerator2D<>(new Block[][]{
                {Blocks.sand, Blocks.darksand, Blocks.darksand, Blocks.darksand, Blocks.grass},
                {Blocks.sand, Blocks.sand, Blocks.grass, Blocks.darksand, Blocks.stone},
                {Blocks.water, Blocks.sand, Blocks.sand, Blocks.darksand, Blocks.darksand},
                {Blocks.water, Blocks.water, Blocks.sand, Blocks.sand, Blocks.darksand},
                {Blocks.water, Blocks.salt, Blocks.sand, Blocks.sand, Blocks.sand}
        }, 12, 0.55, 0.005, 1.2, 12, 0.6, 0.003, 1.2));

        ores.each(o -> ((OreFilter) o).threshold -= 0.03f);
        ores.each(o -> {
            OreFilter oreFilter = ((OreFilter) o);
            if (oreFilter.ore == Blocks.oreThorium) oreFilter.threshold += 0.01f;
        });
    }

    @Override
    public void sample(int x, int y, Tile tile) {
        super.sample(x, y, tile);

        if (tile.block() == Blocks.air) {
            if (tile.floor() == Blocks.sand && Mathf.chance(0.01)) tile.setBlock(Blocks.sandBoulder);
            if (tile.floor() == Blocks.darksand && Mathf.chance(0.005)) tile.setBlock(Blocks.basaltBoulder);
            if (tile.floor() == Blocks.grass && Mathf.chance(0.01)) tile.setBlock(Blocks.pine);
        }
    }
}
