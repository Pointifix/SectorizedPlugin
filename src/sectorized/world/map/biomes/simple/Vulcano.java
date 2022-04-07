package sectorized.world.map.biomes.simple;

import arc.math.Mathf;
import mindustry.content.Blocks;
import mindustry.maps.filters.OreFilter;
import mindustry.world.Block;
import mindustry.world.Tile;
import sectorized.world.map.biomes.SimpleBiome;
import sectorized.world.map.generator.SimplexGenerator2D;

public class Vulcano extends SimpleBiome {
    public Vulcano() {
        super(new SimplexGenerator2D<>(new Block[][]{
                {Blocks.slag, Blocks.hotrock, Blocks.basalt, Blocks.basalt, Blocks.darksand},
                {Blocks.magmarock, Blocks.basalt, Blocks.basalt, Blocks.darksand, Blocks.darksand},
                {Blocks.hotrock, Blocks.basalt, Blocks.darksand, Blocks.darksand, Blocks.stone},
                {Blocks.basalt, Blocks.darksand, Blocks.darksand, Blocks.stone, Blocks.charr},
                {Blocks.darksand, Blocks.darksand, Blocks.stone, Blocks.craters, Blocks.dacite}
        }, 12, 0.67, 0.02, 1.3, 12, 0.67, 0.03, 1.3));

        ores.each(o -> ((OreFilter) o).threshold -= 0.03f);
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
            if (tile.floor() == Blocks.darksand && Mathf.chance(0.005)) tile.setBlock(Blocks.boulder);
            if (tile.floor() == Blocks.darksand && Mathf.chance(0.005)) tile.setBlock(Blocks.basaltBoulder);
            if (tile.floor() == Blocks.stone && Mathf.chance(0.01)) tile.setBlock(Blocks.boulder);
            if (tile.floor() == Blocks.dacite && Mathf.chance(0.02)) tile.setBlock(Blocks.daciteBoulder);
        }
    }
}
