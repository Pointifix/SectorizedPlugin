package sectorized.world.map.biomes.simple;

import arc.math.Mathf;
import mindustry.content.Blocks;
import mindustry.maps.filters.OreFilter;
import mindustry.world.Block;
import mindustry.world.Tile;
import sectorized.world.map.biomes.SimpleBiome;
import sectorized.world.map.generator.SimplexGenerator2D;

public class Tundra extends SimpleBiome {
    public Tundra() {
        super(new SimplexGenerator2D<>(new Block[][]{
                {Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.snow},
                {Blocks.sand, Blocks.sand, Blocks.snow, Blocks.iceSnow, Blocks.ice},
                {Blocks.sand, Blocks.snow, Blocks.iceSnow, Blocks.ice, Blocks.ice},
                {Blocks.iceSnow, Blocks.iceSnow, Blocks.ice, Blocks.ice, Blocks.water},
                {Blocks.tar, Blocks.ice, Blocks.sand, Blocks.ice, Blocks.water}
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
    public void sample(int x, int y, Tile tile) {
        super.sample(x, y, tile);

        if (tile.block() == Blocks.air) {
            if (tile.floor() == Blocks.sand && Mathf.chance(0.01)) tile.setBlock(Blocks.sandBoulder);
            if (tile.floor() == Blocks.snow && Mathf.chance(0.003)) tile.setBlock(Blocks.whiteTree);
            if (tile.floor() == Blocks.iceSnow && Mathf.chance(0.001)) tile.setBlock(Blocks.whiteTreeDead);
        }
    }
}
