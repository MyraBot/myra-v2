package com.myra.dev.marian.utilities;

import com.myra.dev.marian.Config;
import com.myra.dev.marian.Myra;
import com.myra.dev.marian.database.MongoDb;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import okhttp3.OkHttpClient;
import org.bson.Document;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

public class Utilities {
    public final static ScheduledExecutorService TIMER = Executors.newScheduledThreadPool(5);
    public final static OkHttpClient HTTP_CLIENT = new OkHttpClient();
    private final static Utilities getUtils = new Utilities();

    public static Utilities getUtils() {
        return getUtils;
    }

    //colours
    public final int red = 0xC16B65;
    public final int blue = 0x7AC8F2;
    public final int gray = 0x282c34;
    //keys
    public final String youTubeKey = "AIzaSyAOJVth0U1loodJ9ShNjocc1eKMZr-Xxsg";
    public final String twitchClientId = "2ns4hcj4kkd6vj3armlievqsw8znd3";
    public final String twitchClientSecret = "kbvqhnosdqrezqhy8zuly9hapzeapn";
    public final String twitchRedirect_uri = "http://localhost";
    public final String giphyKey = "nV9Hhe5WbaVli6jg8Nlo2VcIB1kq5Ekq";
    public final String HypixelKey = "6cb5b7e7-66ab-477d-9d18-4f029e676d37";
    public final String spotifyClientId = "f19bf0a7cb204c098dbdaaeedf47f842";
    public final String spotifyClientSecret = "d4d48b2e4b474d098fa440a6d01ece42";
    public final String topGgKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjcxODQ0NDcwOTQ0NTYzMjEyMiIsImJvdCI6dHJ1ZSwiaWF0IjoxNjA0MzMwMTg3fQ.-zX8YHLdiH9w6pmDceN0fHDjTAJd9FbDiNXM2sftoA4";

    /**
     * @param name The name of the emote.
     * @return Returns an emote from Myra's Server.
     */
    public static Emote findEmote(String name) {
        final Guild guild = Myra.shardManager.getGuildById(Config.myraServer);

        if (guild.getEmotesByName(name, true).isEmpty()) return null;
        return new Emote(guild.getEmotesByName(name, true).get(0));
    }

    public static class Emote {
        public final net.dv8tion.jda.api.entities.Emote emote;

        public Emote(net.dv8tion.jda.api.entities.Emote emote) {
            this.emote = emote;
        }

        public net.dv8tion.jda.api.entities.Emote getAsEmote() {
            return this.emote;
        }

        public String getAsMention() {
            return this.emote.getAsMention();
        }

        public String getAsReactionEmote() {
            return "R" + this.emote;
        }
    }

    /**
     * Get a clickable message, which redirects you to a link.
     *
     * @param message The shown message.
     * @param link    The link if you click on the message.
     * @return Returns a hyperlink as a String.
     */
    public String hyperlink(String message, String link) {
        return "[" + message + "](" + link + ")";
    }

    /**
     * Get an array as a full sentence.
     *
     * @param array The array, which should be put together.
     * @return Returns the Strings of the array as one String.
     */
    public String getString(String[] array) {
        StringBuilder string = new StringBuilder();
        for (String s : array) {
            string.append(s).append(" ");
        }
        //Remove last space
        string = new StringBuilder(string.substring(0, string.length() - 1));
        return string.toString();
    }

    /**
     * Get the duration from a String.
     *
     * @param providedInformation The String with all information.
     * @return Returns a String List, which contains the given duration, the duration in milliseconds and the Time Unit.
     */
    public JSONObject getDuration(String providedInformation) {
        //get time unit
        TimeUnit timeUnit = null;
        switch (providedInformation.replaceAll("\\d+", "")) {
            case "s":
            case "sec":
            case "second":
            case "seconds":
                timeUnit = TimeUnit.SECONDS;
                break;
            case "m":
            case "min":
            case "minute":
            case "minutes":
                timeUnit = TimeUnit.MINUTES;
                break;
            case "h":
            case "hour":
            case "hours":
                timeUnit = TimeUnit.HOURS;
                break;
            case "d":
            case "day":
            case "days":
                timeUnit = TimeUnit.DAYS;
                break;
        }
        //get duration
        long duration = Long.parseLong(providedInformation.replaceAll("[^\\d.]", ""));
        long durationInMilliseconds = timeUnit.toMillis(duration);
        // Create the JSONObject
        JSONObject time = new JSONObject();
        time.put("duration", duration);
        time.put("durationInMilliseconds", durationInMilliseconds);
        time.put("timeUnit", timeUnit);
        return time;
    }

    /**
     * Add '.' separators to show the number more nicely.
     *
     * @param number The number to format.
     * @return Returns the formatted number as a String.
     */
    public String formatNumber(int number) {
        return NumberFormat.getInstance().format(number);
    }

