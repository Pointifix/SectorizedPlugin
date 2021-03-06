package sectorized.world.map.biomes.simple;

import arc.math.Mathf;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.maps.filters.OreFilter;
import mindustry.world.Tile;
import sectorized.world.map.biomes.SimpleBiome;
import sectorized.world.map.generator.BlockG;
import sectorized.world.map.generator.Generator;
import sectorized.world.map.generator.SimplexGenerator2D;

public class Grove extends SimpleBiome {
    private final Simplex rotationSimplex;

    public Grove() {
        super(new SimplexGenerator2D(new Generator[][]{
                {BlockG.tar, BlockG.darksand, BlockG.darksand, BlockG.shale, BlockG.dirt},
                {BlockG.basalt, BlockG.shale, BlockG.shale, BlockG.dirt, BlockG.shale},
                {BlockG.charr, BlockG.shale, BlockG.darksand, BlockG.shale, BlockG.darksand},
                {BlockG.shale, BlockG.darksand, BlockG.shale, BlockG.darksand, BlockG.hotrock},
                {BlockG.stone, new SimplexGenerator2D(new Generator[][]{
                        {BlockG.tar, BlockG.shale},
                        {BlockG.stone, BlockG.darksand},
                }, 12, 0.7, 0.1, 1.15, 12, 0.7, 0.1, 1.15), BlockG.darksand, BlockG.hotrock, BlockG.magmarock}
        }, 12, 0.65, 0.02, 1.1, 12, 0.65, 0.002, 1.2));

        ores.each(o -> ((OreFilter) o).threshold -= 0.01f);
        ores.each(o -> {
            OreFilter oreFilter = ((OreFilter) o);
            if (oreFilter.ore == Blocks.oreThorium) oreFilter.threshold -= 0.02f;
            if (oreFilter.ore == Blocks.oreTitanium) oreFilter.threshold -= 0.02f;
        });
        ores.insert(0, new OreFilter() {{
            ore = Blocks.oreScrap;
            scl *= 0.8f;
            threshold = 0.75f;
        }});

        rotationSimplex = new Simplex(Mathf.random(99999999));
    }

    @Override
    public void sample(int x, int y, Tile tile) {
        super.sample(x, y, tile);

        if (tile.block() == Blocks.air) {
            if (tile.floor() == Blocks.sand && Mathf.chance(0.01)) tile.setBlock(Blocks.sandBoulder);
            if (tile.floor() == Blocks.shale && Mathf.chance(0.02)) tile.setBlock(Blocks.shaleBoulder);
            if (tile.floor() == Blocks.salt && Mathf.chance(0.001)) tile.setBlock(Blocks.boulder);
            if (tile.floor() == Blocks.darksand && Mathf.chance(0.005)) tile.setBlock(Blocks.basaltBoulder);
        }
    }

    @Override
    public String toString() {
        return "Grove";
    }
}
