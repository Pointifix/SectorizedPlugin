package sectorized.constant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Config {
    public static Config c;
    private final HashMap<String, String> values;

    public Config(HashMap<String, String> values) {
        this.values = values;
    }

    public String getString(String key) {
        String val = values.get(key);
        if (val == null) throw new IllegalArgumentException("Config key '" + key + "' does not exist");
        return val;
    }

    public int getInt(String key) {
        String val = values.get(key);
        if (val == null) throw new IllegalArgumentException("Config key '" + key + "' does not exist");
        try { return Integer.parseInt(val); } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Config key '" + key + "' has non-integer value: " + val);
        }
    }

    public long getLong(String key) {
        String val = values.get(key);
        if (val == null) throw new IllegalArgumentException("Config key '" + key + "' does not exist");
        try { return Long.parseLong(val); } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Config key '" + key + "' has non-long value: " + val);
        }
    }

    public float getFloat(String key) {
        String val = values.get(key);
        if (val == null) throw new IllegalArgumentException("Config key '" + key + "' does not exist");
        try { return Float.parseFloat(val); } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Config key '" + key + "' has non-float value: " + val);
        }
    }

    public double getDouble(String key) {
        String val = values.get(key);
        if (val == null) throw new IllegalArgumentException("Config key '" + key + "' does not exist");
        try { return Double.parseDouble(val); } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Config key '" + key + "' has non-double value: " + val);
        }
    }

    public boolean getBool(String key) {
        String val = values.get(key);
        if (val == null) throw new IllegalArgumentException("Config key '" + key + "' does not exist");
        return "true".equalsIgnoreCase(val) || "1".equals(val);
    }

    public String getString(String key, String defaultValue) {
        return values.getOrDefault(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        String val = values.get(key);
        if (val == null) return defaultValue;
        try { return Integer.parseInt(val); } catch (NumberFormatException e) { return defaultValue; }
    }

    public long getLong(String key, long defaultValue) {
        String val = values.get(key);
        if (val == null) return defaultValue;
        try { return Long.parseLong(val); } catch (NumberFormatException e) { return defaultValue; }
    }

    public float getFloat(String key, float defaultValue) {
        String val = values.get(key);
        if (val == null) return defaultValue;
        try { return Float.parseFloat(val); } catch (NumberFormatException e) { return defaultValue; }
    }

    public double getDouble(String key, double defaultValue) {
        String val = values.get(key);
        if (val == null) return defaultValue;
        try { return Double.parseDouble(val); } catch (NumberFormatException e) { return defaultValue; }
    }

    public boolean getBool(String key, boolean defaultValue) {
        String val = values.get(key);
        if (val == null) return defaultValue;
        return "true".equalsIgnoreCase(val) || "1".equals(val);
    }

    private static HashMap<String, String> defaults() {
        HashMap<String, String> d = new HashMap<>();

        d.put("database.enabled", "false");
        d.put("database.updateScoreDecay", "false");
        d.put("database.retries", "5");
        d.put("database.retryDelayMs", "3000");
        d.put("database.driver", "org.mariadb.jdbc.Driver");

        d.put("discord.enabled", "false");
        d.put("discord.rankRoleThreshold.1", "500");
        d.put("discord.rankRoleThreshold.2", "200");
        d.put("discord.rankRoleThreshold.3", "100");
        d.put("discord.rankRoleThreshold.4", "50");
        d.put("discord.rankRoleThreshold.5", "25");
        d.put("discord.rankRoleThreshold.6", "10");

        d.put("game.infiniteResources", "false");
        d.put("game.playerLimit", "50");
        d.put("game.waveSpacing", String.valueOf(60 * 60 * 3));
        d.put("game.corePlacementCooldown.serpulo", "10");
        d.put("game.corePlacementCooldown.erekir", "15");
        d.put("game.corePlacementCooldown.initial", "30");
        d.put("game.domination.coreLead.serpulo", "3");
        d.put("game.domination.coreLead.erekir", "5");
        d.put("game.domination.healthThreshold", "10000");
        d.put("game.domination.healthPerWave", "1000");
        d.put("game.domination.checkStartWave", "15");
        d.put("game.domination.checkMinPlayers", "2");
        d.put("game.gameOverStartWave", "5");
        d.put("game.gracePeriod", String.valueOf(60 * 60 * 5));
        d.put("game.respawnCooldown", String.valueOf(60 * 5));
        d.put("game.shockMineReArmDelayMin", "10");
        d.put("game.shockMineReArmDelayMax", "15");
        d.put("game.playerDisconnectCoreDestroyDelay", "30");
        d.put("game.joinRequestTimeout", "30");
        d.put("game.planetChanceSerpulo", "0.7");
        d.put("game.maxTeamSize", "4");
        d.put("game.coreTierSize", "25");
        d.put("game.attack.rtsMinWeight", "1.2");
        d.put("game.attack.squadMinSize", "1");
        d.put("game.attack.squadMaxSize", "100");
        d.put("game.factionGracePeriodMaxCores", "3");

        d.put("world.mapWidth", "600");
        d.put("world.mapHeight", "600");
        d.put("world.spawnCellSize", "50");
        d.put("world.dropZoneRadius", "100");
        d.put("world.radius.coreShard", "28");
        d.put("world.radius.coreFoundation", "33");
        d.put("world.radius.coreNucleus", "38");
        d.put("world.radius.coreBastion", "30");
        d.put("world.radius.coreCitadel", "38");
        d.put("world.radius.coreAcropolis", "42");
        d.put("world.obstacleCheckRadius", "5");
        d.put("world.spawnSurfaceDivisor", "300");
        d.put("world.gridCellSize", "100");
        d.put("world.biomeSampleDensity", "50");
        d.put("world.biomeDisplayThreshold", "0.1");
        d.put("world.biomeDisplayMaxCount", "3");
        d.put("world.voteOptimizationRetries", "10");
        d.put("world.sectorMap.sampleDensity", "50");

        d.put("multiplier.buildSpeed", "2.0");
        d.put("multiplier.blockDamage", "2.0");
        d.put("multiplier.unitDamage", "0.5");
        d.put("multiplier.buildCost", "1.0");
        d.put("multiplier.blockDamageFormulaA", "1.0");
        d.put("multiplier.blockDamageFormulaB", "0.05");
        d.put("multiplier.blockDamageFormulaC", "1.0");
        d.put("multiplier.unitDamageFormulaA", "3.0");
        d.put("multiplier.unitDamageFormulaB", "2.0");
        d.put("multiplier.unitDamageFormulaC", "0.02");
        d.put("multiplier.unitHealthFormulaA", "6.0");
        d.put("multiplier.unitHealthFormulaB", "5.0");
        d.put("multiplier.unitHealthFormulaC", "0.02");

        d.put("turret.foreshadowAmmoDamage", "1.5");
        d.put("turret.spectreAmmoDamage", "2.0");
        d.put("turret.meltdownShootTypeDamage", "1.5");
        d.put("turret.reconstructorConstructTime", "0.75");

        d.put("scoring.k", "10");
        d.put("scoring.offset", "10");
        d.put("scoring.eloDiffClamp", "2000");
        d.put("scoring.winnerMultiplier", "1.5");
        d.put("scoring.decayMultiplier", "0.99");
        d.put("scoring.decayThreshold", "100");
        d.put("scoring.leaderboardPageSize", "10");
        d.put("scoring.leaderboardLimit", "100");

        d.put("interval.hudPopup", String.valueOf(60 * 5));
        d.put("interval.infoMessage", String.valueOf(60 * 60 * 8));
        d.put("interval.dominationCheck", String.valueOf(60 * 60 * 2));
        d.put("interval.gameStateLog", String.valueOf(60 * 60 * 5));

        d.put("unitCap.initial", "0");
        d.put("unitCap.modifier.coreShard", "4");
        d.put("unitCap.modifier.coreFoundation", "6");
        d.put("unitCap.modifier.coreNucleus", "8");
        d.put("unitCap.modifier.coreBastion", "3");
        d.put("unitCap.modifier.coreCitadel", "5");
        d.put("unitCap.modifier.coreAcropolis", "7");

        d.put("vote.displayPeriodSec", "3");
        d.put("vote.displayDurationSec", "20");
        d.put("vote.restartCountdownDelaySec", "25");
        d.put("vote.restartCountdownTicks", "10");

        d.put("misc.messageBufferDuration", "3");

        d.put("loadout.serpulo.copper.base", "800");
        d.put("loadout.serpulo.copper.perWave", "150");
        d.put("loadout.serpulo.lead.base", "500");
        d.put("loadout.serpulo.lead.perWave", "100");
        d.put("loadout.serpulo.graphite.base", "150");
        d.put("loadout.serpulo.graphite.perWave", "20");
        d.put("loadout.serpulo.silicon.base", "150");
        d.put("loadout.serpulo.silicon.perWave", "30");
        d.put("loadout.serpulo.metaglass.base", "100");
        d.put("loadout.serpulo.metaglass.perWave", "10");
        d.put("loadout.serpulo.titanium.base", "50");
        d.put("loadout.serpulo.titanium.perWave", "20");
        d.put("loadout.serpulo.thorium.base", "10");
        d.put("loadout.serpulo.thorium.perWave", "15");
        d.put("loadout.serpulo.plastanium.waveRequired", "5");
        d.put("loadout.serpulo.plastanium.base", "0");
        d.put("loadout.serpulo.plastanium.perWave", "20");
        d.put("loadout.serpulo.plastanium.waveOffset", "4");
        d.put("loadout.serpulo.phaseFabric.waveRequired", "10");
        d.put("loadout.serpulo.phaseFabric.base", "0");
        d.put("loadout.serpulo.phaseFabric.perWave", "15");
        d.put("loadout.serpulo.phaseFabric.waveOffset", "9");
        d.put("loadout.serpulo.surgeAlloy.waveRequired", "10");
        d.put("loadout.serpulo.surgeAlloy.base", "0");
        d.put("loadout.serpulo.surgeAlloy.perWave", "15");
        d.put("loadout.serpulo.surgeAlloy.waveOffset", "9");

        d.put("loadout.erekir.beryllium.base", "300");
        d.put("loadout.erekir.beryllium.perWave", "100");
        d.put("loadout.erekir.graphite.base", "100");
        d.put("loadout.erekir.graphite.perWave", "50");
        d.put("loadout.erekir.silicon.base", "50");
        d.put("loadout.erekir.silicon.perWave", "30");
        d.put("loadout.erekir.thorium.base", "150");
        d.put("loadout.erekir.thorium.perWave", "20");
        d.put("loadout.erekir.tungsten.waveRequired", "1");
        d.put("loadout.erekir.tungsten.base", "0");
        d.put("loadout.erekir.tungsten.perWave", "30");
        d.put("loadout.erekir.tungsten.waveOffset", "0");
        d.put("loadout.erekir.oxide.waveRequired", "3");
        d.put("loadout.erekir.oxide.base", "0");
        d.put("loadout.erekir.oxide.perWave", "20");
        d.put("loadout.erekir.oxide.waveOffset", "2");
        d.put("loadout.erekir.carbide.waveRequired", "5");
        d.put("loadout.erekir.carbide.base", "0");
        d.put("loadout.erekir.carbide.perWave", "10");
        d.put("loadout.erekir.carbide.waveOffset", "4");
        d.put("loadout.erekir.surgeAlloy.waveRequired", "7");
        d.put("loadout.erekir.surgeAlloy.base", "0");
        d.put("loadout.erekir.surgeAlloy.perWave", "20");
        d.put("loadout.erekir.surgeAlloy.waveOffset", "7");
        d.put("loadout.erekir.phaseFabric.waveRequired", "9");
        d.put("loadout.erekir.phaseFabric.base", "0");
        d.put("loadout.erekir.phaseFabric.perWave", "10");
        d.put("loadout.erekir.phaseFabric.waveOffset", "4");

        d.put("coreCost.serpulo.copper.base", "200");
        d.put("coreCost.serpulo.copper.perTier", "100");
        d.put("coreCost.serpulo.lead.base", "100");
        d.put("coreCost.serpulo.lead.perTier", "70");
        d.put("coreCost.serpulo.graphite.base", "50");
        d.put("coreCost.serpulo.graphite.perTier", "20");
        d.put("coreCost.serpulo.graphite.minTier", "1");
        d.put("coreCost.serpulo.silicon.base", "70");
        d.put("coreCost.serpulo.silicon.perTier", "50");
        d.put("coreCost.serpulo.silicon.minTier", "2");
        d.put("coreCost.serpulo.metaglass.base", "50");
        d.put("coreCost.serpulo.metaglass.perTier", "30");
        d.put("coreCost.serpulo.metaglass.minTier", "3");
        d.put("coreCost.serpulo.titanium.base", "200");
        d.put("coreCost.serpulo.titanium.perTier", "40");
        d.put("coreCost.serpulo.titanium.minTier", "5");
        d.put("coreCost.serpulo.thorium.base", "100");
        d.put("coreCost.serpulo.thorium.perTier", "40");
        d.put("coreCost.serpulo.thorium.minTier", "6");
        d.put("coreCost.serpulo.plastanium.base", "50");
        d.put("coreCost.serpulo.plastanium.perTier", "30");
        d.put("coreCost.serpulo.plastanium.minTier", "8");
        d.put("coreCost.serpulo.phaseFabric.base", "20");
        d.put("coreCost.serpulo.phaseFabric.perTier", "20");
        d.put("coreCost.serpulo.phaseFabric.minTier", "11");
        d.put("coreCost.serpulo.surgeAlloy.base", "30");
        d.put("coreCost.serpulo.surgeAlloy.perTier", "30");
        d.put("coreCost.serpulo.surgeAlloy.minTier", "15");

        d.put("coreCost.erekir.beryllium.base", "50");
        d.put("coreCost.erekir.beryllium.perTier", "50");
        d.put("coreCost.erekir.beryllium.minTier", "0");
        d.put("coreCost.erekir.graphite.base", "20");
        d.put("coreCost.erekir.graphite.perTier", "20");
        d.put("coreCost.erekir.graphite.minTier", "1");
        d.put("coreCost.erekir.silicon.base", "50");
        d.put("coreCost.erekir.silicon.perTier", "30");
        d.put("coreCost.erekir.silicon.minTier", "3");
        d.put("coreCost.erekir.oxide.base", "10");
        d.put("coreCost.erekir.oxide.perTier", "20");
        d.put("coreCost.erekir.oxide.minTier", "5");
        d.put("coreCost.erekir.carbide.base", "10");
        d.put("coreCost.erekir.carbide.perTier", "10");
        d.put("coreCost.erekir.carbide.minTier", "7");

        d.put("unit.speedMultiplier.zenith", "0.6");
        d.put("unit.speedMultiplier.mega", "0.5");
        d.put("unit.speedMultiplier.antumbra", "0.8");
        d.put("unit.speedMultiplier.quad", "0.8");
        d.put("unit.speedMultiplier.quad.extra", "1.25");
        d.put("unit.speedMultiplier.eclipse", "0.7");
        d.put("unit.speedMultiplier.crawler", "1.25");
        d.put("unit.speedMultiplier.dagger", "1.25");
        d.put("unit.speedMultiplier.nova", "1.25");
        d.put("unit.speedMultiplier.atrax", "1.25");
        d.put("unit.speedMultiplier.mace", "1.25");
        d.put("unit.speedMultiplier.pulsar", "1.25");
        d.put("unit.speedMultiplier.spiroct", "1.25");
        d.put("unit.speedMultiplier.fortress", "1.25");
        d.put("unit.speedMultiplier.arkyid", "1.25");
        d.put("unit.speedMultiplier.scepter", "1.25");
        d.put("unit.speedMultiplier.vela", "1.25");
        d.put("unit.speedMultiplier.toxopid", "1.25");
        d.put("unit.speedMultiplier.reign", "1.25");
        d.put("unit.speedMultiplier.corvus", "1.25");
        d.put("unit.speedMultiplier.emanate", "0.6");
        d.put("unit.healthMultiplier.zenith", "0.5");
        d.put("unit.healthMultiplier.mega", "0.8");
        d.put("unit.healthMultiplier.antumbra", "0.8");
        d.put("unit.healthMultiplier.eclipse", "0.7");

        return d;
    }

    static {
        String path = "config/mods/config/config.json";
        HashMap<String, String> loaded = new HashMap<>();

        try {
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                Reader reader = Files.newBufferedReader(Paths.get(path));
                JsonObject obj = new Gson().fromJson(reader, JsonObject.class);
                reader.close();
                if (obj != null) {
                    for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                        loaded.put(entry.getKey(), entry.getValue().getAsString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<String, String> oldToNew = new HashMap<>();
        oldToNew.put("databaseEnabled", "database.enabled");
        oldToNew.put("updateScoreDecay", "database.updateScoreDecay");
        oldToNew.put("discordEnabled", "discord.enabled");
        oldToNew.put("infiniteResources", "game.infiniteResources");
        for (Map.Entry<String, String> e : oldToNew.entrySet()) {
            if (loaded.containsKey(e.getKey())) {
                loaded.put(e.getValue(), loaded.get(e.getKey()));
                loaded.remove(e.getKey());
            }
        }

        HashMap<String, String> merged = new HashMap<>(defaults());
        merged.putAll(loaded);
        c = new Config(merged);

        try {
            java.io.File file = new java.io.File(path);
            file.getParentFile().mkdirs();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(merged);
            Files.write(Paths.get(path), json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Config {\n");
        for (Map.Entry<String, String> e : values.entrySet()) {
            sb.append("  ").append(e.getKey()).append("=").append(e.getValue()).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
