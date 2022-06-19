package sectorized.world.map.generator;

import arc.math.Mathf;
import arc.util.noise.Simplex;
import sectorized.world.map.Biomes;

public class SimplexGenerator3D {
    private final Biomes.Biome[][][] mapping;

    private final Simplex simplex1, simplex2, simplex3;

    private final double octaves, persistence, scale, multiplier;

    public SimplexGenerator3D(Biomes.Biome[][][] mapping, double octaves, double persistence, double scale, double multiplier) {
        this.mapping = mapping;

        this.octaves = octaves;
        this.persistence = persistence;
        this.scale = scale;
        this.multiplier = multiplier;

        simplex1 = new Simplex(Mathf.random(99999999));
        simplex2 = new Simplex(Mathf.random(99999999));
        simplex3 = new Simplex(Mathf.random(99999999));
    }

    public Biomes.Biome sample(int x, int y) {
        int sampleX = sampleSimplex(simplex1, octaves, persistence, scale, multiplier, x, y, mapping.length - 1);
        int sampleY = sampleSimplex(simplex2, octaves, persistence, scale, multiplier, x, y, mapping[0].length - 1);
        int sampleZ = sampleSimplex(simplex3, octaves, persistence, scale, multiplier, x, y, mapping[0][0].length - 1);

        return mapping[sampleX][sampleY][sampleZ];
    }

    private int sampleSimplex(Simplex simplex, double octaves, double persistence, double scale, double multiplier, int x, int y, int max) {
        return (int) Math.max(Math.min((normalizeSimplex(simplex.octaveNoise2D(octaves, persistence, scale, x, y)) * multiplier - (multiplier / 2) + 0.5) * (max + 1), max), 0);
    }

    private double normalizeSimplex(double value) {
        return (value - 0.1) * 1.25;
    }
}
