package sectorized.world.map;

import mindustry.world.Tile;
import sectorized.world.map.biomes.erekir.*;
import sectorized.world.map.biomes.serpulo.*;

import java.util.ArrayList;

public class Biomes {
    public static Biome savanna, vulcano, swamp, tundra, desert, salines, grove, ruins, archipelago;

    public static Biome crystal, beryllic, arkyic, regolith, rhyolite, carbon, redstone;

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

        crystal = new Crystal();
        all.add(crystal);
        beryllic = new Beryllic();
        all.add(beryllic);
        arkyic = new Arkyic();
        all.add(arkyic);
        regolith = new Regolith();
        all.add(regolith);
        rhyolite = new Rhyolite();
        all.add(rhyolite);
        carbon = new Carbon();
        all.add(carbon);
        redstone = new Redstone();
        all.add(redstone);
    }

    public interface Biome {
        void sample(int x, int y, Tile tile, Biome neighbor, double proximity);

        String getPlanet();
    }
}
