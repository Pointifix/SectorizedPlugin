package sectorized.world.map;

import sectorized.world.map.generator.BiomeSelection;

public interface BiomesGenerator {
    BiomeSelection sample(int x, int y);
}
