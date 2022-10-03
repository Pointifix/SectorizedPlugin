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

public class Tundra extends SerpuloBiome {
    public Tundra() {
        super(new SimplexGenerator2D(new Generator[][]{
                {BlockG.sand, BlockG.sand, BlockG.sand, BlockG.sand, BlockG.snow},
                {BlockG.sand, BlockG.sand, BlockG.snow, BlockG.iceSnow, new SimplexGenerator2D(new Generator[][]{
                        {BlockG.tar, BlockG.ice},
                        {BlockG.ice, BlockG.cryofluid},
                }, 12, 0.5, 0.1, 1.15, 12, 0.5, 0.1, 1.15)},
                {BlockG.sand, BlockG.snow, BlockG.iceSnow, BlockG.ice, BlockG.ice},
                {BlockG.iceSnow, BlockG.iceSnow, BlockG.ice, BlockG.ice, BlockG.water},
                {BlockG.tar, BlockG.ice, BlockG.sand, BlockG.ice, BlockG.water}
        }, 12, 0.55, 0.05, 1.2, 12, 0.6, 0.03, 1.2));

        ores.each(o -> ((OreFilter) o).threshold -= 0.03f);
        ores.each(o -> {
            OreFilter oreFilter = ((OreFilter) o);
            if (oreFilter.ore == Blocks.oreCoal) oreFilter.threshold -= 0.01f;
        });
        ores.insert(0, new OreFilter() {{
            ore = Blocks.oreScrap;
            scl *= 0.8f;
            threshold = 0.75f;
        }});
    }

    @Override
    public void sample(int x, int y, Tile tile, Biomes.Biome neighbor, double proximity) {
        super.sample(x, y, tile, neighbor, proximity);

        if (tile.block() == Blocks.air) {
            if (tile.floor() == Blocks.sand && Mathf.chance(0.01)) tile.setBlock(Blocks.sandBoulder);
            if (tile.floor() == Blocks.snow && Mathf.chance(0.003)) tile.setBlock(Blocks.whiteTree);
            if (tile.floor() == Blocks.iceSnow && Mathf.chance(0.001)) tile.setBlock(Blocks.whiteTreeDead);
        }
    }

    @Override
    public String toString() {
        return "Tundra";
    }
}
