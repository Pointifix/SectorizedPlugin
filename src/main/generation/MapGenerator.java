package main.generation;

import arc.func.Cons;
import arc.struct.StringMap;
import mindustry.content.Blocks;
import mindustry.maps.Map;
import mindustry.world.Tile;
import mindustry.world.Tiles;

import static mindustry.Vars.state;

public class MapGenerator implements Cons<Tiles> {
    private final int width, height;

    public MapGenerator(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void get(Tiles tiles) {
        BiomesGenerator biomesGenerator = new BiomesGenerator();

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                Biome biome = biomesGenerator.getBiome(x, y);

                Tile tile = biome.getTile(x, y, this.width, this.height);

                if (x == 0 || x == this.width - 1 || y == 0 || y == this.height - 1) tile.setBlock(Blocks.duneWall);

                tiles.set(x, y, tile);
            }
        }

        state.map = new Map(StringMap.of("name", "Sectorized"));
    }
}
