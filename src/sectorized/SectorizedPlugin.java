package sectorized;

import arc.Core;
import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.core.GameState;
import mindustry.mod.Plugin;
import mindustry.net.Administration;
import sectorized.constant.Config;
import sectorized.constant.DiscordBot;
import sectorized.constant.Rules;
import sectorized.constant.State;
import sectorized.faction.FactionManager;
import sectorized.sector.SectorManager;
import sectorized.update.UpdateManager;
import sectorized.world.WorldManager;

import java.io.IOException;
import java.net.BindException;
import java.util.concurrent.TimeUnit;

import static arc.util.Log.err;
import static arc.util.Log.info;
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
        Log.info(Config.c.toString());

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
            Rules.setSpawnGroups(state.rules);
            state.rules.infiniteResources = Config.c.infiniteResources;

            Core.settings.put("playerlimit", 50);

            while (true) {
                try {
                    net.host(Administration.Config.port.num());
                    info("Opened a server on port @.", Administration.Config.port.num());
                    break;
                } catch (BindException e) {
                    err("Unable to host: Port " + Administration.Config.port.num() + " already in use! Make sure no other servers are running on the same port in your network.");
                    state.set(GameState.State.menu);
                } catch (IOException e) {
                    err(e);
                    state.set(GameState.State.menu);
                }

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            logic.play();

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
