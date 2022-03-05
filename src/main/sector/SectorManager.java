package main.sector;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.Time;
import mindustry.content.Blocks;
import mindustry.content.Bullets;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import static mindustry.Vars.*;

public class SectorManager {
    protected static final HashMap<CoreBlock, Integer> radii = new HashMap<CoreBlock, Integer>() {{
        put((CoreBlock) Blocks.coreShard, 28);
        put((CoreBlock) Blocks.coreFoundation, 33);
        put((CoreBlock) Blocks.coreNucleus, 38);
    }};

    private final int width, height;
    private final int[][] sectors;
    private final GridAccelerator gridAccelerator;
    private final LinkedHashMap<Integer, Rectangle> coreBuildRectangleMap = new LinkedHashMap<>();
    public final SectorSpawns sectorSpawns;

    private int nextZIndex = 0;

    public SectorManager(int width, int height) {
        this.width = width;
        this.height = height;
        this.sectors = new int[this.width][this.height];
        this.gridAccelerator = new GridAccelerator(width, height);
        this.sectorSpawns = new SectorSpawns(this.width, this.height, this.gridAccelerator);

        Arrays.stream(this.sectors).forEach(array -> Arrays.fill(array, 0));
    }

    public boolean validPlace(int x, int y, int teamId) {
        return this.sectors[x][y] == teamId;
    }

    public boolean validPlace(int x, int y, Block block, int teamId) {
        int offset = -(block.size - 1) / 2;

        for (int dx = 0; dx < block.size; dx++) {
            for (int dy = 0; dy < block.size; dy++) {
                int wx = dx + offset + x, wy = dy + offset + y;

                if (!validPlace(wx, wy, teamId)) return false;
            }
        }

        return true;
    }

    public void addArea(int coreX, int coreY, CoreBlock coreBlock, int teamId) {
        int size = SectorManager.radii.get(coreBlock);

        Rectangle addRectangle = new Rectangle(Math.max(coreX - size, 0),
                Math.max(coreY - size, 0),
                Math.min(coreX + size, this.width - 1),
                Math.min(coreY + size, this.height - 1),
                teamId,
                nextZIndex++);

        coreBuildRectangleMap.put(Point2.pack(coreX, coreY), addRectangle);

        SubRectangle[] subRectangles = this.gridAccelerator.getIntersectingRectangles(addRectangle);
        this.gridAccelerator.addRectangle(addRectangle);

        HashSet<Border> borderHashSet = new HashSet<>(size * 6);

        addRectangle.iterate((x, y, borderX, borderY) -> {
            Tile tile = world.tile(x, y);
            if (tile.block() == Blocks.shockMine) {
                this.destroyTileWithoutPolyRebuild(tile);
            }

            if (borderX || borderY) {
                borderHashSet.add(new Border(x, y, borderX, borderY, addRectangle.teamId));
            } else {
                this.sectors[x][y] = addRectangle.teamId;
            }
        });

        handleSubRectanglesAndBorder(subRectangles, borderHashSet);
    }

    public void upgradeArea(int coreX, int coreY, CoreBlock coreBlock, int teamId) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                Tile nearby = world.tile(coreX, coreY).nearby(x, y);