    /**
     * Generate a new invite link for the bot.
     *
     * @param jda The bot.
     * @return Returns a bot invite.
     */
    public String inviteJda(JDA jda) {
        return jda.getInviteUrl(
                Permission.CREATE_INSTANT_INVITE,
                Permission.KICK_MEMBERS,
                Permission.BAN_MEMBERS,
                //Permission.ADMINISTRATOR,
                Permission.MANAGE_CHANNEL,
                //Permission.MANAGE_SERVER,
                Permission.MESSAGE_ADD_REACTION,
                //Permission.VIEW_AUDIT_LOGS,
                //Permission.PRIORITY_SPEAKER,
                //Permission.VIEW_GUILD_INSIGHTS,

                // Applicable to all channel types
                Permission.VIEW_CHANNEL,

                // Text Permissions
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                //Permission.MESSAGE_TTS,
                Permission.MESSAGE_MANAGE,
                Permission.MESSAGE_EMBED_LINKS,
                Permission.MESSAGE_ATTACH_FILES,
                Permission.MESSAGE_HISTORY,
                //Permission.MESSAGE_MENTION_EVERYONE,
                Permission.MESSAGE_EXT_EMOJI,
                Permission.USE_SLASH_COMMANDS,

                // Voice Permissions
                //Permission.VOICE_STREAM,
                Permission.VOICE_CONNECT,
                Permission.VOICE_SPEAK,
                //Permission.VOICE_MUTE_OTHERS,
                //Permission.VOICE_DEAF_OTHERS,
                //Permission.VOICE_MOVE_OTHERS,
                //Permission.VOICE_USE_VAD,

                //Permission.NICKNAME_CHANGE,
                Permission.NICKNAME_MANAGE,

                Permission.MANAGE_ROLES,
                //Permission.MANAGE_PERMISSIONS,
                Permission.MANAGE_WEBHOOKS
                //Permission.MANAGE_EMOTES,
        );
    }

    public String marianUrl() {
        return "https://discord.com/users/639544573114187797";
    }

    //error message
    public void error(MessageChannel textChannel, String command, String commandEmoji, String errorHeader, String error, String authorAvatar) {
        textChannel.sendMessage(new EmbedBuilder()
                .setAuthor(command, null, authorAvatar)
                .setColor(Utilities.getUtils().red)
                .addField("\uD83D\uDEA7 │ " + errorHeader, error, false)
                .build())
                .queue();
    }

    /**
     * Get member from message.
     *
     * @param event          The GuildMessageReceivedEvent
     * @param providedMember The String the user should be in
     * @param command        The command name
     * @param commandEmoji   The emoji of the command
     * @return
     */
    public Member getMember(MessageReceivedEvent event, String providedMember, String command, String commandEmoji) {
        Member member = null;

        // Member given by id or mention
        if (providedMember.startsWith("<@") || providedMember.matches("\\d+")) {
            member = event.getGuild().retrieveMemberById(providedMember.replaceAll("[<@!>]", "")).complete();
        }
        else if (!event.getGuild().getMembersByEffectiveName(providedMember, true).isEmpty()) {
            member = event.getGuild().getMembersByEffectiveName(providedMember, true).get(0);
        }

        // No role given
        if (member == null) {
            error(event.getTextChannel(), command, commandEmoji, "No user given", "Please enter the id or mention the user", event.getAuthor().getEffectiveAvatarUrl());
            return null;
        }
        return member;
    }

    /**
     * Get a user.
     *
     * @param event        The MessageReceivedEvent.
     * @param providedUser The String the user is given.
     * @param command      The name of the command.
     * @param commandEmoji The Emoji of the command.
     * @return Returns the user as a User object.
     */
    public User getUser(MessageReceivedEvent event, String providedUser, String command, String commandEmoji) {
        User user;
        final JDA jda = event.getJDA();

        // User given by id or mention
        if (providedUser.startsWith("<@") || providedUser.matches("\\d+")) {
            user = jda.retrieveUserById(providedUser.replaceAll("[<@!>]", "")).complete();
        }
        // User given by name
        else if (!jda.getUsersByName(providedUser, true).isEmpty()) {
            user = jda.getUsersByName(providedUser, true).get(0);
        }
        // No User given
        else {
            error(event.getChannel(), command, commandEmoji, "No user given", "Please enter the id or mention the user", event.getAuthor().getEffectiveAvatarUrl());
            return null;
        }
        return user;
    }

