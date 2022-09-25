package sectorized.world.map;

import sectorized.world.map.generator.BiomeSelection;
import sectorized.world.map.generator.SimplexGenerator3D;

public class ErekirBiomesGenerator implements BiomesGenerator {
    private final SimplexGenerator3D generator;

    public ErekirBiomesGenerator() {
        generator = new SimplexGenerator3D(new Biomes.Biome[][][]{
                {
                        {Biomes.crystal, Biomes.carbon},
                        {Biomes.arkyic, Biomes.carbon}
                },
                {
                        {Biomes.crystal, Biomes.rhyolite},
                        {Biomes.redstone, Biomes.beryllic}
                },
                {
                        {Biomes.regolith, Biomes.rhyolite},
                        {Biomes.regolith, Biomes.beryllic},
                }
        }, 12, 0.35, 0.005, 3);
    }

    public BiomeSelection sample(int x, int y) {
        return generator.sample(x, y);
    }
}