                if (coreBuildRectangleMap.containsKey(nearby.pos())) {
                    Rectangle removeRectangle = coreBuildRectangleMap.remove(nearby.pos());
                    this.gridAccelerator.removeRectangle(removeRectangle);
                    this.addArea(coreX, coreY, coreBlock, teamId);

                    return;
                }
            }
        }
    }

    public void removeArea(int coreX, int coreY) {
        Tile coreTile = world.tile(coreX, coreY);
        int size = SectorManager.radii.get((CoreBlock) coreTile.block());
        Rectangle removeRectangle = coreBuildRectangleMap.remove(coreTile.pos());

        this.gridAccelerator.removeRectangle(removeRectangle);
        SubRectangle[] subRectangles = this.gridAccelerator.getIntersectingRectangles(removeRectangle);

        removeRectangle.iterate((x, y, borderX, borderY) -> {
            Tile tile = world.tile(x, y);
            if (tile.block() == Blocks.shockMine) {
                this.destroyTileWithoutPolyRebuild(tile);
            }
            this.sectors[x][y] = 0;
        });

        HashSet<Border> borderHashSet = new HashSet<>(size * 6);
        handleSubRectanglesAndBorder(subRectangles, borderHashSet);

        removeRectangle.iterate((x, y, borderX, borderY) -> {
            int sector = this.sectors[x][y];

            if (!(borderX || borderY) && sector >= 0 && sector != world.tile(x, y).team().id) {
                this.destroyTileWithoutPolyRebuild(world.tile(x, y));
            }
        });
    }

    private void handleSubRectanglesAndBorder(SubRectangle[] subRectangles, HashSet<Border> borderHashSet) {
        for (SubRectangle subRectangle : subRectangles) {
            subRectangle.iterate((x, y, borderX, borderY) -> {
                Border border = new Border(x, y, borderX, borderY, subRectangle.teamId, this.sectors[x][y]);

                borderHashSet.remove(border);

                if ((borderX || borderY) && this.sectors[x][y] != subRectangle.teamId) borderHashSet.add(border);
                else this.sectors[x][y] = subRectangle.teamId;
            });
        }

        for (Border border : borderHashSet) {
            this.sectors[border.x][border.y] = -1;
            Tile tile = world.tile(border.x, border.y);
            if (tile.build != null) destroyTileWithoutPolyRebuild(tile);

            if (tile.passable()) {
                if (border.borderX) {
                    if (border.y % 2 == 0 && !border.borderY) {
                        if (border.team2Id > 0)
                            tile.setNet(Blocks.shockMine, Team.get(border.team2Id), 0);
                    } else if (border.team1Id > 0) {
                        tile.setNet(Blocks.shockMine, Team.get(border.team1Id), 0);
                    }
                }
                if (border.borderY) {
                    if (border.x % 2 == 0 && !border.borderX) {
                        if (border.team2Id > 0)
                            tile.setNet(Blocks.shockMine, Team.get(border.team2Id), 0);
                    } else if (border.team1Id > 0) {
                        tile.setNet(Blocks.shockMine, Team.get(border.team1Id), 0);
                    }
                }
            }
        }
    }

    private void destroyTileWithoutPolyRebuild(Tile tile) {
        Building building = tile.build;

        if (building != null) {
            float explosiveness = building.block.baseExplosiveness;
            float flammability = 0.0F;
            if (building.block.hasItems) {
                for (Item item : content.items()) {
                    int amount = building.items.get(item);
                    explosiveness += item.explosiveness * amount;
                    flammability += item.flammability * amount;
                }
            }
            if (building.block.hasLiquids) {
                flammability += building.liquids.sum((liquid, amount) -> liquid.flammability * amount / 2.0F);
                explosiveness += building.liquids.sum((liquid, amount) -> liquid.explosiveness * amount / 2.0F);
            }

            float radius = tilesize * building.block.size / 2.0F;
            if (state.rules.damageExplosions) {
                for (int i = 0; i < Mathf.clamp(flammability / 4, 0, 30); i++) {
                    Time.run(i / 2f, () -> Call.createBullet(Bullets.fireball, Team.derelict, building.x, building.y, Mathf.random(360f), Bullets.fireball.damage, 1, 1));
                }

                int waves = Mathf.clamp((int) (explosiveness / 4), 0, 30);

                for (int i = 0; i < waves; i++) {
                    int f = i;
                    Time.run(i * 2f, () -> Call.effect(Fx.blockExplosionSmoke, building.x + Mathf.range(radius), building.y + Mathf.range(radius), 0, Color.white));
                }
            }

            if (explosiveness > 15f) {
                Call.effect(Fx.shockwave, building.x, building.y, 0, Color.white);
            }

            if (explosiveness > 30f) {
                Call.effect(Fx.bigShockwave, building.x, building.y, 0, Color.white);
            }

            Call.effectReliable(Fx.dynamicExplosion, building.x, building.y, radius / 8f, Color.white);

            tile.setNet(Blocks.air);
        }
    }
}
