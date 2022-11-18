package sectorized.world.map.biomes.serpulo;

import arc.math.Mathf;
import mindustry.content.Blocks;
import mindustry.game.Schematic;
import mindustry.game.Schematics;
import mindustry.game.Team;
import mindustry.maps.filters.OreFilter;
import mindustry.world.Tile;
import sectorized.world.map.Biomes;
import sectorized.world.map.biomes.SerpuloBiome;
import sectorized.world.map.generator.BlockG;
import sectorized.world.map.generator.Generator;
import sectorized.world.map.generator.SimplexGenerator2D;

import static mindustry.Vars.world;

public class Ruins extends SerpuloBiome {
    private static final Schematic[] ruins = {
            Schematics.readBase64("bXNjaAF4nEWMSwqAMBBD31h/6MaLuPBIRYsUaitV8fqOuHAR8iEJLaagjHZz1Pny8ZjoF3fM2e+nTxEMnTq7j7cNgeHXY7B5dZRLShloFBQYQVTIO1TSb+gQofrS+u18bfMAAasVAw=="),
            Schematics.readBase64("bXNjaAF4nC1N2wrDIAw9VdlG21/xpX+UrXkQnEp0lP39ElxIci7hEAR4h1DozbjJJ5V+YDu5vyS1kWoBAlZV1OJFOWPrNZPERoUz7k8ag+WLtdWLJZZ6MoAds5b/aDljxvWZEW/LGTgzvcLymFJdWFvCz7yd8QP0RxiP"),
            Schematics.readBase64("bXNjaAF4nEWJ3QqEIBSExx8qdgl6Ea96IlFZDpjWSen1s7zYgWG+mYGCltDJbgEDV0rniq8Pp2PaC+UEaIwuJ1+pYGmz3c1lYzTR8i/g818wRzoqecO5lsAAJnSJ16IDZAv1oOzX04VqIXtr3w28thuS"),
            Schematics.readBase64("bXNjaAF4nCWM6wqDMAyFT+ttOGTsQfpvL1RsYAFtXWwne/ulSkjOyZcLevQWbfQroZfCcX/hHmifhbfMKQIDxi0dJC6mQJi2suzk5hRD4Yxp4U/h4CSVTIJHfifhsjohP+ckeGbOPlaiF1/6KRr1t9/c4ZcFD1YomYLbk9QHwKQJczNoYK2BqZ1FNcbgNA1qmFZLh6ZDq9BW6ap0V6cetr8WMJygvbhO/jfcL+E="),
            Schematics.readBase64("bXNjaAF4nEWKQQ7DIAwE10CrNrnkIxz7ICuxKiQCyLTK94u5VBrL410jIDiEwqfgrt9U+gvrIX3X1D6pFuAGz7pjGxG3eHHOMbO+BWuvQ2LjIhnLv8bS6iUaSz0EwHMMaAJnkKk3B7l5z8bBDywMY9HDMvuiH4ZMHPs="),
            Schematics.readBase64("bXNjaAF4nB2IQQ7CMAwEN26ACi58pFf+Y7mRail1KieA+D2Gw+7OLDIyIRvvBWd/qvUHbmvp4noMbQZkXMP4WN5cK+57kY1NheuyusZz6cJjFMcszV7l0xzAKYIESlEB9GOkCdPfYwj0BbRvGcI="),
            Schematics.readBase64("bXNjaAF4nCWLWQ4CIRBECxrUaKIn4dvzEIY4bZiWsGSub49+VF5qg4e3cBK3jFObLP2J25J7alwHfwTwuKqLNeyxFFzeU9KvcGvkgvtLq5VHDrXl3vGokucWB6ewNNYDcFbBgIzCgg5HfziQxRETKQysBjqxXzkAHfM="),
            Schematics.readBase64("bXNjaAF4nC2Maw7CIBCER+mD1Bi9CP+9DtJVMRSahbbp7V3UnWTmy2Sy6NEe0UQ7ETpefMw3nEbKjv1cfIqAxnUi97LROxvMyD4EaJfiSnti6PcS3Xd4yT546U2eKBRi9HdbJHcMc9qITUwjYZDPdjablS/ntBI/QtrM0xYCMOB3B9Gxwt+qpFI1FBpBsUYQbQUN1QkpqB5d3X0AZwstDw==")
    };

    public Ruins() {
        super(new SimplexGenerator2D(new Generator[][]{
                {new SimplexGenerator2D(new Generator[][]{
                        {BlockG.dirt, BlockG.cryofluid},
                        {BlockG.basalt, BlockG.darksand},
                }, 12, 0.5, 0.1, 1.15, 12, 0.5, 0.1, 1.15), BlockG.basalt, BlockG.darksand, BlockG.sand, BlockG.grass},
                {BlockG.basalt, BlockG.darksand, BlockG.darksand, BlockG.sand, BlockG.sand},
                {BlockG.darksand, BlockG.darksand, BlockG.darksand, BlockG.darksand, BlockG.darksand},
                {BlockG.darksand, BlockG.darksand, BlockG.darksand, new SimplexGenerator2D(new Generator[][]{
                        {BlockG.darksand, BlockG.darksand, BlockG.darksand},
                        {BlockG.tar, BlockG.darksand, BlockG.darksand},
                        {BlockG.darksand, BlockG.darksand, BlockG.basalt},
                }, 12, 0.5, 0.1, 1.15, 12, 0.5, 0.1, 1.15), BlockG.metalFloorDamaged},
                {BlockG.dacite, BlockG.darksand, BlockG.darksand, BlockG.metalFloorDamaged, BlockG.metalFloor}
        }, 12, 0.65, 0.02, 1.8, 12, 0.65, 0.02, 1.8));

        ores.each(o -> ((OreFilter) o).threshold -= 0.01f);
    }

    @Override
    public void sample(int x, int y, Tile tile, Biomes.Biome neighbor, double proximity) {
        super.sample(x, y, tile, neighbor, proximity);

        if (tile.block() == Blocks.air) {
            if (tile.floor() == Blocks.sand && Mathf.chance(0.01)) tile.setBlock(Blocks.sandBoulder);
            if (tile.floor() == Blocks.salt && Mathf.chance(0.002)) tile.setBlock(Blocks.boulder);
            if (tile.floor() == Blocks.darksand && Mathf.chance(0.005)) tile.setBlock(Blocks.basaltBoulder);
            if (tile.floor() == Blocks.shale && Mathf.chance(0.005)) tile.setBlock(Blocks.pine);
        }

        if (tile.floor() == Blocks.metalFloor || tile.floor() == Blocks.metalFloorDamaged) {
            if (Mathf.chance(0.05)) setRandomRuin(tile);
        }

        if (tile.floor() == Blocks.darksand) {
            if (Mathf.chance(0.005)) setRandomRuin(tile);
        }
    }

    public void setRandomRuin(Tile tile) {
        Schematic ruin = Schematics.rotate(ruins[Mathf.random(ruins.length - 1)], Mathf.random(3));

        ruin.tiles.each(st -> {
            Tile t = world.tile(tile.x + st.x - ruin.width, tile.y + st.y - ruin.height);

            if (t == null || Mathf.chance(0.2)) return;

            if (t.block() == Blocks.air && t.floor().hasSurface()) {
                t.setBlock(st.block, Team.sharded, st.rotation);

                if (st.config != null) {
                    t.build.configureAny(st.config);
                }

                if (t.build != null) {
                    t.build.damage(Mathf.random((float) (t.build.health * 0.95)));
                }
            }
        });
    }

    @Override
    public String toString() {
        return "Ruins";
    }
}
