package sectorized.constant;

import arc.files.Fi;
import arc.graphics.Pixmap;
import arc.graphics.PixmapIO;
import arc.graphics.gl.FrameBuffer;
import arc.util.Buffers;
import arc.util.ScreenUtils;
import com.google.gson.Gson;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static arc.Core.camera;
import static mindustry.Vars.*;

public class DiscordBot {
    private static JDA bot;
    private static TextChannel log;

    public static void init() {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("config/mods/config/discordConfig.json"));
            DiscordConfig config = gson.fromJson(reader, DiscordConfig.class);
            reader.close();

            DiscordBot.bot = JDABuilder.createDefault(config.token).build().awaitReady();

            DiscordBot.log = DiscordBot.bot.getTextChannelById(config.logChannelID);
        } catch (LoginException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(String message) {
        log.sendMessage(message).queue();
    }

    public static void sendMessageWithScreenshot(String message) {
        File screenshot = DiscordBot.takeMapScreenshot();

        if (screenshot != null) log.sendMessage(message).addFile(screenshot).queue();
        else log.sendMessage(message).queue();
    }

    public static void setStatus(String status) {
        DiscordBot.bot.getPresence().setActivity(Activity.playing(status));
    }

    private static File takeMapScreenshot() {
        int w = world.width() * tilesize, h = world.height() * tilesize;
        int memory = w * h * 4 / 1024 / 1024;

        if (memory >= (mobile ? 65 : 120)) {
            ui.showInfo("@screenshot.invalid");
            return null;
        }

        FrameBuffer buffer = new FrameBuffer(w, h);

        renderer.drawWeather = false;
        float vpW = camera.width, vpH = camera.height, px = camera.position.x, py = camera.position.y;
        disableUI = true;
        camera.width = w;
        camera.height = h;
        camera.position.x = w / 2f + tilesize / 2f;
        camera.position.y = h / 2f + tilesize / 2f;
        buffer.begin();
        renderer.draw();
        buffer.end();
        disableUI = false;
        camera.width = vpW;
        camera.height = vpH;
        camera.position.set(px, py);
        buffer.begin();
        byte[] lines = ScreenUtils.getFrameBufferPixels(0, 0, w, h, true);
        for (int i = 0; i < lines.length; i += 4) {
            lines[i + 3] = (byte) 255;
        }
        buffer.end();
        Pixmap fullPixmap = new Pixmap(w, h, Pixmap.Format.rgba8888);
        Buffers.copy(lines, 0, fullPixmap.getPixels(), lines.length);
        Fi file = new Fi("screenshot.png");
        PixmapIO.writePNG(file, fullPixmap);
        fullPixmap.dispose();
        renderer.drawWeather = true;

        buffer.dispose();
        return file.file();
    }

    private static class DiscordConfig {
        public String token;
        public long logChannelID;

        public DiscordConfig(String token, long logChannelID) {
            this.token = token;
            this.logChannelID = logChannelID;
        }
    }
}
