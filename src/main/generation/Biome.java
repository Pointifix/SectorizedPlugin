package main.generation;

import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.maps.filters.GenerateFilter;
import mindustry.maps.filters.OreFilter;
import mindustry.world.Block;
import mindustry.world.Tile;

import static mindustry.Vars.maps;

public class Biome {
    public static Biome savanna, swamp, vulcano, icelands, river;

    static {
        Seq<GenerateFilter> savannaOres = new Seq<>();
        maps.addDefaultOres(savannaOres);
        savannaOres.each(o -> ((OreFilter) o).threshold -= 0.03f);
        savannaOres.each(o -> {
            OreFilter oreFilter = ((OreFilter) o);
            if (oreFilter.ore == Blocks.oreThorium) oreFilter.threshold += 0.01f;
        });
        savannaOres.each(GenerateFilter::randomize);

        savanna = new Biome(new Block[][]{
                {Blocks.sand, Blocks.darksand, Blocks.darksand, Blocks.darksand, Blocks.grass, Blocks.grass},
                {Blocks.sand, Blocks.sand, Blocks.grass, Blocks.darksand, Blocks.stone, Blocks.grass},
                {Blocks.water, Blocks.sand, Blocks.sand, Blocks.darksand, Blocks.darksand, Blocks.darksand},
                {Blocks.water, Blocks.water, Blocks.sand, Blocks.sand, Blocks.darksand, Blocks.darksand},
                {Blocks.water, Blocks.salt, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.darksand}
        }, savannaOres, 12, 0.55, 0.005, 12, 0.6, 0.003);

        Seq<GenerateFilter> swampOres = new Seq<>();
        maps.addDefaultOres(swampOres);
        swampOres.each(o -> ((OreFilter) o).threshold -= 0.04f);
        swampOres.insert(0, new OreFilter() {{
            ore = Blocks.oreScrap;
            scl *= 2f;
            threshold = 0.87f;
        }});
        swampOres.each(GenerateFilter::randomize);

        swamp = new Biome(new Block[][]{
                {Blocks.tar, Blocks.shale, Blocks.taintedWater, Blocks.grass, Blocks.grass, Blocks.grass},
                {Blocks.shale, Blocks.shale, Blocks.darksand, Blocks.grass, Blocks.grass, Blocks.grass},
                {Blocks.shale, Blocks.darksand, Blocks.darksand, Blocks.grass, Blocks.grass, Blocks.dirt},
                {Blocks.water, Blocks.darksand, Blocks.darksand, Blocks.moss, Blocks.dirt, Blocks.dirt},
                {Blocks.deepwater, Blocks.water, Blocks.moss, Blocks.sporeMoss, Blocks.mud, Blocks.mud}
        }, swampOres, 12, 0.63, 0.01, 12, 0.63, 0.008);

        Seq<GenerateFilter> vulcanoOres = new Seq<>();
        maps.addDefaultOres(vulcanoOres);
        vulcanoOres.each(o -> ((OreFilter) o).threshold -= 0.03f);
        vulcanoOres.insert(0, new OreFilter() {{
            ore = Blocks.oreScrap;
            scl *= 2f;
            threshold = 0.87f;
        }});
        vulcanoOres.each(GenerateFilter::randomize);

        vulcano = new Biome(new Block[][]{
                {Blocks.slag, Blocks.hotrock, Blocks.basalt, Blocks.basalt, Blocks.darksand},
                {Blocks.magmarock, Blocks.basalt, Blocks.basalt, Blocks.darksand, Blocks.darksand},
                {Blocks.hotrock, Blocks.basalt, Blocks.darksand, Blocks.darksand, Blocks.stone},
                {Blocks.basalt, Blocks.darksand, Blocks.darksand, Blocks.stone, Blocks.charr},
                {Blocks.darksand, Blocks.darksand, Blocks.stone, Blocks.craters, Blocks.dacite}
        }, vulcanoOres, 12, 0.67, 0.02, 12, 0.67, 0.03);

        Seq<GenerateFilter> icelandsOres = new Seq<>();
        maps.addDefaultOres(icelandsOres);
        icelandsOres.each(o -> ((OreFilter) o).threshold -= 0.03f);
        icelandsOres.each(o -> {
            OreFilter oreFilter = ((OreFilter) o);
            if (oreFilter.ore == Blocks.oreCoal) oreFilter.threshold -= 0.01f;
        });
        icelandsOres.insert(0, new OreFilter() {{
            ore = Blocks.oreScrap;
            scl *= 0.8f;
            threshold = 0.75f;
        }});
        icelandsOres.each(GenerateFilter::randomize);

        icelands = new Biome(new Block[][]{
                {Blocks.sand, Blocks.sand, Blocks.sand, Blocks.snow, Blocks.iceSnow},
                {Blocks.sand, Blocks.sand, Blocks.snow, Blocks.iceSnow, Blocks.ice},
                {Blocks.snow, Blocks.snow, Blocks.iceSnow, Blocks.ice, Blocks.ice},
                {Blocks.iceSnow, Blocks.iceSnow, Blocks.ice, Blocks.ice, Blocks.water},
                {Blocks.iceSnow, Blocks.ice, Blocks.sand, Blocks.ice, Blocks.water},
        }, icelandsOres, 12, 0.55, 0.05, 12, 0.6, 0.03);

        Seq<GenerateFilter> riverOres = new Seq<>();

        river = new Biome(new Block[][]{
                {Blocks.deepwater, Blocks.deepwater, Blocks.deepwater},
                {Blocks.deepwater, Blocks.water, Blocks.water},
                {Blocks.deepwater, Blocks.water, Blocks.water},
        }, riverOres, 12, 0.6, 0.01, 12, 0.6, 0.01);
    }

