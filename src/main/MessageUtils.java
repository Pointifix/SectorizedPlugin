package main;

import arc.util.Timer;
import mindustry.gen.Call;
import mindustry.gen.Player;

import java.util.ArrayList;
import java.util.EnumMap;

public class MessageUtils {
    public enum MessageLevel {
        INFO,
        WARNING
    }

    private static final String welcomePopupMessage = "[cyan]Welcome to\n[white]\uF897[#9C4F96]S[#FF6355]E[#FBA949]C[#FAE442]T[#8BD448]O[#2AA8F2]R[#01D93F]I[#F0EC00]Z[#FF8B00]E[#DB2B28]D[white]\uF897\n\n" +
            "[gold]\uE87C How it works \uE87C[white]\n" +
            "You can only build within the bounds of your teams sector, highlighted by [gray]shock mines [white]\uF897.\n" +
            "Expand your sector by placing [pink]vaults[white] \uF866 within the borders of your sector. You can see the expansion costs on the info popup.\n" +
            "Eliminate all other teams to win, but be aware of the crux waves.\n\n" +
            "[blue]\u26A0 BETA \u26A0[white]\n" +
            "The plugin is still under development!\nPlease report any bugs you encounter on discord.\n\n" +
            "Have fun playing :)\n\n" +
            "[blue]\uE80D [lightgray]https://discord.gg/AmdMXKkS9Q[white]";

    private static final ArrayList<String> cannotBuildHereCooldown = new ArrayList<>();

    private static final EnumMap<MessageLevel, String> messageLevelPrefixes = new EnumMap<>(MessageLevel.class);

    static {
        messageLevelPrefixes.put(MessageLevel.INFO, "[gold]\uE837 [white]");
        messageLevelPrefixes.put(MessageLevel.WARNING, "[red]\u26A0 [white]");
    }

    public static void sendMessage(String message, MessageLevel level) {
        Call.sendMessage(messageLevelPrefixes.get(level) + message);
    }

    public static void sendMessage(Player player, String message, MessageLevel level) {
        player.sendMessage(messageLevelPrefixes.get(level) + message);
    }

    public static void sendWelcomeMessage(Player player) {
        Call.infoMessage(player.con(), welcomePopupMessage);
    }

    public static void sendCannotBuildHereMessage(Player player) {
        String uuid = player.uuid();

        if (!cannotBuildHereCooldown.contains(uuid)) {
            cannotBuildHereCooldown.add(uuid);
            player.sendMessage(messageLevelPrefixes.get(MessageLevel.WARNING) + "You cannot build outside your sector! To expand your sector place a [pink]vault[white] \uF866 within the borders of your sector!");

            Timer.schedule(() -> cannotBuildHereCooldown.remove(uuid), 3);
        }
    }
}
