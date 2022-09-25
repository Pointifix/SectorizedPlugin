package sectorized.sector;

import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.CommandHandler;
import arc.util.Timer;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Planets;
import mindustry.content.UnitTypes;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.world.blocks.storage.CoreBlock;
import sectorized.Manager;
import sectorized.SectorizedEvents;
import sectorized.constant.CoreCost;
import sectorized.constant.MessageUtils;
import sectorized.constant.State;
import sectorized.constant.VaultLogic;
import sectorized.sector.core.GridAccelerator;
import sectorized.sector.core.SectorLogic;
import sectorized.sector.core.SectorSpawns;

import java.util.HashMap;

import static mindustry.Vars.netServer;

public class SectorManager implements Manager {
    private SectorLogic sectorLogic;
    private SectorSpawns sectorSpawns;

    private HashMap<Integer, Double> bufferedCoresPlacement;

    @Override
    public void init() {
        Events.on(SectorizedEvents.BiomesGeneratedEvent.class, event -> {
            GridAccelerator gridAccelerator = new GridAccelerator();
            sectorLogic = new SectorLogic(gridAccelerator);
            sectorSpawns = new SectorSpawns(gridAccelerator);

            bufferedCoresPlacement = new HashMap<>();
        });

        Events.on(SectorizedEvents.NewMemberEvent.class, event -> {
            try {
                Point2 spawnPoint = sectorSpawns.getNextFreeSpawn();

                Events.fire(new SectorizedEvents.MemberSpawnedEvent(spawnPoint, event.member));

                if (State.planet.equals(Planets.serpulo.name)) {
                    sectorLogic.addArea(spawnPoint.x, spawnPoint.y, (CoreBlock) Blocks.coreFoundation, event.member.faction.team.id);
                } else if (State.planet.equals(Planets.erekir.name)) {
                    sectorLogic.addArea(spawnPoint.x, spawnPoint.y, (CoreBlock) Blocks.coreAcropolis, event.member.faction.team.id);
                }

            } catch (SectorSpawns.NoSpawnPointAvailableException e) {
                Events.fire(new SectorizedEvents.NoSpawnPointAvailableEvent(event.member));
            }
        });

        Events.on(EventType.BlockBuildBeginEvent.class, event -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            if (event.unit.type == UnitTypes.poly && !event.unit.plans.isEmpty()) {
                BuildPlan plan = event.unit.plans.first();

                if (!sectorLogic.validPlace(plan.x, plan.y, plan.block, event.unit.team().id)) {
                    event.unit.plans.removeFirst();
                }
            }

            if (!sectorLogic.validPlace(event.tile.x, event.tile.y, event.tile.cblock() != null ? event.tile.cblock() : Blocks.conveyor, event.unit.team().id)) {
                event.tile.setNet(Blocks.air);
            }

            if (event.tile.block() == Blocks.coreFoundation || event.tile.block() == Blocks.coreNucleus) {
                sectorLogic.upgradeArea(event.tile.x, event.tile.y, (CoreBlock) event.tile.block(), event.team.id);
            }
        });

        Events.on(EventType.BlockDestroyEvent.class, event -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            if (event.tile.block() == Blocks.shockMine) {
                Team team = event.tile.team();

                Timer.schedule(() -> {
                    if (sectorLogic.validBorder(event.tile.x, event.tile.y, team.id)) {
                        Call.effectReliable(Fx.tapBlock, event.tile.getX(), event.tile.getY(), 0, Color.red);
                        event.tile.setNet(Blocks.shockMine, team, 0);
                    }
                }, Mathf.random(10.0f, 15.0f));
            }
        });

        Events.on(SectorizedEvents.CoreDestroyEvent.class, event -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            sectorLogic.removeArea(event.coreBuild.tile.x, event.coreBuild.tile.y);
        });

        netServer.admins.addActionFilter(action -> {
            if (State.gameState == State.GameState.GAMEOVER) return false;
            if (State.gameState == State.GameState.INACTIVE) return true;

            switch (action.type) {
                case placeBlock:
                    if (!sectorLogic.validPlace(action.tile.x, action.tile.y, action.block, action.player.team().id)) {
                        MessageUtils.sendBufferedMessage(action.player, "You cannot build outside your sector! To expand your sector place a " + MessageUtils.cHighlight3 + "vault" + MessageUtils.cDefault + " \uF866 within the borders of your sector!", MessageUtils.MessageLevel.WARNING);
                        return false;
                    }

                    if ((action.block == Blocks.vault || action.block == Blocks.reinforcedVault) && action.tile.cblock() == Blocks.air && !VaultLogic.adjacentToCore(action.tile.x, action.tile.y, action.block)) {
                        if (bufferedCoresPlacement.containsKey(action.player.team().id)) {
                            int seconds = 10 - (int) Math.floor((State.time - bufferedCoresPlacement.get(action.player.team().id)) / 60);

                            MessageUtils.sendBufferedMessage(action.player, "Wait " + MessageUtils.cInfo + seconds + " seconds" + MessageUtils.cDefault + " to place a new core!", MessageUtils.MessageLevel.WARNING, 1);
                            action.tile.setNet(Blocks.air);
                        } else if (CoreCost.checkAndConsumeFunds(action.player.team())) {
                            if (State.planet.equals(Planets.serpulo.name)) {
                                action.tile.setNet(Blocks.coreShard, action.player.team(), 0);
                            } else if (State.planet.equals(Planets.erekir.name)) {
                                action.tile.setNet(Blocks.coreBastion, action.player.team(), 0);
                            }

                            sectorLogic.addArea(action.tile.x, action.tile.y, (CoreBlock) Blocks.coreShard, action.player.team().id);

                            int team = action.player.team().id;
                            bufferedCoresPlacement.put(team, State.time);
                            Timer.schedule(() -> bufferedCoresPlacement.remove(team), 10);

                            Events.fire(new SectorizedEvents.CoreBuildEvent(action.tile));
                        } else {
                            MessageUtils.sendBufferedMessage(action.player, "Insufficient resources for a new core", MessageUtils.MessageLevel.WARNING, 1);
                            action.tile.setNet(Blocks.air);
                        }

                        return false;
                    }
                    break;
                case breakBlock:
                    if (action.block == Blocks.shockMine) {
                        return false;
                    }
                    break;
            }

            return true;
        });
    }

    @Override
    public void reset() {

    }

    @Override
    public void registerServerCommands(CommandHandler handler) {

    }

    @Override
    public void registerClientCommands(CommandHandler handler) {

    }
}