    private final Simplex altitudeSimplex;
    private final Simplex windSimplex;

    private final Block[][] blocks;
    private final Seq<GenerateFilter> ores;
    private final GenerateFilter.GenerateInput in;

    private final double octaves1, persistence1, scale1, octaves2, persistence2, scale2;

    public Biome(Block[][] blocks, Seq<GenerateFilter> ores, double octaves1, double persistence1, double scale1, double octaves2, double persistence2, double scale2) {
        this.blocks = blocks;
        this.ores = ores;

        this.octaves1 = octaves1;
        this.persistence1 = persistence1;
        this.scale1 = scale1;

        this.octaves2 = octaves2;
        this.persistence2 = persistence2;
        this.scale2 = scale2;

        this.altitudeSimplex = new Simplex(Mathf.random(100_000));
        this.windSimplex = new Simplex(Mathf.random(100_000));

        this.in = new GenerateFilter.GenerateInput();
    }

    public Tile getTile(int x, int y, int width, int height) {
        int altitude = Mathf.clamp(
                (int) (Utils.normalizeSimplex(this.altitudeSimplex.octaveNoise2D(this.octaves1, this.persistence1, this.scale1, x, y)) * this.blocks.length),
                0,
                this.blocks.length - 1);
        int wind = Mathf.clamp(
                (int) (Utils.normalizeSimplex(this.windSimplex.octaveNoise2D(this.octaves2, this.persistence2, this.scale2, x, y)) * this.blocks[0].length),
                0,
                this.blocks[0].length - 1);

        Block floor = this.blocks[altitude][wind];
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

        if (wall == Blocks.air) {
            if (floor == Blocks.sand && Mathf.chance(0.01)) wall = Blocks.sandBoulder;
            if (floor == Blocks.darksand && Mathf.chance(0.005)) wall = Blocks.basaltBoulder;
            if (floor == Blocks.grass && Mathf.chance(0.02)) wall = Blocks.pine;
            if (floor == Blocks.stone && Mathf.chance(0.01)) wall = Blocks.boulder;
            if (floor == Blocks.snow && Mathf.chance(0.003)) wall = Blocks.whiteTree;
            if (floor == Blocks.iceSnow && Mathf.chance(0.001)) wall = Blocks.whiteTreeDead;
            if (floor == Blocks.shale && Mathf.chance(0.003)) wall = Blocks.shaleBoulder;
            if (floor == Blocks.moss && Mathf.chance(0.009)) wall = Blocks.sporePine;
            if (floor == Blocks.sporeMoss && Mathf.chance(0.01)) wall = Blocks.sporeCluster;
            if (floor == Blocks.dacite && Mathf.chance(0.02)) wall = Blocks.daciteBoulder;
        }

        return new Tile(x, y, floor, ore, wall);
    }
}
