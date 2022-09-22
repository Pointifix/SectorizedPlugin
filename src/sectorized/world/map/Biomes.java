package sectorized.world.map;

import mindustry.world.Tile;
import sectorized.world.map.biomes.simple.*;

import java.util.ArrayList;

public class Biomes {
    public static Biome savanna, vulcano, swamp, tundra, desert, salines, grove, ruins, archipelago;

    public static ArrayList<Biome> all = new ArrayList<>();

    static {
        savanna = new Savanna();
        all.add(savanna);
        vulcano = new Vulcano();
        all.add(vulcano);
        swamp = new Swamp();
        all.add(swamp);
        tundra = new Tundra();
        all.add(tundra);
        desert = new Desert();
        all.add(desert);
        salines = new Salines();
        all.add(salines);
        grove = new Grove();
        all.add(grove);
        ruins = new Ruins();
        all.add(ruins);
        archipelago = new Archipelago();
        all.add(archipelago);
    }

    public interface Biome {
        void sample(int x, int y, Tile tile);
    }
}
