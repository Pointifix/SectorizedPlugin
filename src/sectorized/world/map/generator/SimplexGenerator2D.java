package sectorized.world.map.generator;

import arc.math.Mathf;
import arc.util.noise.Simplex;
import mindustry.world.Block;

public class SimplexGenerator2D extends Generator {
    private final Generator[][] mapping;

    private final int seed1 = Mathf.random(99999999), seed2 = Mathf.random(99999999);

    private final double octaves1, persistence1, scale1, multiplier1;
    private final double octaves2, persistence2, scale2, multiplier2;

    public SimplexGenerator2D(Generator[][] mapping, double octaves1, double persistence1, double scale1, double multiplier1, double octaves2, double persistence2, double scale2, double multiplier2) {
        this.mapping = mapping;

        this.octaves1 = octaves1;
        this.persistence1 = persistence1;
        this.scale1 = scale1;
        this.multiplier1 = multiplier1;

        this.octaves2 = octaves2;
        this.persistence2 = persistence2;
        this.scale2 = scale2;
        this.multiplier2 = multiplier2;
    }

    public SimplexGenerator2D(Generator[][] mapping, double octaves1, double persistence1, double scale1, double octaves2, double persistence2, double scale2) {
        this(mapping, octaves1, persistence1, scale1, 1, octaves2, persistence2, scale2, 1);
    }

    public Block sample(int x, int y) {
        int sampleX = sampleSimplex(seed1, octaves1, persistence1, scale1, multiplier1, x, y, mapping.length - 1);
        int sampleY = sampleSimplex(seed2, octaves2, persistence2, scale2, multiplier2, x, y, mapping[0].length - 1);

        return mapping[sampleX][sampleY].sample(x, y);
    }

    private int sampleSimplex(int seed, double octaves, double persistence, double scale, double multiplier, int x, int y, int max) {
        return (int) Math.max(Math.min((normalizeSimplex(Simplex.noise2d(seed, octaves, persistence, scale, x, y)) * multiplier - (multiplier / 2) + 0.5) * (max + 1), max), 0);
    }

    private double normalizeSimplex(double value) {
        return (value - 0.1) * 1.25;
    }
}
