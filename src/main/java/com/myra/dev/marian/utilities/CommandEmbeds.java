package com.myra.dev.marian.utilities;

import com.myra.dev.marian.Config;
import com.myra.dev.marian.database.guild.MongoGuild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import static com.myra.dev.marian.utilities.language.Lang.lang;

public class CommandEmbeds {
    // Variables
    private final JDA jda;
    private final Guild guild;
    private final User author;
    private final String prefix;

    // Constructor
    public CommandEmbeds(Guild guild, User author) {
        this.jda = guild.getJDA();
        this.guild = guild;
        this.author = author;
        this.prefix = new MongoGuild(guild).getString("prefix");
    }

    //commands list
    public EmbedBuilder commands() {
        return new EmbedBuilder()
                .setAuthor("commands", null, author.getEffectiveAvatarUrl())
                .setColor(Utilities.gray)
                .addField("`help`", "\uD83D\uDCD6 │ " + lang(this.guild).get("category.help"), false)
                .addField("`general`", "\uD83C\uDF88 │ " + lang(this.guild).get("category.general"), false)
                .addField("`fun`", "\uD83D\uDD79 │ " + lang(this.guild).get("category.fun"), false)
                .addField("`leveling`", "\uD83C\uDFC6 │ " + lang(this.guild).get("category.leveling"), false)
                .addField("`economy`", "\uD83D\uDCB0 │ " + lang(this.guild).get("category.economy"), false)
                .addField("`music`", "\uD83D\uDCFB │ " + lang(this.guild).get("category.music"), false)
                .addField("`moderation`", "\uD83D\uDD28 │ " + lang(this.guild).get("category.moderation"), false)
                .addField("`administrator`", "\uD83D\uDD29 │ " + lang(this.guild).get("category.administration"), false);
    }

    // Help
    public EmbedBuilder help() {
        return new EmbedBuilder()
                .setAuthor("help", null, author.getEffectiveAvatarUrl())
                .setColor(Utilities.gray)
                .addField("`" + prefix + "help`", "\uD83E\uDDF0 │ " + lang(this.guild).get("description.help.help"), false)
                .addField("`" + prefix + "commands`", "\uD83D\uDCC3 │ " + lang(this.guild).get("description.help.commands"), false)
                .addField("`" + prefix + "invite`", "\u2709\uFE0F │ " + lang(this.guild).get("description.help.invite"), false)
                .addField("`" + prefix + "support`", "\u26A0\uFE0F │ " + lang(this.guild).get("description.help.support"), false)
                .addField("`" + prefix + "ping`", "\uD83C\uDFD3 │ " + lang(this.guild).get("description.help.ping"), false)
                .addField("`" + prefix + "report <bug>`", "\uD83D\uDC1B │ " + lang(this.guild).get("description.help.report"), false)
                .addField("`" + prefix + "feature <feature description>`", "\uD83D\uDCCC │ " + lang(this.guild).get("description.help.feature"), false)
                .addField("`" + prefix + "vote`", "\uD83D\uDDF3 │ " + lang(this.guild).get("description.help.vote"), false);
    }


    //general
    public EmbedBuilder general() {
        return new EmbedBuilder()
                .setAuthor("general", null, author.getEffectiveAvatarUrl())
                .setColor(Utilities.gray)
                .addField("`" + prefix + "calculate <number 1 <operator> <number 2>`", "\uD83E\uDDEE │ " + lang(this.guild).get("description.general.calculate"), false)
                .addField("`" + prefix + "avatar @user`", "\uD83D\uDDBC │ " + lang(this.guild).get("description.general.avatar"), false)
                .addField("`" + prefix + "information`", "\uD83D\uDDD2 │ " + lang(this.guild).get("description.general.info"), false)
                .addField("`" + prefix + "reminder <duration><time unit> <description>`", "\u23F0 │ " + lang(this.guild).get("description.general.reminder"), false)
                .addField("`" + prefix + "suggest`", "\uD83D\uDDF3 │ " + lang(this.guild).get("description.suggest"), false)
                .addField("`" + prefix + "character`", "\u2049 │ " + lang(this.guild).get("description.general.emoji"), false);
    }

    //fun
    public EmbedBuilder fun() {
        return new EmbedBuilder()
                .setAuthor("fun", null, author.getEffectiveAvatarUrl())
                .setColor(Utilities.gray)
                .addField("`" + prefix + "meme`", "\uD83E\uDD2A │ " + lang(this.guild).get("description.fun.meme"), false)
                .addField("`" + prefix + "format <text>`", "\uD83D\uDDDA │ " + lang(this.guild).get("description.fun.format"), false);
    }

