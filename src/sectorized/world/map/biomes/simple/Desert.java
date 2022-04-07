package sectorized.world.map.biomes.simple;

import arc.math.Mathf;
import mindustry.content.Blocks;
import mindustry.maps.filters.OreFilter;
import mindustry.world.Block;
import mindustry.world.Tile;
import sectorized.world.map.biomes.SimpleBiome;
import sectorized.world.map.generator.SimplexGenerator2D;

public class Desert extends SimpleBiome {
    public Desert() {
        super(new SimplexGenerator2D<>(new Block[][]{
                {Blocks.tar, Blocks.charr, Blocks.darksand, Blocks.darksand, Blocks.darksand},
                {Blocks.stone, Blocks.darksand, Blocks.darksand, Blocks.sand, Blocks.sand},
                {Blocks.darksand, Blocks.darksand, Blocks.sand, Blocks.sand, Blocks.sand},
                {Blocks.darksand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sand},
                {Blocks.darksand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sand}
        }, 12, 0.7, 0.006, 1.15, 12, 0.6, 0.004, 1.15));

        ores.each(o -> ((OreFilter) o).threshold -= 0.03f);
    }

    @Override
    public void sample(int x, int y, Tile tile) {
        super.sample(x, y, tile);

        if (tile.block() == Blocks.air) {
            if (tile.floor() == Blocks.sand && Mathf.chance(0.01)) tile.setBlock(Blocks.sandBoulder);
            if (tile.floor() == Blocks.darksand && Mathf.chance(0.005)) tile.setBlock(Blocks.basaltBoulder);
            if (tile.floor() == Blocks.grass && Mathf.chance(0.005)) tile.setBlock(Blocks.pine);
        }
    }
}
