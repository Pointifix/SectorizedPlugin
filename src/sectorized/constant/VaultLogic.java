package sectorized.constant;

import arc.math.geom.Point2;
import mindustry.world.Block;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.world;

public class VaultLogic {
    public static boolean adjacentToCore(int x, int y, Block block) {
        Point2[] nearby = Edges.getEdges(block.size);

        for (Point2 point2 : nearby) {
            Tile neighbor = world.tile(x + point2.x, y + point2.y);

            if (neighbor.block() instanceof CoreBlock) return true;
        }
        return false;
    }
}
