package com.github.m5rian.myra.utilities;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.commands.administrator.*;
import com.github.m5rian.myra.commands.administrator.leveling.LevelingHelp;
import com.github.m5rian.myra.commands.administrator.notifications.NotificationsHelp;
import com.github.m5rian.myra.commands.administrator.reactionRoles.ReactionRolesHelp;
import com.github.m5rian.myra.commands.administrator.welcome.WelcomeHelp;
import com.github.m5rian.myra.commands.member.Leaderboard;
import com.github.m5rian.myra.commands.member.economy.*;
import com.github.m5rian.myra.commands.member.economy.blackjack.BlackJack;
import com.github.m5rian.myra.commands.member.fun.Meme;
import com.github.m5rian.myra.commands.member.fun.TextFormatter;
import com.github.m5rian.myra.commands.member.general.Avatar;
import com.github.m5rian.myra.commands.member.general.Calculate;
import com.github.m5rian.myra.commands.member.general.Emoji;
import com.github.m5rian.myra.commands.member.general.Reminder;
import com.github.m5rian.myra.commands.member.general.information.InformationHelp;
import com.github.m5rian.myra.commands.member.help.*;
import com.github.m5rian.myra.commands.member.leveling.Background;
import com.github.m5rian.myra.commands.member.leveling.Rank;
import com.github.m5rian.myra.commands.member.leveling.Time;
import com.github.m5rian.myra.commands.member.music.*;
import com.github.m5rian.myra.commands.moderation.Clear;
import com.github.m5rian.myra.commands.moderation.Kick;
import com.github.m5rian.myra.commands.moderation.ModerationHelp;
import com.github.m5rian.myra.commands.moderation.Nick;
import com.github.m5rian.myra.commands.moderation.ban.Ban;
import com.github.m5rian.myra.commands.moderation.ban.Tempban;
import com.github.m5rian.myra.commands.moderation.ban.Unban;
import com.github.m5rian.myra.commands.moderation.mute.Mute;
import com.github.m5rian.myra.commands.moderation.mute.MuteRole;
import com.github.m5rian.myra.commands.moderation.mute.Tempmute;
import com.github.m5rian.myra.commands.moderation.mute.Unmute;
import com.github.m5rian.myra.listeners.Someone;
import com.github.m5rian.myra.listeners.suggestions.SubmitSuggestion;
import com.github.m5rian.myra.listeners.suggestions.SuggestionsHelp;
import com.github.m5rian.myra.utilities.language.Lang;
import net.dv8tion.jda.api.EmbedBuilder;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class CommandEmbeds implements CommandHandler {
    // Variables
    private final CommandContext ctx;

    // Constructor
    public CommandEmbeds(CommandContext ctx) {
        this.ctx = ctx;
    }

    //commands list
    public EmbedBuilder commands() {
        return new EmbedBuilder()
                .setAuthor("commands", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.gray)
                .addField("`help`", "\uD83D\uDCD6 │ " + Lang.lang(this.ctx.getGuild()).get("category.help"), false)
                .addField("`general`", "\uD83C\uDF88 │ " + Lang.lang(this.ctx.getGuild()).get("category.general"), false)
                .addField("`fun`", "\uD83D\uDD79 │ " + Lang.lang(this.ctx.getGuild()).get("category.fun"), false)
                .addField("`leveling`", "\uD83C\uDFC6 │ " + Lang.lang(this.ctx.getGuild()).get("category.leveling"), false)
                .addField("`economy`", "\uD83D\uDCB0 │ " + Lang.lang(this.ctx.getGuild()).get("category.economy"), false)
                .addField("`music`", "\uD83D\uDCFB │ " + Lang.lang(this.ctx.getGuild()).get("category.music"), false)
                .addField("`moderation`", "\uD83D\uDD28 │ " + Lang.lang(this.ctx.getGuild()).get("category.moderation"), false)
                .addField("`administrator`", "\uD83D\uDD29 │ " + Lang.lang(this.ctx.getGuild()).get("category.administration"), false);
    }

    // Help
    public EmbedBuilder help() {
        return usage(this.ctx).addUsages(
                Help.class,
                Commands.class,
                Invite.class,
                Support.class,
                Ping.class,
                Report.class,
                Feature.class,
                Vote.class)
                .getEmbed();
    }


    //general
    public EmbedBuilder general() {
        return usage(this.ctx).addUsages(
                Calculate.class,
                Avatar.class,
                InformationHelp.class,
                Reminder.class,
                SubmitSuggestion.class,
                Emoji.class)
                .getEmbed();
    }

    //fun
    public EmbedBuilder fun() {
        return usage(this.ctx).addUsages(
                Meme.class,
                TextFormatter.class)
                .getEmbed();
    }

    //leveling
    public EmbedBuilder leveling() {
        return usage(this.ctx).addUsages(
                Rank.class,
                Leaderboard.class,
                Time.class,
                Background.class)
                .getEmbed();
    }

    //leveling
    public EmbedBuilder economy() {
        return usage(this.ctx).addUsages(
                Leaderboard.class,
                Balance.class,
                Daily.class,
                Streak.class,
                Fish.class,
                BlackJack.class,
                Give.class,
                Buy.class)
                .getEmbed();
    }

    //music
    public EmbedBuilder music() {
        return usage(this.ctx).addUsages(
                MusicJoin.class,
                MusicLeave.class,
                MusicPlay.class,
                MusicStop.class,
                MusicClearQueue.class,
                MusicShuffle.class,
                MusicInformation.class,
                MusicQueue.class)
                .setFooter(Lang.lang(this.ctx).get("command.music.info.platforms"))
                .getEmbed();
    }

    //moderation
    public EmbedBuilder moderation() {
        return usage(this.ctx).addUsages(
                ModerationHelp.class,
                Clear.class,
                Kick.class,
                Nick.class,
                MuteRole.class,
                Unmute.class,
                Tempmute.class,
                Mute.class,
                Unban.class,
                Tempban.class,
                Ban.class)
                .getEmbed();
    }

    //administrator
    public EmbedBuilder administrator() {
        return usage(this.ctx).addUsages(
                Prefix.class,
                Toggle.class,
                com.github.m5rian.myra.commands.administrator.Config.class,
                Say.class,
                Someone.class,
                LogChannel.class,
                AutoRole.class,
                WelcomeHelp.class,
                NotificationsHelp.class,
                SuggestionsHelp.class,
                LevelingHelp.class,
                EconomyHelp.class,
                GlobalChatChannel.class,
                ReactionRolesHelp.class,
                MusicVotingToggle.class)
                .forbidCommands("onMemberPurge", "onMemberUpdate") // Ignored commands
                .addInlineField("`@someone`", "\uD83C\uDFB2 │ " + lang(this.ctx).get("description.someone"))
                .getEmbed();
    }


    //support server
    public EmbedBuilder supportServer() {
        return new EmbedBuilder()
                .setAuthor("support", Config.MARIANS_DISCORD_INVITE, this.ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.blue)
                .setThumbnail(this.ctx.getEvent().getJDA().getGuildById(Config.MARIAN_SERVER_ID).getIconUrl())
                .setDescription("\u26A0\uFE0F │ " + Lang.lang(this.ctx).get("command.help.help.support").replace("{$url}", Config.MARIANS_DISCORD_INVITE));
    }

    //invite bot
    public EmbedBuilder inviteJda() {
        return new EmbedBuilder()
                .setAuthor("invite", Utilities.inviteJda(this.ctx.getEvent().getJDA()), this.ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.blue)
                .setThumbnail(ctx.getEvent().getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setDescription("\u2709\uFE0F │ " + Lang.lang(this.ctx.getGuild()).get("command.help.help.invite").replace("{$url}", Utilities.inviteJda(this.ctx.getEvent().getJDA())));
    }
}