    //leveling
    public EmbedBuilder leveling() {
        return new EmbedBuilder()
                .setAuthor("leveling", null, author.getEffectiveAvatarUrl())
                .setColor(Utilities.gray)
                .addField("`" + prefix + "rank <user>`", "\uD83C\uDFC5 │ " + lang(this.guild).get("description.leveling.rank"), false)
                .addField("`" + prefix + "leaderboard`", "\uD83E\uDD47 │ " + lang(this.guild).get("description.leaderboard"), false)
                .addField("`" + prefix + "time <user>`", "\u231A │ " + lang(this.guild).get("description.leveling.time"), false)
                .addField("`" + prefix + "edit rank <url>`", "\uD83D\uDDBC " + lang(this.guild).get("description.leveling.edit.rank"), false);
    }

    //leveling
    public EmbedBuilder economy() {
        final String currency = new MongoGuild(guild).getNested("economy").getString("currency"); // Get server currency
        final String myraName = this.guild.getSelfMember().getEffectiveName(); // Get Myra's current name

        return new EmbedBuilder()
                .setAuthor("economy", null, author.getEffectiveAvatarUrl())
                .setColor(Utilities.gray)
                .addField("`" + prefix + "leaderboard`", "\uD83E\uDD47 │ " + lang(this.guild).get("description.leaderboard"), false)
                .addField("`" + prefix + "balance <user>`", currency + " │ " + lang(this.guild).get("description.economy.balance").replace("{$currency}", currency), false)
                .addField("`" + prefix + "daily`", "\uD83E\uDD47 │ " + lang(this.guild).get("description.economy.daily"), false)
                .addField("`" + prefix + "streak`", "\uD83D\uDCCA │ " + lang(this.guild).get("description.economy.streak"), false)
                .addField("`" + prefix + "fish`", "\uD83C\uDFA3 │ " + lang(this.guild).get("description.economy.fish"), false)
                .addField("`" + prefix + "blackjack <bet>`", "\uD83C\uDCCF │ " + lang(this.guild).get("description.economy.blackjack").replace("{$myra}", myraName), false)
                .addField("`" + prefix + "give <user> <balance>`", "\uD83D\uDCB8 │ " + lang(this.guild).get("description.economy.give"), false)
                .addField("`" + prefix + "buy <role>`", "\uD83D\uDED2 │ " + lang(this.guild).get("description.economy.buy"), false);
    }

    //music
    public EmbedBuilder music() {
        return new EmbedBuilder()
                .setAuthor("music", null, author.getEffectiveAvatarUrl())
                .setColor(Utilities.gray)
                .addField("`" + prefix + "join`", "\uD83D\uDCE5 │ " + lang(this.guild).get("description.music.join"), false)
                .addField("`" + prefix + "disconnect`", "\uD83D\uDCE4 │ " + lang(this.guild).get("description.music.disconnect"), false)
                .addField("`" + prefix + "play <song>`", "\uD83D\uDCBF │ " + lang(this.guild).get("description.music.play"), false)
                .addField("`" + prefix + "stop`", "\u23F9 │ " + lang(this.guild).get("description.music.stop"), false)
                .addField("`" + prefix + "skip`", "\u23ED\uFE0F │ " + lang(this.guild).get("description.music.skip"), false)
                .addField("`" + prefix + "clear queue`", "\uD83D\uDDD1 │ " + lang(this.guild).get("description.music.clearQueue"), false)
                .addField("`" + prefix + "shuffle`", "\uD83C\uDFB2 │ " + lang(this.guild).get("description.music.shuffle"), false)
                .addField("`" + prefix + "track information`", "\uD83D\uDDD2 │ " + lang(this.guild).get("description.music.trackInformation"), false)
                .addField("`" + prefix + "queue`", "\uD83D\uDCC3 │ " + lang(this.guild).get("description.music.queue"), false)
                .setFooter(lang(this.guild).get("command.music.info.platforms"));
    }