    /**
     * Get a user, who will be modified.
     *
     * @param event        The MessageReceivedEvent.
     * @param providedUser The String the user is given.
     * @param command      The name of the command.
     * @param commandEmoji The Emoji of the command.
     * @return Returns the user as a User object.
     */
    public Member getModifiedMember(MessageReceivedEvent event, String providedUser, String command, String commandEmoji) {
        Member member;

        // Role given by id or mention
        if (providedUser.startsWith("<@") || providedUser.matches("\\d+")) {
            member = event.getGuild().getMemberById(providedUser.replaceAll("[<!@>]", ""));
        }
        // Role given by name
        else if (!event.getGuild().getMembersByEffectiveName(providedUser, true).isEmpty()) {
            member = event.getGuild().getMembersByEffectiveName(providedUser, true).get(0);
        }
        // No role given
        else {
            error(event.getChannel(), command, commandEmoji, "No user found", "Be sure the user is in the server", event.getAuthor().getEffectiveAvatarUrl());
            return null;
        }

        //can't modify yourself
        if (member.equals(event.getMember())) {
            error(event.getChannel(), command, commandEmoji, "Can't " + command + " the mentioned user", "You can't " + command + " yourself", event.getAuthor().getEffectiveAvatarUrl());
            return null;
        }
        //can't modify the owner
        else if (member.isOwner()) {
            error(event.getChannel(), command, commandEmoji, "Can't " + command + " the mentioned user", "You can't " + command + " the owner of the server", event.getAuthor().getEffectiveAvatarUrl());
            return null;
        }
        //if user has a higher or equal role than you
        if (!member.getRoles().isEmpty()) {
            if (member.getRoles().get(0).getPosition() > event.getGuild().getMember(event.getJDA().getSelfUser()).getRoles().get(0).getPosition()) {
                error(event.getChannel(), command, commandEmoji, "Can't " + command + " " + member.getEffectiveName(), "I can't " + command + " a member with a higher or equal role than me", event.getAuthor().getEffectiveAvatarUrl());
                return null;
            }
        }
        return member;
    }

    /**
     * Get a text channel.
     *
     * @param event           The GuildMessageReceivedEvent.
     * @param providedChannel The String the channel should be in.
     * @param command         The command name.
     * @param commandEmoji    The command emoji.
     * @return Returns a channel as a TextChannel Object.
     */
    public TextChannel getTextChannel(MessageReceivedEvent event, String providedChannel, String command, String commandEmoji) {
        TextChannel channel;

        // Role given by id or mention
        if (providedChannel.startsWith("<#") || providedChannel.matches("\\d+")) {
            channel = event.getGuild().getTextChannelById(providedChannel.replaceAll("[<#>]", ""));
        }
        // Role given by name
        else if (!event.getGuild().getTextChannelsByName(providedChannel, true).isEmpty()) {
            channel = event.getGuild().getTextChannelsByName(providedChannel, true).get(0);
        }
        // No role given
        else {
            error(event.getTextChannel(), command, commandEmoji, "No channel given", "Please enter the id or mention the channel", event.getAuthor().getEffectiveAvatarUrl());
            return null;
        }

        // No role found
        if (channel == null) {
            error(event.getTextChannel(), command, commandEmoji, "No channel found", "The given channel doesn't exist", event.getAuthor().getEffectiveAvatarUrl());
            return null;
        }
        return channel;
    }

    /**
     * Get a role.
     *
     * @param event        The MessageReceivedEvent.
     * @param providedRole The String the role should be in.
     * @param command      The command name.
     * @param commandEmoji The command Emoji.
     * @return Returns a role as a Role Object.
     */
    public Role getRole(MessageReceivedEvent event, String providedRole, String command, String commandEmoji) {
        Role role = null;

        // Role given by id or mention
        if (providedRole.startsWith("<@&") || providedRole.matches("\\d+")) {
            role = event.getGuild().getRoleById(providedRole.replaceAll("[<@&>]", ""));
        }
        // Role given by name
        if (!event.getGuild().getRolesByName(providedRole, true).isEmpty()) {
            role = event.getGuild().getRolesByName(providedRole, true).get(0);
        }

        // No role found
        if (role == null) {
            error(event.getTextChannel(), command, commandEmoji, "No role found", "I couldn't find the specified role", event.getAuthor().getEffectiveAvatarUrl());
            return null;
        }
        return role;
    }

    /**
     * @param document The document to get the long from.
     * @param key      The key of the long value.
     * @return Returns a long even if BSON reads an Integer.
     */
    public static Long getBsonLong(Document document, String key) {
        try {
            return document.getLong(key);
        }
        // If voice call time is an integer
        catch (ClassCastException e){
            return Long.valueOf(document.getInteger(key)); // Parse to long
        }
    }

    /**
     * @param jda A {@link JDA} object.
     * @return Returns the amount of {@link Member} of all servers.
     */
    public static long getMemberCount(JDA jda) {
        return jda.getGuilds().stream().mapToLong(Guild::getMemberCount).sum();
    }

    /**
     * This value updates when the bot joins a new server.
     * See in {@link com.myra.dev.marian.database.MongoDbUpdate#guildJoinEvent(GuildJoinEvent)}.
     *
     * @return Returns the amount of {@link User}.
     */
    public static long getUserCount(JDA jda) {
        final Document stats = new MongoDb().getCollection("config").find(eq("document", "stats")).first(); // Get stats document
        return getBsonLong(stats, "users"); // Returns user count as long
    }

}
