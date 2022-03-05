package main.old;

import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Timer;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType;
import mindustry.game.Schematic;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.type.ItemStack;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

import java.util.Arrays;
import java.util.HashMap;

import static mindustry.Vars.state;
import static mindustry.Vars.world;

public class SectorManager {
    private static final int coreShardRadius = 28;
    private static final int coreFoundationRadius = 32;
    private static final int coreNucleusRadius = 38;

    protected static final HashMap<CoreBlock, Integer> radii = new HashMap<CoreBlock, Integer>() {{
        put((CoreBlock) Blocks.coreShard, coreShardRadius);
        put((CoreBlock) Blocks.coreFoundation, coreFoundationRadius);
        put((CoreBlock) Blocks.coreNucleus, coreNucleusRadius);
    }};

    private static int[][] sectors;
    private static int[][] overlaps;

    private static final int cellSize = 50;
    private static Seq<Point2> spawnSeq = new Seq<>();
    private static int spawningSearchStartIndex = -1;

    public static void reset() {
        sectors = new int[world.width()][world.height()];
        overlaps = new int[world.width()][world.height()];

        Arrays.stream(sectors).forEach(array -> Arrays.fill(array, 0));
        Arrays.stream(overlaps).forEach(array -> Arrays.fill(array, 0));

        spawningSearchStartIndex = -1;

        spawnSeq = new Seq<>();
        for (int x = 0; x + cellSize <= world.width(); x += cellSize) {
            for (int y = 0; y + cellSize <= world.height(); y += cellSize) {
                int rx = Mathf.random(cellSize / 2) - cellSize / 4 + x + cellSize / 2;
                int ry = Mathf.random(cellSize / 2) - cellSize / 4 + y + cellSize / 2;

                BuildPlan buildPlan = new BuildPlan(rx, ry, 0, Blocks.multiplicativeReconstructor);
                if (buildPlan.placeable(Team.sharded) && world.tile(rx, ry).floor().hasSurface())
                    spawnSeq.add(new Point2(rx, ry));
            }
        }

        spawnSeq.sort((Point2 a, Point2 b) -> Math.max(Math.abs(world.width() / 2 - b.x), Math.abs(world.width() / 2 - b.y)) - Math.max(Math.abs(world.width() / 2 - a.x), Math.abs(world.width() / 2 - a.y)));

        for (int i = 0; i < spawnSeq.size - 1; i++) {
            Point2 a = spawnSeq.get(i);
            int r = Math.min(Mathf.random(spawnSeq.size / 10) + i, spawnSeq.size - 1);
            Point2 b = spawnSeq.get(r);

            spawnSeq.set(i, b);
            spawnSeq.set(r, a);
        }

        for (int i = 0; i < 3; i++) {
            Tile tile = world.tile(spawnSeq.get(spawnSeq.size - 1).pack());
            spawnSeq.remove(spawnSeq.size - 1);
            tile.setOverlay(Blocks.spawn);
        }

        Events.fire(new EventType.WorldLoadEvent());
    }

    public static Point2 getNextFreeSpawn() {
        spawningSearchStartIndex = (spawningSearchStartIndex + 1) % spawnSeq.size;

        for (int i = 0; i < spawnSeq.size; i++) {
            Point2 p = spawnSeq.get((i + spawningSearchStartIndex) % spawnSeq.size);

            int dxp = Math.min(p.x + coreNucleusRadius, world.width() - 1);
            int dyp = Math.min(p.y + coreNucleusRadius, world.height() - 1);
            int dxm = Math.max(p.x - coreNucleusRadius, 0);
            int dym = Math.max(p.y - coreNucleusRadius, 0);

            if (sectors[p.x][p.y] == 0 &&
                    sectors[dxp][dyp] == 0 &&
                    sectors[dxp][dym] == 0 &&
                    sectors[dxm][dyp] == 0 &&
                    sectors[dxm][dym] == 0 &&
                    sectors[dxp][p.y] == 0 &&
                    sectors[dxm][p.y] == 0 &&
                    sectors[p.x][dyp] == 0 &&
                    sectors[p.x][dym] == 0) {
                return p;
            }
        }

        return null;
    }

    public static boolean validPlace(Team team, int x, int y) {
        return sectors[x][y] == team.id;
    }

    public static boolean isEmpty(int x, int y) {
        return sectors[x][y] == 0;
    }

    public static boolean isBorder(int x, int y) {
        return sectors[x][y] == -1;
    }

    private static boolean isBorder(Team team, int tileX, int tileY) {
        return isBorder(team, tileX, tileY, false, false);
    }

    private static boolean isBorder(Team team, int tileX, int tileY, boolean broken, boolean any) {
        int c = sectors[tileX][tileY];

        if (!broken && c != -1) return false;

        if (any) {
            boolean enemy = false;
            boolean ally = false;

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    int n = sectors[tileX + x][tileY + y];

                    if ((n != team.id && n != 0 && n != -1)) enemy = true;
                    if ((n == team.id)) ally = true;
                }
            }

