package sectorized.world.map.biomes.simple;

import arc.math.Mathf;
import mindustry.content.Blocks;
import mindustry.maps.filters.OreFilter;
import mindustry.world.Block;
import mindustry.world.Tile;
import sectorized.world.map.biomes.SimpleBiome;
import sectorized.world.map.generator.SimplexGenerator2D;

public class Archipelago extends SimpleBiome {
    public Archipelago() {
        super(new SimplexGenerator2D<>(new Block[][]{
                {Blocks.grass, Blocks.sand, Blocks.sand, Blocks.water, Blocks.deepwater},
                {Blocks.sand, Blocks.darksand, Blocks.water, Blocks.deepwater, Blocks.water},
                {Blocks.darksand, Blocks.water, Blocks.deepwater, Blocks.water, Blocks.sand},
                {Blocks.water, Blocks.deepwater, Blocks.water, Blocks.sand, Blocks.grass},
                {Blocks.deepwater, Blocks.water, Blocks.darksand, Blocks.darksand, Blocks.grass}
        }, 12, 0.45, 0.01, 2, 12, 0.45, 0.01, 2));

        ores.each(o -> ((OreFilter) o).scl -= 10f);
        ores.each(o -> ((OreFilter) o).falloff -= 0.2f);

        ores.each(o -> ((OreFilter) o).threshold -= 0.05f);
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
