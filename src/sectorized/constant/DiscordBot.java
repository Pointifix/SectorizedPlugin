package sectorized.constant;

import arc.util.Strings;
import com.google.gson.Gson;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class DiscordBot {
    private static JDA bot;
    private static Guild guild;
    private static TextChannel log;
    private static TextChannel hallOfFame;

    private static final HashMap<String, sectorized.faction.core.Member> awaitConfirmMessage = new HashMap<>();

    public static void init() {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("config/mods/config/discordConfig.json"));
            DiscordConfig config = gson.fromJson(reader, DiscordConfig.class);
            reader.close();

            DiscordBot.bot = JDABuilder.createDefault(config.token)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .build()
                    .awaitReady();

            DiscordBot.guild = DiscordBot.bot.getGuildById(config.guildID);

            DiscordBot.log = DiscordBot.bot.getTextChannelById(config.logChannelID);
            DiscordBot.hallOfFame = DiscordBot.bot.getTextChannelById(config.hallOfFameChannelID);

            DiscordBot.bot.addEventListener(new MessageListener());
        } catch (LoginException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(String message) {
        log.sendMessage(message).queue();
    }

    public static void sendMessageToHallOfFame(String message) {
        hallOfFame.sendMessage(message).queue();
    }

    public static void setStatus(String status) {
        DiscordBot.bot.getPresence().setActivity(Activity.playing(status));
    }

    public static boolean checkIfExists(String tag) {
        return guild.getMemberByTag(tag) != null;
    }

    public static void register(String tag, sectorized.faction.core.Member sectorizedMember) {
        Member member = guild.getMemberByTag(tag);

        if (member != null) {
            awaitConfirmMessage.put(member.getUser().getAsTag(), sectorizedMember);

            member.getUser().openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage("Was that you? \nA user named *" + Strings.stripColors(sectorizedMember.player.name).substring(1).replace("@", "at") + "* requested to link this account, type **yes** to confirm! \nIf that was not you please ignore this message!").queue();
            });

            MessageUtils.sendMessage(sectorizedMember.player, "Check your Discord, you should have received a message from the " + MessageUtils.cHighlight1 + "SectorizedBot" + MessageUtils.cDefault + ". \nIf not, you probably have to adjust your settings to allow messages from other server members.", MessageUtils.MessageLevel.INFO);
        }
    }

    private static class DiscordConfig {
        public String token;
        public long guildID;
        public long logChannelID;
        public long hallOfFameChannelID;

        public DiscordConfig(String token, long guildID, long logChannelID, long hallOfFameChannelID) {
            this.token = token;
            this.guildID = guildID;
            this.logChannelID = logChannelID;
            this.hallOfFameChannelID = hallOfFameChannelID;
        }
    }

    private static class MessageListener extends ListenerAdapter {
        public void onMessageReceived(MessageReceivedEvent event) {
            if (awaitConfirmMessage.containsKey(event.getAuthor().getAsTag()) && event.getMessage().getContentDisplay().equalsIgnoreCase("yes")) {
                sectorized.faction.core.Member sectorizedMember = awaitConfirmMessage.get(event.getAuthor().getAsTag());

                sectorizedMember.discordTag = event.getAuthor().getAsTag();

                Member guildMember = guild.getMemberByTag(sectorizedMember.discordTag);

                if (guildMember != null) {
                    DiscordBot.assignRole(sectorizedMember);

                    guildMember.getUser().openPrivateChannel().queue(privateChannel -> {
                        privateChannel.sendMessage("Successfully linked your ingame account!").queue();
                    });
                }
            }
        }
    }

    public static void assignRole(sectorized.faction.core.Member sectorizedMember) {
        if (sectorizedMember.discordTag != null) {
            Member guildMember = DiscordBot.guild.getMemberByTag(sectorizedMember.discordTag);

            if (guildMember != null) {
                String roleName;
                int rank = sectorizedMember.rank;

                if (rank > 500 || rank == -1) roleName = "other";
                else if (rank > 200) roleName = "top 500";
                else if (rank > 100) roleName = "top 200";
                else if (rank > 50) roleName = "top 100";
                else if (rank > 25) roleName = "top 50";
                else if (rank > 10) roleName = "top 25";
                else roleName = "top 10";

                Role role = null;
                for (Role r : DiscordBot.guild.getRoles()) {
                    if (r.getName().equals(roleName)) {
                        role = r;
                    }
                }

                if (role != null) {
                    for (Role guildMemberRole : guildMember.getRoles()) {
                        DiscordBot.guild.removeRoleFromMember(guildMember, guildMemberRole).queue();
                    }

                    DiscordBot.guild.addRoleToMember(guildMember, role).queue();
                }
            }
        }
    }
}
