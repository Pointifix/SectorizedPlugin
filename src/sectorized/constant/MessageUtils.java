package sectorized.constant;

import arc.util.Timer;
import mindustry.gen.Call;
import mindustry.gen.Player;

import java.util.ArrayList;
import java.util.EnumMap;

public class MessageUtils {
    public static final String cDefault = "[#dddddd]"; // lightgray
    public static final String cInfo = "[#33c9ff]"; // cyan
    public static final String cWarning = "[#ffa733]"; // orange
    public static final String cDanger = "[#ff3336]"; // red
    public static final String cPlayer = "[#335fff]"; // blue
    public static final String cHighlight1 = "[#ffe433]"; // gold
    public static final String cHighlight2 = "[#33ff5c]"; // light green
    public static final String cHighlight3 = "[#f533ff]"; // magenta

    private static final ArrayList<Integer> bufferedMessages = new ArrayList<>();
    private static final EnumMap<MessageLevel, String> messageLevelPrefixes = new EnumMap<>(MessageLevel.class);

    static {
        messageLevelPrefixes.put(MessageLevel.INFO, cInfo + "\uE837 " + cDefault);
        messageLevelPrefixes.put(MessageLevel.WARNING, cWarning + "\u26A0 " + cDefault);
        messageLevelPrefixes.put(MessageLevel.ELIMINATION, cDanger + "\uE861 " + cDefault);

        messageLevelPrefixes.put(MessageLevel.ATTACK, cDanger + "\uE865 " + cDefault);
        messageLevelPrefixes.put(MessageLevel.DEFEND, cWarning + "\uE86B " + cDefault);
        messageLevelPrefixes.put(MessageLevel.IDLE, cInfo + "\uE86C " + cDefault);
    }

    public static void sendMessage(String message, MessageLevel level) {
        Call.sendMessage(messageLevelPrefixes.get(level) + message);
    }

    public static void sendMessage(Player player, String message, MessageLevel level) {
        player.sendMessage(messageLevelPrefixes.get(level) + message);
    }

    public static void sendBufferedMessage(Player player, String message, MessageLevel level, int seconds) {
        if (!bufferedMessages.contains(player.id)) {
            bufferedMessages.add(player.id);

            player.sendMessage(messageLevelPrefixes.get(level) + message);

            Timer.schedule(() -> bufferedMessages.remove(Integer.valueOf(player.id)), seconds);
        }
    }

    public static void sendBufferedMessage(Player player, String message, MessageLevel level) {
        sendBufferedMessage(player, message, level, 3);
    }

    public enum MessageLevel {
        INFO,
        WARNING,
        ELIMINATION,
        ATTACK,
        DEFEND,
        IDLE
    }
}
