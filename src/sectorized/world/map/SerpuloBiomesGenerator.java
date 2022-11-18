package sectorized.world.map;

import sectorized.world.map.generator.BiomeSelection;
import sectorized.world.map.generator.SimplexGenerator3D;

public class SerpuloBiomesGenerator implements BiomesGenerator {
    private final SimplexGenerator3D generator;

    public SerpuloBiomesGenerator() {
        generator = new SimplexGenerator3D(new Biomes.Biome[][][]{
                {
                        {Biomes.desert, Biomes.desert, Biomes.archipelago},
                        {Biomes.desert, Biomes.desert, Biomes.ruins},
                        {Biomes.grove, Biomes.desert, Biomes.salines}
                },
                {
                        {Biomes.desert, Biomes.desert, Biomes.swamp},
                        {Biomes.desert, Biomes.desert, Biomes.swamp},
                        {Biomes.vulcano, Biomes.vulcano, Biomes.tundra}
                },
                {
                        {Biomes.savanna, Biomes.savanna, Biomes.swamp},
                        {Biomes.savanna, Biomes.ruins, Biomes.swamp},
                        {Biomes.vulcano, Biomes.vulcano, Biomes.tundra}
                }
        }, 12, 0.7, 0.0002, 8);
    }

    public BiomeSelection sample(int x, int y) {
        return generator.sample(x, y);
    }
}
