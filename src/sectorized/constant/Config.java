package sectorized.constant;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Config {
    public static Config c;

    public final boolean databaseEnabled;
    public final boolean updateScoreDecay;
    public final boolean discordEnabled;
    public final boolean infiniteResources;

    public Config(boolean databaseEnabled, boolean updateScoreDecay, boolean discordEnabled, boolean infiniteResources) {
        this.databaseEnabled = databaseEnabled;
        this.updateScoreDecay = updateScoreDecay;
        this.discordEnabled = discordEnabled;
        this.infiniteResources = infiniteResources;
    }

    static {
        try {
            Reader reader = Files.newBufferedReader(Paths.get("config/mods/config/config.json"));

            Config.c = new Gson().fromJson(reader, Config.class);

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Config{" +
                "databaseEnabled=" + databaseEnabled +
                ", updateScoreDecay=" + updateScoreDecay +
                ", discordEnabled=" + discordEnabled +
                ", infiniteResources=" + infiniteResources +
                '}';
    }
}
