package sectorized.sector.core;

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
import sectorized.constant.Constants;
import sectorized.sector.core.objects.Border;
import sectorized.sector.core.objects.Rectangle;
import sectorized.sector.core.objects.SubRectangle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;

import static mindustry.Vars.*;

public class SectorLogic {
    private final int[][] sectors;
    private final GridAccelerator gridAccelerator;
    private final LinkedHashMap<Integer, Rectangle> coreBuildRectangleMap = new LinkedHashMap<>();
    private int nextZIndex = 0;

    public SectorLogic(GridAccelerator gridAccelerator) {
        sectors = new int[world.width()][world.height()];
        this.gridAccelerator = gridAccelerator;

        Arrays.stream(sectors).forEach(array -> Arrays.fill(array, 0));
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

    public boolean validBorder(int x, int y, int teamId) {
        if (sectors[x][y] != -1) return false;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (sectors[Math.max(0, Math.min(x + dx, world.width() - 1))][Math.max(0, Math.min(y + dy, world.height() - 1))] == teamId) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addArea(int coreX, int coreY, CoreBlock coreBlock, int teamId) {
        int size = Constants.radii.get(coreBlock);

        Rectangle addRectangle = new Rectangle(Math.max(coreX - size, 0),
                Math.max(coreY - size, 0),
                Math.min(coreX + size, world.width() - 1),
                Math.min(coreY + size, world.height() - 1),
                teamId,
                nextZIndex++);

        coreBuildRectangleMap.put(Point2.pack(coreX, coreY), addRectangle);

        SubRectangle[] subRectangles = gridAccelerator.getIntersectingRectangles(addRectangle);
        gridAccelerator.addRectangle(addRectangle);

        HashSet<Border> borderHashSet = new HashSet<>(size * 6);

        addRectangle.iterate((x, y, borderX, borderY) -> {
            Tile tile = world.tile(x, y);
            if (tile.block() == Blocks.shockMine) {
                destroyTileWithoutPolyRebuild(tile);
            }

            if (borderX || borderY) {
                borderHashSet.add(new Border(x, y, borderX, borderY, addRectangle.teamId));
            } else {
                sectors[x][y] = addRectangle.teamId;
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
                    gridAccelerator.removeRectangle(removeRectangle);
                    addArea(coreX, coreY, coreBlock, teamId);

                    return;
                }
            }
        }
    }

    public void removeArea(int coreX, int coreY) {
        Tile coreTile = world.tile(coreX, coreY);
        int size = Constants.radii.get((CoreBlock) coreTile.block());
        Rectangle removeRectangle = coreBuildRectangleMap.remove(coreTile.pos());

        gridAccelerator.removeRectangle(removeRectangle);
        SubRectangle[] subRectangles = gridAccelerator.getIntersectingRectangles(removeRectangle);

        removeRectangle.iterate((x, y, borderX, borderY) -> {
            Tile tile = world.tile(x, y);
            if (tile.block() == Blocks.shockMine) {
                destroyTileWithoutPolyRebuild(tile);
            }
            sectors[x][y] = 0;
        });

        HashSet<Border> borderHashSet = new HashSet<>(size * 6);
        handleSubRectanglesAndBorder(subRectangles, borderHashSet);

        removeRectangle.iterate((x, y, borderX, borderY) -> {
            int sector = sectors[x][y];

            if (!(borderX || borderY) && sector >= 0 && sector != world.tile(x, y).team().id) {
                destroyTileWithoutPolyRebuild(world.tile(x, y));
            }
        });
    }

    private boolean validPlace(int x, int y, int teamId) {
        return sectors[x][y] == teamId;
    }

    private void handleSubRectanglesAndBorder(SubRectangle[] subRectangles, HashSet<Border> borderHashSet) {
        for (SubRectangle subRectangle : subRectangles) {
            subRectangle.iterate((x, y, borderX, borderY) -> {
                Border border = new Border(x, y, borderX, borderY, subRectangle.teamId, sectors[x][y]);

                borderHashSet.remove(border);

                if ((borderX || borderY) && sectors[x][y] != subRectangle.teamId) borderHashSet.add(border);
                else sectors[x][y] = subRectangle.teamId;
            });
        }

        for (Border border : borderHashSet) {
            sectors[border.x][border.y] = -1;
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
