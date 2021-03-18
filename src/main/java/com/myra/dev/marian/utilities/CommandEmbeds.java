package com.myra.dev.marian.utilities;

import com.myra.dev.marian.database.allMethods.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class CommandEmbeds {
    // Variables
    private Guild guild;
    private User author;
    private JDA jda;
    private String prefix;

    // Constructor
    public CommandEmbeds(Guild guild, JDA jda, User author, String prefix) {
        this.guild = guild;
        this.jda = jda;
        this.author = author;
        this.prefix = prefix;
    }

    //commands list
    public EmbedBuilder commands() {
        return new EmbedBuilder()
                .setAuthor("commands", null, author.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().gray)
                .addField("`help`", "\uD83D\uDCD6 │ Helpful commands", false)
                .addField("`general`", "\uD83C\uDF88 │ The main commands of the bot", false)
                .addField("`fun`", "\uD83D\uDD79 │ Commands to play around with", false)
                .addField("`leveling`", "\uD83C\uDFC6 │ Leveling", false)
                .addField("`economy`", "\uD83D\uDCB0 │ Economy", false)
                .addField("`music`", "\uD83D\uDCFB │ Music related commands", false)
                .addField("`moderation`", "\uD83D\uDD28 │ Commands for security", false)
                .addField("`administrator`", "\uD83D\uDD29 │ Server commands", false);
    }

    // Help
    public EmbedBuilder help() {
        return new EmbedBuilder()
                .setAuthor("help", null, author.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().gray)
                .addField("`" + prefix + "help`", "\uD83E\uDDF0 │ Opens a menu with several helpful links and lists", false)
                .addField("`" + prefix + "commands`", "\uD83D\uDCC3 │ Shows this", false)
                .addField("`" + prefix + "invite`", "\u2709\uFE0F │ Invite me to your server", false)
                .addField("`" + prefix + "support`", "\u26A0\uFE0F │ Join the support server to get help and report bugs", false)
                .addField("`" + prefix + "ping`", "\uD83C\uDFD3 │ Check my latency", false)
                .addField("`" + prefix + "report <bug>`", "\uD83D\uDC1B │ Report a bug you found", false)
                .addField("`" + prefix + "feature <feature description>`", "\uD83D\uDCCC │ Submit a feature", false)
                .addField("`" + prefix + "vote`", "\uD83D\uDDF3 │ Vote for me on " + Utilities.getUtils().hyperlink("top.gg", "https://top.gg/bot/718444709445632122"), false);
    }


    //general
    public EmbedBuilder general() {
        return new EmbedBuilder()
                .setAuthor("general", null, author.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().gray)
                .addField("`" + prefix + "calculate <number 1 <operator> <number 2>`", "\uD83E\uDDEE │ Let me calculate something for you", false)
                .addField("`" + prefix + "avatar @user`", "\uD83D\uDDBC │ Gives you profile pictures of other people", false)
                .addField("`" + prefix + "information`", "\uD83D\uDDD2 │ Gives you information", false)
                .addField("`" + prefix + "reminder <duration><time unit> <description>`", "\u23F0 │ Let " + jda.getSelfUser().getName() + " remind you of something", false)
                .addField("`" + prefix + "suggest`", "\uD83D\uDDF3 │ Suggest something", false)
                .addField("`" + prefix + "`" + "character", "\u2049 │ Get the Unicode of a character or emoji", false);
    }

    //fun
    public EmbedBuilder fun() {
        return new EmbedBuilder()
                .setAuthor("fun", null, author.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().gray)
                .addField("`" + prefix + "meme`", "\uD83E\uDD2A │ Shows a random meme", false)
                .addField("`" + prefix + "format <text>`", "\uD83D\uDDDA │ Change the font of your text", false)
                .addField("`" + prefix + "would you rather`", " │ Play a round of would you rather", false);
    }

    //leveling
    public EmbedBuilder leveling() {
        return new EmbedBuilder()
                .setAuthor("leveling", null, author.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().gray)
                .addField("`" + prefix + "leveling toggle", "\uD83D\uDD11 │ Toggle experience gain off and on", false)
                .addField("`" + prefix + "rank <user>`", "\uD83C\uDFC5 │ Shows the rank of a user", false)
                .addField("`" + prefix + "leaderboard`", "\uD83E\uDD47 │ Shows the leaderboard", false)
                .addField("`" + prefix + "time <user>`", "\u231A │ Check the time a user spend in a voice call", false)
                .addField("`" + prefix + "edit rank <url>`", "\uD83D\uDDBC │ Set a custom rank background", false);
    }

    //leveling
    public EmbedBuilder economy() {
        return new EmbedBuilder()
                .setAuthor("economy", null, author.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().gray)
                .addField("`" + prefix + "leaderboard`", "\uD83E\uDD47 │ Shows the leaderboard", false)
                .addField("`" + prefix + "balance <user>`", new Database(guild).getNested("economy").getString("currency") + " │ Shows how many " + new Database(guild).getNested("economy").getString("currency") + " you have.", false)
                .addField("`" + prefix + "daily`", "\uD83E\uDD47 │ Claim your daily reward", false)
                .addField("`" + prefix + "streak`", "\uD83D\uDCCA │ Shows your current daily streak", false)
                .addField("`" + prefix + "fish`", "\uD83C\uDFA3 │ Try to catch a fish", false)
                .addField("`" + prefix + "blackjack <bet>`", "\uD83C\uDCCF │ Play blackjack against " + jda.getSelfUser().getName(), false)
                .addField("`" + prefix + "give <user> <balance>`", "\uD83D\uDCB8 │ Give credits to other users", false)
                .addField("`" + prefix + "buy <role>`", "\uD83D\uDED2 │ Buy a role from the role shop", false);
    }

    //music
    public EmbedBuilder music() {
        return new EmbedBuilder()
                .setAuthor("music", null, author.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().gray)
                .addField("`" + prefix + "join`", "\uD83D\uDCE5 │ Let the bot join your voice channel", false)
                .addField("`" + prefix + "disconnect`", "\uD83D\uDCE4 │ Kick the bot from your voice channel", false)
                .addField("`" + prefix + "play <song>`", "\uD83D\uDCBF │ Add a song to the queue*", false)
                .addField("`" + prefix + "stop`", "\u23F9 │ Stop the player", false)
                .addField("`" + prefix + "skip`", "\u23ED\uFE0F │ Skip the current song", false)
                .addField("`" + prefix + "clear queue`", "\uD83D\uDDD1 │ Clear the queue", false)
                .addField("`" + prefix + "shuffle`", "\uD83C\uDFB2 │ Jumble the current queue", false)
                .addField("`" + prefix + "track information`", "\uD83D\uDDD2 │ Get information about the current song", false)
                .addField("`" + prefix + "queue`", "\uD83D\uDCC3 │ Shows the queue", false)
                .setFooter("supported platforms: YoutTube, SoundCloud, Bandcamp, Vimeo, Twitch streams");
    }

    //moderation
    public EmbedBuilder moderation() {
        return new EmbedBuilder()
                .setAuthor("moderation", null, author.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().gray)
                .addField("`" + prefix + "moderation`", "\uD83D\uDD28 │ Display all moderation commands", false)
                .addField("`" + prefix + "clear <amount>`", "\uD83D\uDDD1 │ Clear a certain amount of messages", false)
                .addField("`" + prefix + "kick <user>`", "\uD83D\uDCE4 │ Kick a user", false)
                .addField("`" + prefix + "nick <user>`", "\uD83D\uDD75 │ Change a users nickname", false)
                .addField("`" + prefix + "mute role <role>`", "\uD83D\uDCDD │ Change the mute role", false)
                .addField("`" + prefix + "unmute <user>`", "\uD83D\uDD08 │ Unmute a user", false)
                .addField("`" + prefix + "tempmute <user> <duration><time unit> <reason>`", "\u23F1\uFE0F │ Mute a user for a certain amount of time", false)
                .addField("`" + prefix + "mute <user>`", "\uD83D\uDD07 │ Mute a user", false)
                .addField("`" + prefix + "unban <user>`", "\uD83D\uDD13 │ Unban a user", false)
                .addField("`" + prefix + "tempban <user> <duration><time unit> <reason>`", "\u23F1\uFE0F │ Ban a user for a certain amount of time", false)
                .addField("`" + prefix + "ban <user> <reason>`", "\uD83D\uDD12 │ Ban a user", false);
    }

    //administrator
    public EmbedBuilder administrator() {
        return new EmbedBuilder()
                .setAuthor("administrator", null, author.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().gray)
                .addField("`" + prefix + "prefix <prefix>`", "\uD83D\uDCCC │ Change the prefix of the bot", false)
                .addField("`" + prefix + "toggle <command>`", "\uD83D\uDD11 │ Toggle commands on or off", false)
                .addField("`" + prefix + "say <message>`", "\uD83D\uDCAC │ Let the bot say something", false)
                .addField("`@someone`", "\uD83C\uDFB2 │ Replace in your message `@someone` with a random mention of a member", false)
                .addField("`" + prefix + "log channel`", "\uD83E\uDDFE │ Set the channel where logging messages go", false)
                .addField("`" + prefix + "autorole @role`", "\uD83D\uDCDD │ Give a new joined member automatic a certain role", false)
                .addField("`" + prefix + "welcome`", "\uD83D\uDC4B │ Welcome new users", false)
                .addField("`" + prefix + "notifications`", "\uD83D\uDD14 │ Set automatic notifications for youtube and twitch", false)
                .addField("`" + prefix + "suggestions`", "\uD83D\uDDF3 │ Set up the suggestions", false)
                .addField("`" + prefix + "leveling`", "\uD83C\uDFC6 │ Change some settings of leveling", false)
                .addField("`" + prefix + "economy`", "\uD83D\uDCB0 │ Change some settings of economy", false)
                .addField("`" + prefix + "global chat <channel>`", "\uD83C\uDF10 │ Communicate with other servers", false)
                .addField("`" + prefix + "reaction roles`", "\uD83C\uDF80 │ Bind roles to reactions", false)
                .addField("`" + prefix + "music voting`", "\uD83D\uDDF3 │ Enable/Disable voting on some music commands", false);
    }


    //support server
    public EmbedBuilder supportServer() {
        return new EmbedBuilder()
                .setAuthor("support", "https://discord.gg/nG4uKuB", author.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setThumbnail(jda.getGuildById("642809436515074053").getIconUrl())
                .setDescription("\u26A0\uFE0F │ " + Utilities.getUtils().hyperlink("Report", "https://discord.gg/nG4uKuB") + " bugs and get " + Utilities.getUtils().hyperlink("help", "https://discord.gg/nG4uKuB") + "!");
    }

    //invite bot
    public EmbedBuilder inviteJda() {
        return new EmbedBuilder()
                .setAuthor("invite", Utilities.getUtils().inviteJda(jda), author.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setThumbnail(jda.getSelfUser().getEffectiveAvatarUrl())
                .setDescription("[\u2709\uFE0F │ Invite me to your server!](" + Utilities.getUtils().inviteJda(jda) + " \"bot invite link\")");
    }
}