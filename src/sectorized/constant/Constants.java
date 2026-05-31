package sectorized.constant;

import mindustry.content.Blocks;
import mindustry.world.blocks.storage.CoreBlock;

import java.util.HashMap;

public class Constants {
    public static final int mapWidth = Config.c.getInt("world.mapWidth");
    public static final int mapHeight = Config.c.getInt("world.mapHeight");

    public static final int spawnCellSize = Config.c.getInt("world.spawnCellSize");

    public static final HashMap<CoreBlock, Integer> radii = new HashMap() {{
        put(Blocks.coreShard, Config.c.getInt("world.radius.coreShard"));
        put(Blocks.coreFoundation, Config.c.getInt("world.radius.coreFoundation"));
        put(Blocks.coreNucleus, Config.c.getInt("world.radius.coreNucleus"));
        put(Blocks.coreBastion, Config.c.getInt("world.radius.coreBastion"));
        put(Blocks.coreCitadel, Config.c.getInt("world.radius.coreCitadel"));
        put(Blocks.coreAcropolis, Config.c.getInt("world.radius.coreAcropolis"));
    }};
}
