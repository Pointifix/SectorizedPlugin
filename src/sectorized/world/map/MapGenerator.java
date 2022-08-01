package sectorized.world.map;

import arc.Events;
import arc.func.Cons;
import arc.math.Mathf;
import arc.struct.StringMap;
import mindustry.content.Blocks;
import mindustry.maps.Map;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.Tiles;
import mindustry.world.blocks.environment.Floor;
import sectorized.SectorizedEvents;
import sectorized.constant.DiscordBot;

import java.util.HashMap;

import static mindustry.Vars.state;
import static mindustry.Vars.world;

public class MapGenerator implements Cons<Tiles> {
    @Override
    public void get(Tiles tiles) {
        RiversGenerator riversGenerator = new RiversGenerator();
        BiomesGenerator biomesGenerator = new BiomesGenerator();

        int offsetX = Mathf.random(9999999);
        int offsetY = Mathf.random(9999999);

        HashMap<Biomes.Biome, Integer> biomeDistribution = new HashMap();

        for (int x = 0; x < world.width(); x++) {
            for (int y = 0; y < world.height(); y++) {
                Block water = riversGenerator.sample(x + offsetX, y + offsetY);

                Tile tile = new Tile(x, y);

                if (water == null) {
                    Biomes.Biome biome = biomesGenerator.sample(x + offsetX, y + offsetY);

                    biomeDistribution.put(biome, biomeDistribution.getOrDefault(biome, 0) + 1);

                    biome.sample(x + offsetX, y + offsetY, tile);
                } else {
                    tile.setFloor((Floor) water);
                }

                if (x == 0 || x == world.width() - 1 || y == 0 || y == world.height() - 1)
                    tile.setBlock(Blocks.duneWall);

                tiles.set(x, y, tile);
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

        state.map = new Map(StringMap.of("name", mostFrequentBiomes));
    }
}
