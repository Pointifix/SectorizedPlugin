package sectorized.constant;

import mindustry.content.Blocks;
import mindustry.world.blocks.storage.CoreBlock;

import java.util.HashMap;

public class Constants {
    public static final int mapWidth = Config.c.world.mapWidth;
    public static final int mapHeight = Config.c.world.mapHeight;

    public static final int spawnCellSize = Config.c.world.spawnCellSize;

    public static final HashMap<CoreBlock, Integer> radii = new HashMap() {{
        put(Blocks.coreShard, Config.c.world.radius.coreShard);
        put(Blocks.coreFoundation, Config.c.world.radius.coreFoundation);
        put(Blocks.coreNucleus, Config.c.world.radius.coreNucleus);
        put(Blocks.coreBastion, Config.c.world.radius.coreBastion);
        put(Blocks.coreCitadel, Config.c.world.radius.coreCitadel);
        put(Blocks.coreAcropolis, Config.c.world.radius.coreAcropolis);
    }};
}
