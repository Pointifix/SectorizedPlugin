package sectorized.world.map.biomes.serpulo;

import arc.math.Mathf;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.maps.filters.OreFilter;
import mindustry.world.Tile;
import sectorized.world.map.Biomes;
import sectorized.world.map.biomes.SerpuloBiome;
import sectorized.world.map.generator.BlockG;
import sectorized.world.map.generator.Generator;
import sectorized.world.map.generator.SimplexGenerator2D;

public class Salines extends SerpuloBiome {
    private final int seed = Mathf.random(99999999);

    public Salines() {
        super(new SimplexGenerator2D(new Generator[][]{
                {new SimplexGenerator2D(new Generator[][]{
                        {BlockG.tar, BlockG.charr},
                        {BlockG.shale, BlockG.tar},
                }, 12, 0.5, 0.1, 1.15, 12, 0.5, 0.1, 1.15), BlockG.charr, BlockG.water, BlockG.water, BlockG.water},
                {BlockG.shale, BlockG.salt, BlockG.salt, BlockG.dacite, BlockG.water},
                {BlockG.stone, BlockG.sand, BlockG.sand, BlockG.salt, BlockG.darksand},
                {BlockG.salt, BlockG.dacite, BlockG.salt, BlockG.darksand, BlockG.darksand},
                {BlockG.slag, BlockG.salt, BlockG.darksand, BlockG.darksand, new SimplexGenerator2D(new Generator[][]{
                        {BlockG.slag, BlockG.magmarock},
                        {BlockG.magmarock, BlockG.hotrock},
                }, 12, 0.5, 0.1, 1.15, 12, 0.5, 0.1, 1.15)}
        }, 12, 0.62, 0.01, 1.3, 12, 0.62, 0.01, 1.3));

        ores.each(o -> ((OreFilter) o).threshold -= 0.03f);
    }

    @Override
    public void sample(int x, int y, Tile tile, Biomes.Biome neighbor, double proximity) {
        double rotation = ((Simplex.noise2d(seed, 12, 0.4, 0.0005, x, y) - 0.1) * 1.25) * Math.PI * 2;

        int rx = (int) (35 * Math.cos(rotation));
        int ry = (int) (35 * Math.sin(rotation));

        super.sample(x + rx, y + ry, tile, neighbor, proximity);

        if (tile.block() == Blocks.air) {
            if (tile.floor() == Blocks.sand && Mathf.chance(0.01)) tile.setBlock(Blocks.sandBoulder);
            if (tile.floor() == Blocks.salt && Mathf.chance(0.001)) tile.setBlock(Blocks.boulder);
            if (tile.floor() == Blocks.darksand && Mathf.chance(0.005)) tile.setBlock(Blocks.basaltBoulder);
            if (tile.floor() == Blocks.grass && Mathf.chance(0.005)) tile.setBlock(Blocks.pine);
        }
    }

    @Override
    public String toString() {
        return "Salines";
    }
}
