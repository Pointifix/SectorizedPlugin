package sectorized.world.map.generator;

import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.noise.Simplex;
import sectorized.world.map.Biomes;

public class SimplexGenerator3D {
    private final Biomes.Biome[][][] mapping;

    private final int seed1 = Mathf.random(99999999), seed2 = Mathf.random(99999999), seed3 = Mathf.random(99999999);

    private final double octaves, persistence, scale, multiplier;

    public SimplexGenerator3D(Biomes.Biome[][][] mapping, double octaves, double persistence, double scale, double multiplier) {
        this.mapping = mapping;

        this.octaves = octaves;
        this.persistence = persistence;
        this.scale = scale;
        this.multiplier = multiplier;
    }

    public BiomeSelection sample(int x, int y) {
        double sampleX = sampleSimplex(seed1, octaves, persistence, scale, multiplier, x, y, mapping.length - 1);
        double sampleY = sampleSimplex(seed2, octaves, persistence, scale, multiplier, x, y, mapping[0].length - 1);
        double sampleZ = sampleSimplex(seed3, octaves, persistence, scale, multiplier, x, y, mapping[0][0].length - 1);

        Vec3 origin = new Vec3(sampleX, sampleY, sampleZ);

        Vec3[] corners = {
                new Vec3((int) sampleX, (int) sampleY, (int) sampleZ),
                new Vec3((int) sampleX, (int) sampleY, (int) sampleZ + 1),
                new Vec3((int) sampleX, (int) sampleY + 1, (int) sampleZ),
                new Vec3((int) sampleX, (int) sampleY + 1, (int) sampleZ + 1),
                new Vec3((int) sampleX + 1, (int) sampleY, (int) sampleZ),
                new Vec3((int) sampleX + 1, (int) sampleY, (int) sampleZ + 1),
                new Vec3((int) sampleX + 1, (int) sampleY + 1, (int) sampleZ),
                new Vec3((int) sampleX + 1, (int) sampleY + 1, (int) sampleZ + 1)
        };

        Vec3 c1 = corners[0];
        double cd1 = Double.MAX_VALUE;
        for (Vec3 corner : corners) {
            double d = origin.dst2(corner);
            if (d < cd1) {
                c1 = corner;
                cd1 = d;
            }
        }
        Vec3 c2 = corners[0];
        double cd2 = Double.MAX_VALUE;
        for (Vec3 corner : corners) {
            double d = origin.dst2(corner);
            if (corner != c1 && d < cd2) {
                c2 = corner;
                cd2 = d;
            }
        }

        Biomes.Biome closestBiome = clampedMapping(c1);
        Biomes.Biome farthestBiome = clampedMapping(c2);

        return new BiomeSelection(closestBiome, farthestBiome, cd2);
    }

    private double sampleSimplex(int seed, double octaves, double persistence, double scale, double multiplier, int x, int y, int max) {
        return Math.max(Math.min((normalizeSimplex(Simplex.noise2d(seed, octaves, persistence, scale, x, y)) * multiplier - (multiplier / 2) + 0.5) * (max + 1), max), 0);
    }

    private double normalizeSimplex(double value) {
        return (value - 0.1) * 1.25;
    }

    private Biomes.Biome clampedMapping(Vec3 v) {
        int x = Mathf.clamp((int) v.x, 0, mapping.length - 1);
        int y = Mathf.clamp((int) v.y, 0, mapping[0].length - 1);
        int z = Mathf.clamp((int) v.z, 0, mapping[0][0].length - 1);

        return mapping[x][y][z];
    }
}
