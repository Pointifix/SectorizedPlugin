package sectorized.world.map.biomes.erekir;

import arc.math.Mathf;
import mindustry.content.Blocks;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import sectorized.world.map.Biomes;
import sectorized.world.map.biomes.ErekirBiome;
import sectorized.world.map.generator.BlockG;
import sectorized.world.map.generator.Generator;
import sectorized.world.map.generator.SimplexGenerator2D;

public class Arkyic extends ErekirBiome {
    public Arkyic() {
        super(new SimplexGenerator2D(new Generator[][]{
                {BlockG.arkyicStone, BlockG.arkyicStone, BlockG.arkyicStone},
                {BlockG.arkyicStone, BlockG.arkyicStone, BlockG.arkyicStone},
                {BlockG.arkyicStone, BlockG.arkyicStone, BlockG.arkyciteFloor}
        }, 12, 0.3, 0.05, 1.0, 12, 0.3, 0.05, 1.0), (Floor) Blocks.arkyicVent, Blocks.arkyicWall);
    }

    @Override
    public void sample(int x, int y, Tile tile, Biomes.Biome neighbor, double proximity) {
        super.sample(x, y, tile, neighbor, proximity);

        if (tile.block() == Blocks.air) {
            if (tile.floor() == Blocks.arkyicStone && Mathf.chance(0.008)) tile.setBlock(Blocks.arkyicBoulder);
            if (tile.floor() == Blocks.arkyicStone && Mathf.chance(0.001)) tile.setBlock(Blocks.crystalOrbs);
        }
    }

    @Override
    public String toString() {
        return "Arkyic";
    }
}
