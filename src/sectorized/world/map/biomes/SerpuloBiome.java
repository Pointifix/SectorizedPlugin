package sectorized.world.map.biomes;

import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.content.Planets;
import mindustry.maps.filters.GenerateFilter;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import sectorized.world.map.Biomes;
import sectorized.world.map.generator.SimplexGenerator2D;

import static mindustry.Vars.maps;
import static mindustry.Vars.world;

public class SerpuloBiome implements Biomes.Biome {
    private final String planet = Planets.serpulo.name;

    private final SimplexGenerator2D generator;

    protected final Seq<GenerateFilter> ores;
    private final GenerateFilter.GenerateInput in;

    public SerpuloBiome(SimplexGenerator2D generator) {
        this.generator = generator;

        ores = new Seq<>();
        maps.addDefaultOres(ores);
        ores.each(GenerateFilter::randomize);

        in = new GenerateFilter.GenerateInput();
    }

    @Override
    public void sample(int x, int y, Tile tile, Biomes.Biome neighbor, double proximity) {
        Block floor = generator.sample(x, y);

        tile.setFloor((Floor) floor);

        for (GenerateFilter f : ores) {
            in.floor = floor;
            in.block = Blocks.air;
            in.overlay = Blocks.air;
            in.x = x;
            in.y = y;
            in.width = world.width();
            in.height = world.height();
            f.apply(in);
            if (in.overlay != Blocks.air) {
                tile.setOverlay(in.overlay);
            }
        }
    }

    @Override
    public String getPlanet() {
        return Planets.serpulo.name;
    }
}
