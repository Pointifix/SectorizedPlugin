package sectorized.world.map.biomes.simple;

import arc.math.Mathf;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.maps.filters.OreFilter;
import mindustry.world.Block;
import mindustry.world.Tile;
import sectorized.world.map.biomes.SimpleBiome;
import sectorized.world.map.generator.SimplexGenerator2D;

public class Salines extends SimpleBiome {
    private final Simplex rotationSimplex;

    public Salines() {
        super(new SimplexGenerator2D<>(new Block[][]{
                {Blocks.tar, Blocks.charr, Blocks.water, Blocks.water, Blocks.water},
                {Blocks.shale, Blocks.salt, Blocks.salt, Blocks.dacite, Blocks.water},
                {Blocks.stone, Blocks.sand, Blocks.sand, Blocks.salt, Blocks.darksand},
                {Blocks.salt, Blocks.dacite, Blocks.salt, Blocks.darksand, Blocks.darksand},
                {Blocks.slag, Blocks.salt, Blocks.darksand, Blocks.darksand, Blocks.slag}
        }, 12, 0.62, 0.01, 1.3, 12, 0.62, 0.01, 1.3));

        ores.each(o -> ((OreFilter) o).threshold -= 0.03f);

        rotationSimplex = new Simplex(Mathf.random(99999999));
    }

    @Override
    public void sample(int x, int y, Tile tile) {
        double rotation = ((rotationSimplex.octaveNoise2D(12, 0.4, 0.0005, x, y) - 0.1) * 1.25) * Math.PI * 2;

        int rx = (int) (35 * Math.cos(rotation));
        int ry = (int) (35 * Math.sin(rotation));

        super.sample(x + rx, y + ry, tile);

        if (tile.block() == Blocks.air) {
            if (tile.floor() == Blocks.sand && Mathf.chance(0.01)) tile.setBlock(Blocks.sandBoulder);
            if (tile.floor() == Blocks.salt && Mathf.chance(0.001)) tile.setBlock(Blocks.boulder);
            if (tile.floor() == Blocks.darksand && Mathf.chance(0.005)) tile.setBlock(Blocks.basaltBoulder);
            if (tile.floor() == Blocks.grass && Mathf.chance(0.005)) tile.setBlock(Blocks.pine);
        }
    }
}
