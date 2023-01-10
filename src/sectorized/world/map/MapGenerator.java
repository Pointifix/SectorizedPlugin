package sectorized.world.map;

import arc.Core;
import arc.func.Cons;
import arc.math.Mathf;
import mindustry.content.Blocks;
import mindustry.content.Planets;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.Tiles;
import mindustry.world.blocks.environment.Floor;
import sectorized.constant.State;
import sectorized.world.map.generator.BiomeSelection;

import java.util.HashMap;

import static mindustry.Vars.world;

public class MapGenerator implements Cons<Tiles> {
    private static final int sampleDensity = 50;

    private final Biomes.Biome biomeVote;

    public StringBuilder mostFrequentBiomes = new StringBuilder();

    public MapGenerator(Biomes.Biome biomeVote) {
        this.biomeVote = biomeVote;
    }

    @Override
    public void get(Tiles tiles) {
        RiversGenerator riversGenerator = new RiversGenerator();
        BiomesGenerator biomesGenerator = State.planet.equals(Planets.serpulo.name) ? new SerpuloBiomesGenerator() : new ErekirBiomesGenerator();

        int offsetX = Mathf.random(9999999);
        int offsetY = Mathf.random(9999999);

        HashMap<Biomes.Biome, Integer> biomeDistribution = new HashMap<>();

        if (biomeVote != null) {
            int maxOccurrences = 0;
            int maxOffsetX = offsetX, maxOffsetY = offsetY;

            for (int i = 0; i < 10; i++) {
                for (int x = sampleDensity / 2; x < world.width(); x += sampleDensity) {
                    for (int y = sampleDensity / 2; y < world.height(); y += sampleDensity) {
                        Biomes.Biome biome = biomesGenerator.sample(x + offsetX, y + offsetY).closest;

                        biomeDistribution.put(biome, biomeDistribution.getOrDefault(biome, 0) + 1);
                    }
                }

                int occurrences = biomeDistribution.getOrDefault(biomeVote, 0);

                if (occurrences > maxOccurrences) {
                    maxOccurrences = occurrences;
                    maxOffsetX = offsetX;
                    maxOffsetY = offsetY;
                }

                maxOccurrences = Math.max(maxOccurrences, occurrences);

                offsetX = Mathf.random(9999999);
                offsetY = Mathf.random(9999999);

                biomeDistribution.clear();
            }

            offsetX = maxOffsetX;
            offsetY = maxOffsetY;
        }

        for (int x = 0; x < world.width(); x++) {
            for (int y = 0; y < world.height(); y++) {
                if (State.planet.equals(Planets.serpulo.name)) {
                    Block water = riversGenerator.sample(x + offsetX, y + offsetY);

                    Tile tile = new Tile(x, y);

                    if (x == 0 || x == world.width() - 1 || y == 0 || y == world.height() - 1) {
                        tile.setFloor((Floor) Blocks.stone);
                        tile.setBlock(Blocks.duneWall);
                    } else {
                        if (water == null) {
                            BiomeSelection biomeSelection = biomesGenerator.sample(x + offsetX, y + offsetY);

                            biomeDistribution.put(biomeSelection.closest, biomeDistribution.getOrDefault(biomeSelection.closest, 0) + 1);

                            biomeSelection.closest.sample(x + offsetX, y + offsetY, tile, biomeSelection.farthest, biomeSelection.proximity);
                        } else {
                            tile.setFloor((Floor) water);
                        }
                    }

                    tiles.set(x, y, tile);
                } else if (State.planet.equals(Planets.erekir.name)) {
                    Tile tile = new Tile(x, y);

                    if (x <= 1 || x >= world.width() - 2 || y <= 1 || y >= world.height() - 2) {
                        tile.setFloor((Floor) Blocks.carbonStone);
                        tile.setBlock(Blocks.carbonWall);
                    } else {
                        BiomeSelection biomeSelection = biomesGenerator.sample(x + offsetX, y + offsetY);

                        biomeDistribution.put(biomeSelection.closest, biomeDistribution.getOrDefault(biomeSelection.closest, 0) + 1);

                        biomeSelection.closest.sample(x + offsetX, y + offsetY, tile, biomeSelection.farthest, biomeSelection.proximity);
                    }

                    tiles.set(x, y, tile);
                }
            }
        }

        int threshold = (int) (world.width() * world.height() * 0.1);
        final int[] count = {0};

        biomeDistribution.forEach((key, value) -> {
            if (value >= threshold && count[0] < 3) {
                mostFrequentBiomes.append(key.toString()).append("-");
                count[0]++;
            }
        });

        mostFrequentBiomes.deleteCharAt(mostFrequentBiomes.length() - 1);

        Core.settings.put("mostFrequentBiomes", mostFrequentBiomes.toString());
    }
}