            return enemy && ally;
        } else {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    int n = sectors[tileX + x][tileY + y];

                    if (!(n == 0 || n == -1 || n == team.id)) return false;
                }
            }
            return true;
        }
    }

    private static void assignTeam(Team team, int x, int y) {
        if (sectors[x][y] <= 0 || sectors[x][y] == team.id) {
            sectors[x][y] = team.id;
            if (overlaps[x][y] == 0) SectorizedTeamManager.getTeam(team).tilesCaptured++;
            overlaps[x][y] += 1;
        }
    }

    private static void detachTeam(Team team, int x, int y) {
        if (sectors[x][y] == team.id) {
            overlaps[x][y] -= 1;
            if (overlaps[x][y] == 0) {
                sectors[x][y] = 0;
                SectorizedTeamManager.getTeam(team).tilesCaptured--;
            }
        }
    }

    private static void assignBorder(int x, int y) {
        if (sectors[x][y] <= 0) overlaps[x][y] += 1;
        sectors[x][y] = -1;
    }

    private static void detachBorder(int x, int y) {
        if (sectors[x][y] == -1) {
            overlaps[x][y] -= 1;
            if (overlaps[x][y] == 0) sectors[x][y] = 0;
        }
    }

    public static void assignArea(Team team, CoreBlock core, int coreX, int coreY, boolean spawn) {
        int size = radii.get(core);

        for (int x = Math.max(coreX - size - 1, 1); x <= Math.min(world.width() - 2, coreX + size + 1); x++) {
            for (int y = Math.max(coreY - size - 1, 1); y <= Math.min(world.height() - 2, coreY + size + 1); y++) {
                Tile tile = world.tile(x, y);
                boolean bx = x < coreX - size || x > coreX + size;
                boolean by = y < coreY - size || y > coreY + size;

                if (isBorder(tile.x, tile.y)) {
                    if (isBorder(team, tile.x, tile.y)) {
                        if (!bx && !by) {
                            if (tile.block() == Blocks.shockMine) tile.setNet(Blocks.air);
                            assignTeam(team, x, y);
                        } else {
                            assignBorder(x, y);
                        }
                    } else {
                        if (tile.build == null) tile.setNet(Blocks.shockMine, team, 0);
                        assignBorder(x, y);
                    }
                } else {
                    if (isEmpty(x, y)) {
                        if ((bx && (y / 2) % 2 == 0) || (by && (x / 2) % 2 == 0))
                            tile.setNet(Blocks.shockMine, team, 0);

                        if (bx || by) assignBorder(x, y);
                        else assignTeam(team, x, y);
                    } else {
                        assignTeam(team, x, y);
                    }
                }
            }
        }

        Tile coreWorldTile = world.tile(coreX, coreY);
        Call.effectReliable(Fx.launch, coreWorldTile.getX(), coreWorldTile.getY(), 0, Color.white);

        if (spawn) {
            Schematic start = SectorizedLoadout.getStartingBase(state.wave);

            Schematic.Stile coreTile = start.tiles.find(s -> s.block instanceof CoreBlock);
            if (coreTile == null) throw new IllegalArgumentException("Schematic has no core tile. Exiting.");
            int ox = coreWorldTile.x - coreTile.x, oy = coreWorldTile.y - coreTile.y;
            start.tiles.each(st -> {
                Tile tile = world.tile(st.x + ox, st.y + oy);
                if (tile == null) return;

                if (tile.block() != Blocks.air) {
                    tile.removeNet();
                }

                tile.setNet(st.block, team, st.rotation);

                if (st.config != null) {
                    tile.build.configureAny(st.config);
                }
                if (tile.block() instanceof CoreBlock) {
                    for (ItemStack stack : state.rules.loadout) {
                        Call.setItem(tile.build, stack.item, stack.amount);
                    }
                }
            });
        } else {
            coreWorldTile.setNet(core, team, 0);
        }

        SectorizedTeamManager.getTeam(team).addCore();
    }

    public static void detachArea(CoreBlock.CoreBuild core) {
        int size = radii.get(core.block());

        int coreX = core.tile.x;
        int coreY = core.tile.y;
        Team team = core.team();

        for (int x = Math.max(coreX - size - 1, 1); x <= Math.min(world.width() - 2, coreX + size + 1); x++) {
            for (int y = Math.max(coreY - size - 1, 1); y <= Math.min(world.height() - 2, coreY + size + 1); y++) {
                if (isBorder(x, y)) detachBorder(x, y);
                else detachTeam(team, x, y);
            }
        }

        for (int x = Math.max(coreX - size - 1, 1); x <= Math.min(world.width() - 2, coreX + size + 1); x++) {
            for (int y = Math.max(coreY - size - 1, 1); y <= Math.min(world.height() - 2, coreY + size + 1); y++) {
                Tile tile = world.tile(x, y);

                int c = sectors[x][y];
                int cx1 = sectors[x + 1][y];
                int cx2 = sectors[x - 1][y];
                int cy1 = sectors[x][y + 1];
                int cy2 = sectors[x][y - 1];

                if (c == team.id) {
                    if (cx1 == 0 || cx2 == 0) {
                        if (((y + 2) / 2) % 2 == 0) tile.setNet(Blocks.shockMine, team, 0);
                        assignBorder(x, y);
                    } else if (cy1 == 0 || cy2 == 0) {
                        if (((x + 2) / 2) % 2 == 0) tile.setNet(Blocks.shockMine, team, 0);
                        assignBorder(x, y);
                    }
                } else if (isBorder(team, x, y, false, true)) {
                    if (tile.build == null) tile.setNet(Blocks.shockMine, team, 0);
                } else if (tile.team() == team) {
                    if (tile.build != null) {
                        Timer.schedule(() -> {
                            if (tile.team() == team) {
                                if (tile.block() == Blocks.shockMine) tile.setNet(Blocks.air);
                                else tile.build.kill();
                            }
                        }, Mathf.random(3.0f));
                    }
                }
            }
        }

        Call.effectReliable(Fx.nuclearcloud, core.tile().getX(), core.tile().getY(), 0, Color.white);
        SectorizedTeam sectorizedTeam = SectorizedTeamManager.getTeam(team);
        sectorizedTeam.removeCore();
    }
}
