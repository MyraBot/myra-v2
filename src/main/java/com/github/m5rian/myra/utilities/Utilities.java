package com.github.m5rian.myra.utilities;

import com.github.m5rian.jdaCommandHandler.CommandUtils;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.language.Lang;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import okhttp3.OkHttpClient;
import org.bson.Document;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Utilities {
    public static final ScheduledExecutorService TIMER = Executors.newScheduledThreadPool(5);
    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    //colours
    public static final int red = 0xC16B65;
    public static final int blue = 0x7AC8F2;
    public static final int gray = 0x2F3136;
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
     * @param ctx           A {@link CommandContext}.
     * @param inputDuration The given duration.
     * @param inputTimeUnit The given time unit.
     * @return Returns a {@link Duration} with organized and formatted information.
     */
    public static Duration getDuration(CommandContext ctx, String inputDuration, String inputTimeUnit) {
        // Inputs don't match the right characters
        if (!inputDuration.matches("\\d+") || !inputTimeUnit.matches("\\D+")) {
            CommandUtils.errorFactory.invoke(ctx).setDescription(lang(ctx).get("error.invalidTime") + "\n" + lang(ctx).get("info.usage.time")).send();
            return null;
        }

        // Get timeunit
        final TimeUnit timeUnit = switch (inputTimeUnit) {
            case "s", "sec", "second", "seconds" -> TimeUnit.SECONDS;
            case "m", "min", "minute", "minutes" -> TimeUnit.MINUTES;
            case "h", "hour", "hours" -> TimeUnit.HOURS;
            case "d", "day", "days" -> TimeUnit.DAYS;
            default -> null;
        };
        // Get duration
        long duration = Long.parseLong(inputDuration);
        long durationInMilliseconds = timeUnit.toMillis(duration);

        return new Duration(durationInMilliseconds, duration, timeUnit);
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
        }
        // Member is given by name (needs to be cached though)
        else if (!event.getGuild().getMembersByEffectiveName(providedMember, true).isEmpty()) {
            member = event.getGuild().getMembersByEffectiveName(providedMember, true).get(0);
        }

        // No role given
        if (member == null) {
            error(event.getTextChannel(), command, commandEmoji, lang(event.getGuild()).get("error.retrieving.member.notGiven.header"), lang(event.getGuild()).get("error.retrieving.member.notGiven.error"), event.getAuthor().getEffectiveAvatarUrl());
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

        // Role is mentioned
        if (providedUser.startsWith("<@&")) {
            error(event.getChannel(), command, commandEmoji, "No user given", "Please mention a member, not a role", event.getAuthor().getEffectiveAvatarUrl());
            return null;
        }
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
            error(event.getTextChannel(), command, commandEmoji, lang(event.getGuild()).get("error.retrieving.member.notGiven.header"), lang(event.getGuild()).get("error.retrieving.member.notGiven.error"), event.getAuthor().getEffectiveAvatarUrl());
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

        // Role is mentioned
        if (providedUser.startsWith("<@&")) {
            error(event.getChannel(), command, commandEmoji, lang(event.getGuild()).get("error.retrieving.member.notGiven.header"), lang(event.getGuild()).get("error.retrieving.member.isRole.error"), event.getAuthor().getEffectiveAvatarUrl());
            return null;
        }
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
            error(event.getTextChannel(), command, commandEmoji, lang(event.getGuild()).get("error.retrieving.member.notFound.header"), lang(event.getGuild()).get("error.retrieving.member.notFound.error"), event.getAuthor().getEffectiveAvatarUrl());
            return null;
        }

        //can't modify yourself
        if (member.equals(event.getMember())) {
            error(event.getChannel(), command, commandEmoji, lang(event.getGuild()).get("error.retrieving.member.missingPerms.header").replace("{$command.name}", command), lang(event.getGuild()).get("error.retrieving.member.missingPerms.yourself.error").replace("{$command.name}", command), event.getAuthor().getEffectiveAvatarUrl());
            return null;
        }
        //can't modify the owner
        else if (member.isOwner()) {
            error(event.getChannel(), command, commandEmoji, lang(event.getGuild()).get("error.retrieving.member.missingPerms.header").replace("{$command.name}", command), lang(event.getGuild()).get("error.retrieving.member.missingPerms.owner.error"), event.getAuthor().getEffectiveAvatarUrl());
            return null;
        }
        //if user has a higher or equal role than you
        if (!member.getRoles().isEmpty()) {
            if (member.getRoles().get(0).getPosition() > event.getGuild().getMember(event.getJDA().getSelfUser()).getRoles().get(0).getPosition()) {
                error(event.getChannel(), command, commandEmoji, lang(event.getGuild()).get("error.retrieving.member.missingPerms.roleHierarchy.header").replace("{$command.name}", command).replace("{$member.name}", member.getEffectiveName()), lang(event.getGuild()).get("error.retrieving.member.missingPerms.roleHierarchy.error").replace("{$command.name}", command), event.getAuthor().getEffectiveAvatarUrl());
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
            error(event.getTextChannel(), command, commandEmoji, lang(event.getGuild()).get("error.retrieving.channel.notGiven.header"), lang(event.getGuild()).get("error.retrieving.channel.notGiven.error"), event.getAuthor().getEffectiveAvatarUrl());
            return null;
        }

        // No role found
        if (channel == null) {
            error(event.getTextChannel(), command, commandEmoji, lang(event.getGuild()).get("error.retrieving.channel.notFound.header"), lang(event.getGuild()).get("error.retrieving.channel.notFound.error"), event.getAuthor().getEffectiveAvatarUrl());
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
            error(event.getTextChannel(), command, commandEmoji, lang(event.getGuild()).get("error.retrieving.role.notFound.header"), lang(event.getGuild()).get("error.retrieving.role.notFound.error"), event.getAuthor().getEffectiveAvatarUrl());
            return null;
        }
        return role;
    }

    /**
     * @param document The document to get the long from.
     * @param key      The key of the long value.
     * @return Returns a {@link Long} even if BSON reads an {@link Integer}.
     */
    public static Long getBsonLong(Document document, String key) {
        try {
            return document.getLong(key);
        }
        // If value is an integer
        catch (ClassCastException e) {
            return Long.valueOf(document.getInteger(key)); // Parse to long
        }
    }

    /**
     * @param document The document to get the string from.
     * @param key      The key of the string value.
     * @return Returns a {@link String} even if BSON reads an {@link Integer}.
     */
    public static String getBsonString(Document document, String key) {
        try {
            return document.getString(key);
        }
        // If value is an integer
        catch (ClassCastException e) {
            return String.valueOf(document.getInteger(key)); // Parse to string
        }
    }

    /**
     * @param jda A {@link JDA} object.
     * @return Returns the amount of {@link Member} of all servers.
     */
    public static long getMemberCount(JDA jda) {
        return jda.getGuilds().stream().mapToLong(Guild::getMemberCount).sum();
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

    public static String toCodepoints(String string) {
        final StringBuilder codepoints = new StringBuilder();
        string.codePoints().mapToObj(Integer::toHexString).forEach(s -> codepoints.append("U+").append(s));
        return codepoints.toString();
    }

    public static class Duration {
        private final long durationInMillis;
        private final long duration;
        private final TimeUnit timeUnit;

        Duration(long durationInMillis, long duration, TimeUnit timeUnit) {
            this.durationInMillis = durationInMillis;
            this.duration = duration;
            this.timeUnit = timeUnit;
        }

        public long getMillis() {
            return this.durationInMillis;
        }

        public long getDuration() {
            return duration;
        }

        public TimeUnit getTimeUnit() {
            return timeUnit;
        }

        public String getTimeUnitAsName(Guild guild) {
            final String timeUnit;
            // Duration is exactly 1
            if (this.duration == 1) {
                switch (this.timeUnit) {
                    case SECONDS -> timeUnit = lang(guild).get("word.timeunit.second");
                    case MINUTES -> timeUnit = lang(guild).get("word.timeunit.minute");
                    case HOURS -> timeUnit = lang(guild).get("word.timeunit.hour");
                    case DAYS -> timeUnit = lang(guild).get("word.timeunit.day");
                    default -> timeUnit = lang(guild).get("word.invalid");
                }
            }
            // Duration isn't 1
            else {
                switch (this.timeUnit) {
                    case SECONDS -> timeUnit = lang(guild).get("word.timeunit.seconds");
                    case MINUTES -> timeUnit = lang(guild).get("word.timeunit.minutes");
                    case HOURS -> timeUnit = lang(guild).get("word.timeunit.hours");
                    case DAYS -> timeUnit = lang(guild).get("word.timeunit.days");
                    default -> timeUnit = lang(guild).get("word.invalid");
                }
            }

            return timeUnit;
        }

    }

    public static String mentionRole(String id) {
        return "<@&" + id + ">";
    }

}
