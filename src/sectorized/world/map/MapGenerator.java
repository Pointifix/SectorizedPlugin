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

import static mindustry.Vars.state;
import static mindustry.Vars.world;

public class MapGenerator implements Cons<Tiles> {
    @Override
    public void get(Tiles tiles) {
        RiversGenerator riversGenerator = new RiversGenerator();
        BiomesGenerator biomesGenerator = new BiomesGenerator();

        int offsetX = Mathf.random(9999999);
        int offsetY = Mathf.random(9999999);

        for (int x = 0; x < world.width(); x++) {
            for (int y = 0; y < world.height(); y++) {
                Block water = riversGenerator.sample(x + offsetX, y + offsetY);

                Tile tile = new Tile(x, y);

                if (water == null) {
                    Biomes.Biome biome = biomesGenerator.sample(x + offsetX, y + offsetY);

                    biome.sample(x + offsetX, y + offsetY, tile);
                } else {
                    tile.setFloor((Floor) water);
                }

                if (x == 0 || x == world.width() - 1 || y == 0 || y == world.height() - 1)
                    tile.setBlock(Blocks.duneWall);

                tiles.set(x, y, tile);
            }
        }

        Events.fire(new SectorizedEvents.BiomesGeneratedEvent());

        state.map = new Map(StringMap.of("name", "Sectorized"));
    }
}