    //moderation
    public EmbedBuilder moderation() {
        return new EmbedBuilder()
                .setAuthor("moderation", null, author.getEffectiveAvatarUrl())
                .setColor(Utilities.gray)
                .addField("`" + prefix + "moderation`", "\uD83D\uDD28 │ " + lang(this.guild).get("description.mod.mod"), false)
                .addField("`" + prefix + "clear <amount>`", "\uD83D\uDDD1 │ " + lang(this.guild).get("description.mod.clear"), false)
                .addField("`" + prefix + "kick <user>`", "\uD83D\uDCE4 │ " + lang(this.guild).get("description.mod.kick"), false)
                .addField("`" + prefix + "nick <user>`", "\uD83D\uDD75 │ " + lang(this.guild).get("description.mod.nick"), false)
                .addField("`" + prefix + "mute role <role>`", "\uD83D\uDCDD │ " + lang(this.guild).get("description.mod.mute"), false)
                .addField("`" + prefix + "unmute <user>`", "\uD83D\uDD08 │ " + lang(this.guild).get("description.mod.unmute"), false)
                .addField("`" + prefix + "tempmute <user> <duration><time unit> <reason>`", "\u23F1\uFE0F │ " + lang(this.guild).get("description.mod.tempmute"), false)
                .addField("`" + prefix + "mute <user>`", "\uD83D\uDD07 │ " + lang(this.guild).get("description.mod.mute"), false)
                .addField("`" + prefix + "unban <user>`", "\uD83D\uDD13 │ " + lang(this.guild).get("description.mod.unban"), false)
                .addField("`" + prefix + "tempban <user> <duration><time unit> <reason>`", "\u23F1\uFE0F │ " + lang(this.guild).get("description.mod.tempban"), false)
                .addField("`" + prefix + "ban <user> <reason>`", "\uD83D\uDD12 │ " + lang(this.guild).get("description.mod.ban"), false);
    }

    //administrator
    public EmbedBuilder administrator() {
        return new EmbedBuilder()
                .setAuthor("administrator", null, author.getEffectiveAvatarUrl())
                .setColor(Utilities.gray)
                .addField("`" + prefix + "prefix <prefix>`", "\uD83D\uDCCC │ " + lang(this.guild).get("description.prefix"), false)
                .addField("`" + prefix + "toggle <command>`", "\uD83D\uDD11 │ " + lang(this.guild).get("description.toggle"), false)
                .addField("`" + prefix + "config`", "\u2699 │ " + lang(this.guild).get("description.config"), false)
                .addField("`" + prefix + "say <message>`", "\uD83D\uDCAC │ " + lang(this.guild).get("description.say"), false)
                .addField("`@someone`", "\uD83C\uDFB2 │ " + lang(this.guild).get("description.someone"), false)
                .addField("`" + prefix + "log channel`", "\uD83E\uDDFE │ " + lang(this.guild).get("description.logChannel"), false)
                .addField("`" + prefix + "autorole <role>`", "\uD83D\uDCDD │ " + lang(this.guild).get("description.autorole"), false)
                .addField("`" + prefix + "welcome`", "\uD83D\uDC4B │ " + lang(this.guild).get("description.welcome"), false)
                .addField("`" + prefix + "notifications`", "\uD83D\uDD14 │ " + lang(this.guild).get("description.notifications"), false)
                .addField("`" + prefix + "suggestions`", "\uD83D\uDDF3 │ " + lang(this.guild).get("description.suggestions"), false)
                .addField("`" + prefix + "leveling`", "\uD83C\uDFC6 │ " + lang(this.guild).get("description.leveling"), false)
                .addField("`" + prefix + "economy`", "\uD83D\uDCB0 │ " + lang(this.guild).get("description.leveling"), false)
                .addField("`" + prefix + "global chat <channel>`", "\uD83C\uDF10 │ " + lang(this.guild).get("description.globalChat"), false)
                .addField("`" + prefix + "reaction roles`", "\uD83C\uDF80 │ " + lang(this.guild).get("description.reactionRoles"), false)
                .addField("`" + prefix + "music voting`", "\uD83D\uDDF3 │ " + lang(this.guild).get("description.music.voting"), false);
    }


    //support server
    public EmbedBuilder supportServer() {
        return new EmbedBuilder()
                .setAuthor("support", Config.MARIANS_DISCORD_INVITE, author.getEffectiveAvatarUrl())
                .setColor(Utilities.blue)
                .setThumbnail(jda.getGuildById(Config.MARIAN_SERVER_ID).getIconUrl())
                .setDescription("\u26A0\uFE0F │ " + lang(this.guild).get("command.help.help.support").replace("{$url}", Config.MARIANS_DISCORD_INVITE));
    }

    //invite bot
    public EmbedBuilder inviteJda() {
        return new EmbedBuilder()
                .setAuthor("invite", Utilities.inviteJda(jda), author.getEffectiveAvatarUrl())
                .setColor(Utilities.blue)
                .setThumbnail(jda.getSelfUser().getEffectiveAvatarUrl())
                .setDescription("\u2709\uFE0F │ " + lang(this.guild).get("command.help.help.invite").replace("{$url}", Utilities.inviteJda(this.jda)));
    }
}