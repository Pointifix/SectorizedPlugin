package sectorized.constant;

import arc.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Config {
    public static Config c;

    public Database database = new Database();
    public Discord discord = new Discord();
    public Game game = new Game();
    public World world = new World();
    public Multiplier multiplier = new Multiplier();
    public Turret turret = new Turret();
    public Scoring scoring = new Scoring();
    public Interval interval = new Interval();
    public UnitCap unitCap = new UnitCap();
    public Vote vote = new Vote();
    public Misc misc = new Misc();
    public Loadout loadout = new Loadout();
    public CoreCost coreCost = new CoreCost();
    public Unit unit = new Unit();

    public static class Database {
        public boolean enabled = true;
        public boolean updateScoreDecay = true;
        public int retries = 5;
        public int retryDelayMs = 3000;
        public String driver = "org.mariadb.jdbc.Driver";
    }

    public static class Discord {
        public boolean enabled = true;
        public int[] rankRoleThreshold = {500, 200, 100, 50, 25, 10};
    }

    public static class Game {
        public boolean infiniteResources = false;
        public int playerLimit = 50;
        public int waveSpacing = 60 * 60 * 3;
        public double planetChanceSerpulo = 0.7;
        public int maxTeamSize = 4;
        public int coreTierSize = 25;
        public int gracePeriod = 60 * 60 * 5;
        public int respawnCooldown = 60 * 5;
        public int shockMineReArmDelayMin = 10;
        public int shockMineReArmDelayMax = 15;
        public int playerDisconnectCoreDestroyDelay = 30;
        public int joinRequestTimeout = 30;
        public int gameOverStartWave = 5;
        public int factionGracePeriodMaxCores = 3;
        public CorePlacementCooldown corePlacementCooldown = new CorePlacementCooldown();
        public Domination domination = new Domination();
        public Attack attack = new Attack();

        public static class CorePlacementCooldown {
            public int serpulo = 10;
            public int erekir = 15;
            public int initial = 30;
        }

        public static class Domination {
            public CoreLead coreLead = new CoreLead();
            public int healthThreshold = 10000;
            public int healthPerWave = 1000;
            public int checkStartWave = 15;
            public int checkMinPlayers = 2;

            public static class CoreLead {
                public int serpulo = 3;
                public int erekir = 5;
            }
        }

        public static class Attack {
            public double rtsMinWeight = 1.2;
            public int squadMinSize = 1;
            public int squadMaxSize = 100;
        }
    }

    public static class World {
        public int mapWidth = 600;
        public int mapHeight = 600;
        public int spawnCellSize = 50;
        public int dropZoneRadius = 100;
        public int obstacleCheckRadius = 5;
        public int spawnSurfaceDivisor = 300;
        public int gridCellSize = 100;
        public int biomeSampleDensity = 50;
        public double biomeDisplayThreshold = 0.1;
        public int biomeDisplayMaxCount = 3;
        public int voteOptimizationRetries = 10;
        public Radius radius = new Radius();
        public SectorMap sectorMap = new SectorMap();

        public static class Radius {
            public int coreShard = 28;
            public int coreFoundation = 33;
            public int coreNucleus = 38;
            public int coreBastion = 30;
            public int coreCitadel = 38;
            public int coreAcropolis = 42;
        }

        public static class SectorMap {
            public int sampleDensity = 50;
        }
    }

    public static class Multiplier {
        public double buildSpeed = 2.0;
        public double blockDamage = 2.0;
        public double unitDamage = 0.5;
        public double buildCost = 1.0;
        public double blockDamageFormulaA = 1.0;
        public double blockDamageFormulaB = 0.05;
        public double blockDamageFormulaC = 1.0;
        public double unitDamageFormulaA = 3.0;
        public double unitDamageFormulaB = 2.0;
        public double unitDamageFormulaC = 0.02;
        public double unitHealthFormulaA = 6.0;
        public double unitHealthFormulaB = 5.0;
        public double unitHealthFormulaC = 0.02;
    }

    public static class Turret {
        public double foreshadowAmmoDamage = 1.5;
        public double spectreAmmoDamage = 2.0;
        public double meltdownShootTypeDamage = 1.5;
        public double reconstructorConstructTime = 0.75;
    }

    public static class Scoring {
        public int k = 10;
        public int offset = 10;
        public int eloDiffClamp = 2000;
        public double winnerMultiplier = 1.5;
        public double decayMultiplier = 0.99;
        public int decayThreshold = 100;
        public int leaderboardPageSize = 10;
        public int leaderboardLimit = 100;
        public double survivalMultiplier = 0.2;
        public double survivalExponent = 1.6;
    }

    public static class Interval {
        public int hudPopup = 60 * 5;
        public int infoMessage = 60 * 60 * 8;
        public int dominationCheck = 60 * 60 * 2;
        public int gameStateLog = 60 * 60 * 5;
    }

    public static class UnitCap {
        public int initial = 0;
        public Modifier modifier = new Modifier();

        public static class Modifier {
            public int coreShard = 4;
            public int coreFoundation = 6;
            public int coreNucleus = 8;
            public int coreBastion = 3;
            public int coreCitadel = 5;
            public int coreAcropolis = 7;
        }
    }

    public static class Vote {
        public int displayPeriodSec = 3;
        public int displayDurationSec = 20;
        public int restartCountdownDelaySec = 20;
        public int restartCountdownTicks = 10;
        public int restartCooldownSec = 30;
    }

    public static class Misc {
        public int messageBufferDuration = 3;
    }

    public static class Loadout {
        public Serpulo serpulo = new Serpulo();
        public Erekir erekir = new Erekir();

        public static class Item {
            public int base;
            public int perWave;
            public int waveRequired = -1;
            public int waveOffset = -1;
        }

        public static class Serpulo {
            public Item copper = item(800, 150);
            public Item lead = item(500, 100);
            public Item graphite = item(150, 20);
            public Item silicon = item(150, 30);
            public Item metaglass = item(100, 10);
            public Item titanium = item(50, 20);
            public Item thorium = item(10, 15);
            public Item plastanium = waveItem(0, 20, 5, 4);
            public Item phaseFabric = waveItem(0, 15, 10, 9);
            public Item surgeAlloy = waveItem(0, 15, 10, 9);
        }

        public static class Erekir {
            public Item beryllium = item(300, 100);
            public Item graphite = item(100, 50);
            public Item silicon = item(50, 30);
            public Item thorium = item(150, 20);
            public Item tungsten = waveItem(0, 30, 1, 0);
            public Item oxide = waveItem(0, 20, 3, 2);
            public Item carbide = waveItem(0, 10, 5, 4);
            public Item surgeAlloy = waveItem(0, 20, 7, 7);
            public Item phaseFabric = waveItem(0, 10, 9, 4);
        }

        private static Item item(int base, int perWave) {
            Item i = new Item();
            i.base = base;
            i.perWave = perWave;
            return i;
        }

        private static Item waveItem(int base, int perWave, int waveRequired, int waveOffset) {
            Item i = new Item();
            i.base = base;
            i.perWave = perWave;
            i.waveRequired = waveRequired;
            i.waveOffset = waveOffset;
            return i;
        }
    }

    public static class CoreCost {
        public Serpulo serpulo = new Serpulo();
        public Erekir erekir = new Erekir();

        public static class Tier {
            public int base;
            public int perTier;
            public int minTier = -1;
        }

        public static class Serpulo {
            public Tier copper = tier(200, 100);
            public Tier lead = tier(100, 70);
            public Tier graphite = tier(50, 20, 1);
            public Tier silicon = tier(70, 50, 2);
            public Tier metaglass = tier(50, 30, 3);
            public Tier titanium = tier(200, 40, 5);
            public Tier thorium = tier(100, 40, 6);
            public Tier plastanium = tier(50, 30, 8);
            public Tier phaseFabric = tier(20, 20, 11);
            public Tier surgeAlloy = tier(30, 30, 15);
        }

        public static class Erekir {
            public Tier beryllium = tier(50, 50, 0);
            public Tier graphite = tier(20, 20, 1);
            public Tier silicon = tier(50, 30, 3);
            public Tier oxide = tier(10, 20, 5);
            public Tier carbide = tier(10, 10, 7);
        }

        private static Tier tier(int base, int perTier) {
            Tier t = new Tier();
            t.base = base;
            t.perTier = perTier;
            return t;
        }

        private static Tier tier(int base, int perTier, int minTier) {
            Tier t = new Tier();
            t.base = base;
            t.perTier = perTier;
            t.minTier = minTier;
            return t;
        }
    }

    public static class Unit {
        public SpeedMultiplier speedMultiplier = new SpeedMultiplier();
        public HealthMultiplier healthMultiplier = new HealthMultiplier();

        public static class SpeedMultiplier {
            public double zenith = 0.6;
            public double mega = 0.5;
            public double antumbra = 0.8;
            public double quad = 0.8;
            public double quadExtra = 1.25;
            public double eclipse = 0.7;
            public double crawler = 1.25;
            public double dagger = 1.25;
            public double nova = 1.25;
            public double atrax = 1.25;
            public double mace = 1.25;
            public double pulsar = 1.25;
            public double spiroct = 1.25;
            public double fortress = 1.25;
            public double arkyid = 1.25;
            public double scepter = 1.25;
            public double vela = 1.25;
            public double toxopid = 1.25;
            public double reign = 1.25;
            public double corvus = 1.25;
            public double emanate = 0.6;
        }

        public static class HealthMultiplier {
            public double zenith = 0.5;
            public double mega = 0.8;
            public double antumbra = 0.8;
            public double eclipse = 0.7;
        }
    }

    public static void ensureJson(String path, Object defaults) {
        try {
            java.io.File file = new java.io.File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                Files.write(file.toPath(), new GsonBuilder().setPrettyPrinting().create().toJson(defaults).getBytes());
                Log.warn("[SectorizedPlugin] Created placeholder @. Edit it with your configuration.", file.getName());
            }
        } catch (IOException e) {
            Log.warn("[SectorizedPlugin] Failed to create @: @", path, e.getMessage());
        }
    }

    private static void migrateOldKeys(JsonObject obj) {
        String[][] mappings = {
            {"databaseEnabled", "database", "enabled"},
            {"updateScoreDecay", "database", "updateScoreDecay"},
            {"discordEnabled", "discord", "enabled"},
            {"infiniteResources", "game", "infiniteResources"}
        };
        for (String[] m : mappings) {
            JsonElement old = obj.remove(m[0]);
            if (old != null) {
                JsonObject parent = obj;
                for (int i = 1; i < m.length - 1; i++) {
                    JsonElement child = parent.get(m[i]);
                    if (child == null || !child.isJsonObject()) {
                        JsonObject n = new JsonObject();
                        parent.add(m[i], n);
                        parent = n;
                    } else {
                        parent = child.getAsJsonObject();
                    }
                }
                if (!parent.has(m[m.length - 1])) {
                    parent.add(m[m.length - 1], old);
                }
            }
        }
    }

    static {
        String path = "config/mods/config/sectorized-game-config.json";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                Reader reader = Files.newBufferedReader(Paths.get(path));
                JsonElement parsed = gson.fromJson(reader, JsonElement.class);
                reader.close();
                if (parsed != null && parsed.isJsonObject()) {
                    JsonObject obj = parsed.getAsJsonObject();
                    migrateOldKeys(obj);
                    c = gson.fromJson(obj, Config.class);
                } else {
                    c = new Config();
                }
            } else {
                c = new Config();
            }
        } catch (Exception e) {
            e.printStackTrace();
            c = new Config();
        }

        try {
            java.io.File file = new java.io.File(path);
            file.getParentFile().mkdirs();
            Files.write(Paths.get(path), gson.toJson(c).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reset() {
        c = new Config();
        c.database.enabled = false;
        c.discord.enabled = false;
    }

    @Override
    public String toString() {
        return "Config " + new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
