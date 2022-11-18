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

    private static final String welcomePopupMessage = cInfo + "Welcome to\n[white]\uF897[#9C4F96]S[#FF6355]E[#FBA949]C[#FAE442]T[#8BD448]O[#2AA8F2]R[#01D93F]I[#F0EC00]Z[#FF8B00]E[#DB2B28]D[white]\uF897\n\n" +
            cHighlight1 + "\uE87C How it works \uE87C[white]\n" +
            "You can only build within the bounds of your teams sector, highlighted by " + cDefault + "shock mines [white]\uF897.\n" +
            "Expand your sector by placing " + cHighlight3 + "vaults[white] \uF866 or " + cHighlight3 + "reinforced vaults[white] \uF70C within the borders of your sector. You can see the expansion cost on the info popup.\n\n" +
            "Placing vaults next to a core does not turn the vault into a new core!\n\n" +
            cHighlight1 + "\uE809 Your goal \uE809[white]\n" +
            "Survive against the " + cWarning + "crux[white] waves and eliminate all other teams to win.\n\n" +
            "Have fun playing :)\n\n" +
            "[blue]\uE80D" + cDefault + " https://discord.gg/AmdMXKkS9Q[white]";

    private static final ArrayList<Integer> bufferedMessages = new ArrayList<>();
    private static final EnumMap<MessageLevel, String> messageLevelPrefixes = new EnumMap<>(MessageLevel.class);

    static {
        messageLevelPrefixes.put(MessageLevel.INFO, cInfo + "\uE837 " + cDefault);
        messageLevelPrefixes.put(MessageLevel.WARNING, cWarning + "\u26A0 " + cDefault);
        messageLevelPrefixes.put(MessageLevel.ELIMINATION, cDanger + "\uE861 " + cDefault);
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
        ELIMINATION
    }
}
