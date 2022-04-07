package sectorized.world;

import arc.Events;
import arc.util.CommandHandler;
import sectorized.Manager;
import sectorized.SectorizedEvents;
import sectorized.constant.Constants;
import sectorized.world.map.MapGenerator;

import static mindustry.Vars.world;

public class WorldManager implements Manager {
    @Override
    public void init() {
        Events.on(SectorizedEvents.GamemodeStartEvent.class, event -> {
            MapGenerator mapGenerator = new MapGenerator();
            world.loadGenerator(Constants.mapWidth, Constants.mapHeight, mapGenerator);
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
