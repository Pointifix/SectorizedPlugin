package sectorized.world.map.generator;

import sectorized.world.map.Biomes;

public class BiomeSelection {
    public final Biomes.Biome closest, farthest;
    public final double proximity;

    public BiomeSelection(Biomes.Biome closest, Biomes.Biome farthest, double proximity) {
        this.closest = closest;
        this.farthest = farthest;
        this.proximity = proximity;
    }
}
