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

public class Vulcano extends SerpuloBiome {
    public Vulcano() {
        super(new SimplexGenerator2D(new Generator[][]{
                {BlockG.slag, BlockG.hotrock, BlockG.basalt, BlockG.basalt, BlockG.darksand},
                {BlockG.magmarock, BlockG.basalt, BlockG.basalt, BlockG.darksand, BlockG.darksand},
                {BlockG.hotrock, BlockG.basalt, BlockG.darksand, BlockG.darksand, BlockG.stone},
                {BlockG.basalt, BlockG.darksand, BlockG.darksand, BlockG.stone, BlockG.charr},
                {BlockG.darksand, BlockG.darksand, BlockG.stone, BlockG.craters, new SimplexGenerator2D(new Generator[][]{
                        {BlockG.tar, BlockG.dacite},
                        {BlockG.dacite, BlockG.craters},
                }, 12, 0.5, 0.01, 1.15, 12, 0.5, 0.01, 1.15)}
        }, 12, 0.67, 0.02, 1.3, 12, 0.67, 0.03, 1.3));

        ores.each(o -> ((OreFilter) o).threshold -= 0.03f);
        ores.insert(0, new OreFilter() {{
            ore = Blocks.oreScrap;
            scl *= 2f;
            threshold = 0.87f;
        }});
    }

    @Override
    public void sample(int x, int y, Tile tile, Biomes.Biome neighbor, double proximity) {
        super.sample(x, y, tile, neighbor, proximity);

        if (tile.block() == Blocks.air) {
            if (tile.floor() == Blocks.darksand && Mathf.chance(0.005)) tile.setBlock(Blocks.boulder);
            if (tile.floor() == Blocks.darksand && Mathf.chance(0.005)) tile.setBlock(Blocks.basaltBoulder);
            if (tile.floor() == Blocks.stone && Mathf.chance(0.01)) tile.setBlock(Blocks.boulder);
            if (tile.floor() == Blocks.dacite && Mathf.chance(0.02)) tile.setBlock(Blocks.daciteBoulder);
        }
    }

    @Override
    public String toString() {
        return "Vulcano";
    }
}
