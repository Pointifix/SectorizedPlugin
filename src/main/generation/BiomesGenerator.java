package main.generation;

import arc.math.Mathf;
import arc.util.noise.Simplex;

public class BiomesGenerator {
    private final Biome[][] biomes = {
            {Biome.savanna, Biome.savanna, Biome.river, Biome.vulcano},
            {Biome.savanna, Biome.savanna, Biome.river, Biome.vulcano},
            {Biome.river, Biome.river, Biome.river, Biome.river},
            {Biome.icelands, Biome.river, Biome.swamp, Biome.swamp}
    };

    private final Simplex temperateSimplex, humiditySimplex;

    public BiomesGenerator() {
        temperateSimplex = new Simplex(Mathf.random(100_000));
        humiditySimplex = new Simplex(Mathf.random(100_000));
    }

    protected Biome getBiome(int x, int y) {
        int temperature = Mathf.clamp(
                (int) ((Utils.normalizeSimplex(this.temperateSimplex.octaveNoise2D(10, 0.55, 0.0008, x, y)) * 8 - 4.5) * this.biomes.length),
                0,
                this.biomes.length - 1);
        int humidity = Mathf.clamp(
                (int) ((Utils.normalizeSimplex(this.humiditySimplex.octaveNoise2D(10, 0.55, 0.0008, x, y)) * 8 - 4.5) * this.biomes[0].length),
                0,
                this.biomes[0].length - 1);

        return biomes[temperature][humidity];
    }
}
