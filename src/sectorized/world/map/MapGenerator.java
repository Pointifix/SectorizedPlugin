package sectorized.world.map;

import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.math.Mathf;
import arc.struct.StringMap;
import mindustry.content.Blocks;
import mindustry.content.Planets;
import mindustry.maps.Map;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.Tiles;
import mindustry.world.blocks.environment.Floor;
import sectorized.SectorizedEvents;
import sectorized.constant.DiscordBot;
import sectorized.constant.Loadout;
import sectorized.constant.State;
import sectorized.world.map.generator.BiomeSelection;

import java.util.HashMap;

import static mindustry.Vars.state;
import static mindustry.Vars.world;

public class MapGenerator implements Cons<Tiles> {
    private static final int sampleDensity = 50;

    private final Biomes.Biome biomeVote;

    public MapGenerator() {
        String biomeVoteString = (String) Core.settings.get("biomeVote", "");
        biomeVote = Biomes.all.stream().filter(biome -> biome.toString().equals(biomeVoteString)).findFirst().orElse(null);
        Core.settings.put("biomeVote", "");
    }

    @Override
    public void get(Tiles tiles) {
        final String planet = biomeVote != null ? biomeVote.getPlanet() : Mathf.chance(0.7) ? Planets.serpulo.name : Planets.erekir.name;
        State.planet = planet;
        state.rules.loadout = Loadout.getLoadout(1);

        RiversGenerator riversGenerator = new RiversGenerator();
        BiomesGenerator biomesGenerator = planet.equals(Planets.serpulo.name) ? new SerpuloBiomesGenerator() : new ErekirBiomesGenerator();

        int offsetX = Mathf.random(9999999);
        int offsetY = Mathf.random(9999999);

        HashMap<Biomes.Biome, Integer> biomeDistribution = new HashMap<>();

        if (biomeVote != null) {
            int maxOccurrences = 0;
            int maxOffsetX = offsetX, maxOffsetY = offsetY;

            for (int i = 0; i < 25; i++) {
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
                if (planet.equals(Planets.serpulo.name)) {
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
                } else if (planet.equals(Planets.erekir.name)) {
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

        StringBuilder mostFrequentBiomes = new StringBuilder();
        int threshold = (int) (world.width() * world.height() * 0.1);
        final int[] count = {0};

        biomeDistribution.forEach((key, value) -> {
            if (value >= threshold && count[0] < 3) {
                mostFrequentBiomes.append(key.toString()).append("-");
                count[0]++;
            }
        });

        mostFrequentBiomes.deleteCharAt(mostFrequentBiomes.length() - 1);

        Events.fire(new SectorizedEvents.BiomesGeneratedEvent());

        DiscordBot.sendMessage("**Server started!** Current map: " + mostFrequentBiomes);

        if (planet.equals(Planets.serpulo.name)) {
            state.rules.env = Planets.serpulo.defaultEnv;
            state.rules.hiddenBuildItems.clear();
            state.rules.hiddenBuildItems.addAll(Planets.serpulo.hiddenItems);
        } else if (planet.equals(Planets.erekir.name)) {
            state.rules.env = Planets.erekir.defaultEnv;
            state.rules.hiddenBuildItems.clear();
            state.rules.hiddenBuildItems.addAll(Planets.erekir.hiddenItems);
        }

        state.map = new Map(StringMap.of("name", mostFrequentBiomes));
    }
}
