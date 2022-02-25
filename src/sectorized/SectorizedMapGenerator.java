package sectorized;

import arc.func.Cons;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.maps.Map;
import mindustry.maps.filters.GenerateFilter;
import mindustry.maps.filters.OreFilter;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.Tiles;

import static mindustry.Vars.maps;
import static mindustry.Vars.state;

public class SectorizedMapGenerator implements Cons<Tiles> {
    private final int width, height;

    private final Block[][] floors = {
            {Blocks.sand, Blocks.darksand, Blocks.darksand, Blocks.darksand, Blocks.grass, Blocks.grass},
            {Blocks.deepwater, Blocks.sand, Blocks.grass, Blocks.darksand, Blocks.stone, Blocks.grass},
            {Blocks.deepwater, Blocks.water, Blocks.sand, Blocks.darksand, Blocks.darksand, Blocks.darksand},
            {Blocks.deepwater, Blocks.water, Blocks.sand, Blocks.sand, Blocks.darksand, Blocks.darksand},
            {Blocks.deepwater, Blocks.deepwater, Blocks.water, Blocks.sand, Blocks.sand, Blocks.darksand}
    };

    private final Block[][] blocks = {
            {Blocks.duneWall, Blocks.duneWall, Blocks.duneWall, Blocks.duneWall, Blocks.stoneWall, Blocks.stoneWall},
            {Blocks.sandWall, Blocks.sandWall, Blocks.duneWall, Blocks.duneWall, Blocks.duneWall, Blocks.stoneWall},
            {Blocks.sandWall, Blocks.stoneWall, Blocks.sandWall, Blocks.duneWall, Blocks.duneWall, Blocks.duneWall},
            {Blocks.stoneWall, Blocks.sandWall, Blocks.sandWall, Blocks.sandWall, Blocks.duneWall, Blocks.duneWall},
            {Blocks.stoneWall, Blocks.stoneWall, Blocks.sandWall, Blocks.sandWall, Blocks.sandWall, Blocks.duneWall}
    };

    public SectorizedMapGenerator(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void get(Tiles tiles) {
        Seq<GenerateFilter> ores = new Seq<>();
        maps.addDefaultOres(ores);
        ores.each(o -> ((OreFilter) o).threshold -= 0.05f);
        ores.insert(0, new OreFilter() {{
            ore = Blocks.oreScrap;
            scl *= 2f;
            threshold = 0.87f;
        }});
        ores.each(GenerateFilter::randomize);
        Simplex simplex1 = new Simplex(Mathf.random(100_000));
        Simplex simplex2 = new Simplex(Mathf.random(100_000));
        GenerateFilter.GenerateInput in = new GenerateFilter.GenerateInput();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int temp = Mathf.clamp((int) ((simplex1.octaveNoise2D(12, 0.55, 1.0 / 200, x, y) - 0.5) * 10 * blocks.length), 0, blocks.length - 1);
                int elev = Mathf.clamp((int) ((simplex2.octaveNoise2D(12, 0.6, 1.0 / 300, x, y)) * blocks[0].length), 0, blocks[0].length - 1);

                Block floor = floors[temp][elev];
                Block wall = Blocks.air;
                Block ore = Blocks.air;

                for (GenerateFilter f : ores) {
                    in.floor = floor;
                    in.block = wall;
                    in.overlay = ore;
                    in.x = x;
                    in.y = y;
                    in.width = width;
                    in.height = height;
                    f.apply(in);
                    if (in.overlay != Blocks.air) {
                        ore = in.overlay;
                    }
                }

                if (x < 2 || x >= width - 2 || y < 2 || y >= height - 2) {
                    wall = blocks[temp][elev];
                }

                if (wall == Blocks.air) {
                    if (floor == Blocks.sand && Mathf.chance(0.01)) wall = Blocks.sandBoulder;
                    if (floor == Blocks.darksand && Mathf.chance(0.005)) wall = Blocks.basaltBoulder;
                    if (floor == Blocks.grass && Mathf.chance(0.02)) wall = Blocks.pine;
                    if (floor == Blocks.stone && Mathf.chance(0.01)) wall = Blocks.boulder;
                }

                tiles.set(x, y, new Tile(x, y, floor.id, ore.id, wall.id));
            }
        }

        state.map = new Map(StringMap.of("name", "Sectorized"));
    }
}
