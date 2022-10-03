package sectorized;

import arc.Events;
import arc.util.CommandHandler;
import mindustry.mod.Plugin;
import sectorized.constant.Config;
import sectorized.constant.DiscordBot;
import sectorized.constant.Rules;
import sectorized.constant.State;
import sectorized.faction.FactionManager;
import sectorized.sector.SectorManager;
import sectorized.update.UpdateManager;
import sectorized.world.WorldManager;

import static mindustry.Vars.*;

public class SectorizedPlugin extends Plugin {
    private final Manager[] managers = new Manager[]{
            new WorldManager(),
            new FactionManager(),
            new SectorManager(),
            new UpdateManager()
    };

    @Override
    public void init() {
        System.out.println(Config.c.toString());

        DiscordBot.init();

        for (Manager manager : managers) {
            manager.init();
        }
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        for (Manager manager : managers) {
            manager.registerServerCommands(handler);
        }

        handler.register("sectorized", "Hosts the sectorized gamemode.", args -> {
            logic.reset();
            state.rules = Rules.rules.copy();

            for (Manager manager : this.managers) {
                manager.reset();
            }

            Events.fire(new SectorizedEvents.GamemodeStartEvent());

            state.serverPaused = true;

            Rules.setSpawnGroups(state.rules);

            logic.play();
            netServer.openServer();

            State.gameState = State.GameState.ACTIVE;
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        for (Manager manager : managers) {
            manager.registerClientCommands(handler);
        }
    }
}
