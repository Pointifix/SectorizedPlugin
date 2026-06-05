package sectorized;

import arc.ApplicationListener;
import arc.Core;
import arc.Events;
import arc.backend.headless.HeadlessApplication;
import arc.util.CommandHandler;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Groups;
import mindustry.core.GameState;
import mindustry.core.Logic;
import mindustry.core.NetServer;
import mindustry.core.UI;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Player;
import mindustry.net.ArcNetProvider;
import mindustry.net.Net;
import mindustry.net.NetConnection;
import mindustry.ui.Fonts;
import sectorized.Manager;
import sectorized.SectorizedEvents;
import sectorized.constant.Config;
import sectorized.constant.State;
import sectorized.faction.FactionManager;
import sectorized.faction.core.Member;
import sectorized.faction.core.Faction;

import java.io.IOException;
import java.util.Arrays;

import static arc.util.Log.err;
import static arc.util.Log.info;
import static mindustry.Vars.*;

public class PlayTest implements ApplicationListener {
    private SectorizedPlugin plugin;
    private boolean sectorizedStarted = false;
    private int botIndex = 0;

    public static void main(String[] args) {
        Config.reset();
        Config.c.database.enabled = false;
        Config.c.discord.enabled = false;

        Vars.platform = new mindustry.core.Platform() {};
        Vars.net = new Net(new ArcNetProvider());

        Log.logger = (level, text) -> System.out.println("[" + level.name() + "] " + text);

        new HeadlessApplication(new PlayTest(), throwable -> {
            err("Fatal error", throwable);
            System.exit(1);
        });
    }

    @Override
    public void init() {
        Core.settings.setDataDirectory(Core.files.local("config"));
        loadLocales = false;
        headless = true;

        Vars.loadSettings();
        Vars.init();

        UI.loadColors();
        Fonts.loadContentIconsHeadless();

        content.createBaseContent();
        mods.loadScripts();
        content.createModContent();
        content.init();

        bases.load();

        Core.app.addListener(new ApplicationListener() {
            @Override
            public void update() {
                asyncCore.begin();
            }
        });

        logic = new Logic();
        Core.app.addListener(logic);

        netServer = new NetServer();
        Core.app.addListener(netServer);

        Core.app.addListener(new ApplicationListener() {
            @Override
            public void update() {
                asyncCore.end();
            }
        });

        plugin = new SectorizedPlugin();
        plugin.init();
        plugin.registerServerCommands(new CommandHandler(""));

        Events.fire(new EventType.ServerLoadEvent());

        info("Starting sectorized game...");
        startSectorizedGame();

        Time.runTask(3f, () -> {
            try {
                createBot("Bot-Alpha", "00000000-0000-0000-0000-000000000001");
                createBot("Bot-Bravo", "00000000-0000-0000-0000-000000000002");
                createBot("Bot-Charlie", "00000000-0000-0000-0000-000000000003");
                createBot("Bot-Delta", "00000000-0000-0000-0000-000000000004");
                info("Bot players created.");
            } catch (Exception e) {
                err("Failed to create bot players", e);
            }
        });

        Time.runTask(5f, () -> {
            info("");
            info("============================================================");
            info("  Server is running!");
            info("  Connect your desktop Mindustry client to: localhost:6567");
            info("============================================================");
            info("");

            // Start periodic status logging every 5s
            logBotStatus();
        });
    }

    private void logBotStatus() {
        for (Player p : Groups.player) {
            String unitStatus = p.unit() != null ? "unit=" + p.unit().type.name : "unit=null (dead)";
            String coreStatus = p.bestCore() != null ? "core=yes" : "core=no";
            String teamStr = "team=" + p.team().id;
            info("Bot '@' " + teamStr + " " + coreStatus + " " + unitStatus, p.name());
        }
        Time.runTask(5f, this::logBotStatus);
    }

    private void startSectorizedGame() {
        logic.reset();
        state.rules = sectorized.constant.Rules.rules.copy();
        for (Manager manager : plugin.managers) {
            manager.reset();
        }
        Events.fire(new SectorizedEvents.GamemodeStartEvent());
        sectorized.constant.Rules.setSpawnGroups(state.rules);
        state.rules.infiniteResources = Config.c.game.infiniteResources;
        state.set(GameState.State.paused);
        Core.settings.put("playerlimit", Config.c.game.playerLimit);

        try {
            net.host(6567);
        } catch (IOException e) {
            err("Failed to host server", e);
            return;
        }

        logic.play();
        State.gameState = State.GameState.ACTIVE;
        sectorizedStarted = true;
        info("Sectorized game started on port 6567");
    }

    private void createBot(String name, String uuid) {
        Player player = Player.create();
        player.name(name);
        player.con(new BotConnection(uuid));
        player.team(Team.sharded);
        player.add();
        Events.fire(new EventType.PlayerJoin(player));

        FactionManager factionManager = (FactionManager) java.util.Arrays.stream(plugin.managers)
                .filter(m -> m instanceof FactionManager).findFirst().get();
        Member member = factionManager.getMember(player);
        Events.fire(new SectorizedEvents.NewMemberEvent(member));

        info("Bot '@' joined the game", name);
    }

    @Override
    public void update() {
    }

    static class BotConnection extends NetConnection {
        public BotConnection(String uuid) {
            super("127.0.0.1");
            this.uuid = uuid;
            this.usid = uuid;
            this.hasConnected = true;
            this.hasBegunConnecting = true;
        }

        @Override
        public void send(Object object, boolean reliable) {
        }

        @Override
        public void close() {
        }

        @Override
        public boolean isConnected() {
            return true;
        }
    }
}
