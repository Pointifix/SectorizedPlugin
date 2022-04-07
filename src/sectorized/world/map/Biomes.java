package sectorized.world.map;

import mindustry.world.Tile;
import sectorized.world.map.biomes.simple.*;

public class Biomes {
    public static Biome savanna, vulcano, swamp, tundra, desert, salines, grove, ruins, archipelago;

    static {
        savanna = new Savanna();
        vulcano = new Vulcano();
        swamp = new Swamp();
        tundra = new Tundra();
        desert = new Desert();
        salines = new Salines();
        grove = new Grove();
        ruins = new Ruins();
        archipelago = new Archipelago();
    }

    public interface Biome {
        void sample(int x, int y, Tile tile);
    }
}
