package com.github.m5rian.myra.utilities;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.myra.Myra;
import com.github.m5rian.myra.database.MongoDb;
import com.github.m5rian.myra.database.MongoDbUpdate;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.natanbc.lavadsp.DspInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import okhttp3.OkHttpClient;
import org.bson.Document;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static com.mongodb.client.model.Filters.eq;
import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Utilities {
    public static final ScheduledExecutorService TIMER = Executors.newScheduledThreadPool(5);
    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

    //colours
    public static final int red = 0xC16B65;
    public static final int blue = 0x7AC8F2;
    public static final int gray = 0x282c34;
    //keys
    public static final String URL_PATTERN = "/^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?/";
    public static final String twitchClientId = "2ns4hcj4kkd6vj3armlievqsw8znd3";
    public static final String twitchClientSecret = "kbvqhnosdqrezqhy8zuly9hapzeapn";
    public static final String giphyKey = "nV9Hhe5WbaVli6jg8Nlo2VcIB1kq5Ekq";
    public static final String HypixelKey = "6cb5b7e7-66ab-477d-9d18-4f029e676d37";
    public static final String spotifyClientId = "f19bf0a7cb204c098dbdaaeedf47f842";
    public static final String spotifyClientSecret = "d4d48b2e4b474d098fa440a6d01ece42";
    public static final String topGgKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjcxODQ0NDcwOTQ0NTYzMjEyMiIsImJvdCI6dHJ1ZSwiaWF0IjoxNjA0MzMwMTg3fQ.-zX8YHLdiH9w6pmDceN0fHDjTAJd9FbDiNXM2sftoA4";

    /**
     * Get a clickable message, which redirects you to a link.
     *
     * @param message The shown message.
     * @param link    The link if you click on the message.
     * @return Returns a hyperlink as a String.
     */
    public static String hyperlink(String message, String link) {
        return "[" + message + "](" + link + ")";
    }

    /**
     * Get an array as a full sentence.
     *
     * @param array The array, which should be put together.
     * @return Returns the Strings of the array as one String.
     */
    public static String getString(String[] array) {
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
    public static JSONObject getDuration(String providedInformation) {
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
     * Generate a new invite link for the bot.
     *
     * @param jda The bot.
     * @return Returns a bot invite.
     */
    public static String inviteJda(JDA jda) {
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

    public static String marianUrl() {
        return "https://discord.com/users/639544573114187797";
    }

    //error message
    public static void error(MessageChannel textChannel, String command, String commandEmoji, String errorHeader, String error, String authorAvatar) {
        textChannel.sendMessage(new EmbedBuilder()
                .setAuthor(command, null, authorAvatar)
                .setColor(Utilities.red)
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
    public static Member getMember(MessageReceivedEvent event, String providedMember, String command, String commandEmoji) {
        Member member = null;

        // Member given by id or mention
        if (providedMember.startsWith("<@") || providedMember.matches("\\d+")) {
            member = event.getGuild().retrieveMemberById(providedMember.replaceAll("[<@!>]", "")).complete();
        } else if (!event.getGuild().getMembersByEffectiveName(providedMember, true).isEmpty()) {
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
    public static User getUser(MessageReceivedEvent event, String providedUser, String command, String commandEmoji) {
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
    public static Member getModifiedMember(MessageReceivedEvent event, String providedUser, String command, String commandEmoji) {
        Member member;

        // Role given by id or mention
        if (providedUser.startsWith("<@") || providedUser.matches("\\d+")) {
            member = event.getGuild().retrieveMemberById(providedUser.replaceAll("[<!@>]", "")).complete();
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
    public static TextChannel getTextChannel(MessageReceivedEvent event, String providedChannel, String command, String commandEmoji) {
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
    public static Role getRole(MessageReceivedEvent event, String providedRole, String command, String commandEmoji) {
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
        catch (ClassCastException e) {
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
     * See in {@link MongoDbUpdate#guildJoinEvent(GuildJoinEvent)}.
     *
     * @return Returns the amount of {@link User}.
     */
    public static long getUserCount(JDA jda) {
        final Document stats = new MongoDb().getCollection("config").find(eq("document", "stats")).first(); // Get stats document
        return getBsonLong(stats, "users"); // Returns user count as long
    }

    public static boolean isValidURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static boolean hasMusicError(CommandContext ctx) {
        // Prepare error message
        final Error error = new Error(ctx.getEvent())
                .setCommand("leave")
                .setEmoji("\uD83D\uDCE4");

        // Author isn't in a voice channel yet
        if (!ctx.getEvent().getMember().getVoiceState().inVoiceChannel()) {
            error.setMessage(Lang.lang(ctx).get("command.music.error.memberNotInVoiceChannel")).send();
            return true;
        }
        // Bot not connected to a voice channel
        if (!ctx.getGuild().getAudioManager().isConnected()) {
            error.setMessage(Lang.lang(ctx).get("command.music.error.notConnected")).send();
            return true;
        }
        // Author isn't in the same voice channel as bot
        if (!ctx.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(ctx.getEvent().getMember())) {
            ctx.getGuild().getAudioManager().getConnectedChannel().createInvite().timeout(15, TimeUnit.MINUTES).queue(invite -> {
                error.setMessage(Lang.lang(ctx).get("command.music.error.alreadyConnected")
                        .replace("{$channel}", invite.getChannel().getName()) // Channel name
                        .replace("{$invite}", invite.getUrl())) // Invite url
                        .send();
            });
            return true;
        }

        return false; // No errors
    }

    public static Properties getProperties() {
        try (InputStream inputStream = Myra.class.getClassLoader().getResourceAsStream("config.properties")) {
            final Properties properties = new Properties();

            properties.load(inputStream); // load a properties file
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Finds a library's pom.properties file.
     *
     * @param clazz Any class from the library to check.
     * @return Returns a {@link Properties} object of the pom.properties file.
     */
    public static Properties getPropertyFile(final Class<?> clazz) {
        Optional<Properties> property = Optional.ofNullable(clazz)
                .map(cls -> unthrow(cls::getProtectionDomain))
                .map(ProtectionDomain::getCodeSource)
                .map(CodeSource::getLocation)
                .map(url -> unthrow(url::openStream))
                .map(is -> unthrow(() -> new JarInputStream(is)))
                /*
                 Locate the pom.properties file in the Jar, if present and return a
                 Properties object representing the properties in that file.
                 */
                .map(jarInputStream -> {
                    try {
                        JarEntry jarEntry;
                        while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                            String entryName = jarEntry.getName();
                            if (entryName.startsWith("META-INF")
                                    && entryName.endsWith("pom.properties")) {

                                Properties properties = new Properties();
                                ClassLoader classLoader = clazz.getClassLoader();
                                properties.load(classLoader.getResourceAsStream(entryName));
                                return properties;
                            }
                        }
                    } catch (IOException ignored) {
                    }
                    return null;
                });


        final Properties unknown = new Properties();
        unknown.put("version", "unknown");
        unknown.put("groupId", "unknown");
        unknown.put("artifactId", "unknown");
        return property.orElse(unknown);
    }

    /**
     * Wrap a Callable with code that returns null when an exception occurs, so
     * it can be used in an Optional.map() chain.
     */
    private static <T> T unthrow(final Callable<T> code) {
        try {
            return code.call();
        } catch (Exception ignored) {
            return null;
        }
    }

    public static String getVersions(String pattern, Class... classes) {
        final StringBuilder table = new StringBuilder();
        for (Class<?> clazz : classes) {
            if (clazz.getCanonicalName().startsWith("com.myra.dev.marian")) table.append(String.format(pattern, "Myra", getProperties().getProperty("version")));
            if (clazz.getCanonicalName().startsWith("net.dv8tion.jda")) table.append(String.format(pattern, "JDA", JDAInfo.VERSION));
            if (clazz.getCanonicalName().startsWith("com.github.natanbc")) table.append(String.format(pattern, "LavaDsp", DspInfo.VERSION));
            else table.append(String.format(pattern, getPropertyFile(clazz).getProperty("artifactId"), getPropertyFile(clazz).getProperty("version")));
        }
        return table.toString();
    }

}