package sectorized.constant;

import mindustry.content.Blocks;
import mindustry.world.blocks.storage.CoreBlock;

import java.util.HashMap;

public class Constants {
    public static final int mapWidth = 600, mapHeight = 600;

    public static final int spawnCellSize = 50;

    public static final HashMap<CoreBlock, Integer> radii = new HashMap() {{
        put(Blocks.coreShard, 28);
        put(Blocks.coreFoundation, 33);
        put(Blocks.coreNucleus, 38);
        put(Blocks.coreBastion, 30);
        put(Blocks.coreCitadel, 38);
        put(Blocks.coreAcropolis, 42);
    }};
}
