package sectorized.world.map;

import sectorized.world.map.generator.BiomeSelection;
import sectorized.world.map.generator.SimplexGenerator3D;

public class ErekirBiomesGenerator implements BiomesGenerator {
    private final SimplexGenerator3D generator;

    public ErekirBiomesGenerator() {
        generator = new SimplexGenerator3D(new Biomes.Biome[][][]{
                {
                        {Biomes.carbon, Biomes.carbon, Biomes.carbon},
                        {Biomes.arkyic, Biomes.arkyic, Biomes.crystal}
                },
                {
                        {Biomes.crystal, Biomes.rhyolite, Biomes.carbon},
                        {Biomes.redstone, Biomes.beryllic, Biomes.carbon}
                },
                {
                        {Biomes.regolith, Biomes.crystal, Biomes.redstone},
                        {Biomes.regolith, Biomes.rhyolite, Biomes.rhyolite},
                }
        }, 10, 0.32, 0.005, 2.0);
    }

    public BiomeSelection sample(int x, int y) {
        return generator.sample(x, y);
    }
}
