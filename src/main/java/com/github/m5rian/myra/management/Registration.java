package com.github.m5rian.myra.management;

import com.github.m5rian.myra.DiscordBot;
import com.github.m5rian.myra.commands.administrator.*;
import com.github.m5rian.myra.commands.administrator.leveling.LevelingChannel;
import com.github.m5rian.myra.commands.administrator.leveling.LevelingHelp;
import com.github.m5rian.myra.commands.administrator.leveling.LevelingSet;
import com.github.m5rian.myra.commands.administrator.leveling.LevelingToggle;
import com.github.m5rian.myra.commands.administrator.leveling.levelingRoles.*;
import com.github.m5rian.myra.commands.administrator.notifications.*;
import com.github.m5rian.myra.commands.administrator.reactionRoles.ReactionRolesAdd;
import com.github.m5rian.myra.commands.administrator.reactionRoles.ReactionRolesHelp;
import com.github.m5rian.myra.commands.administrator.reactionRoles.ReactionRolesRemove;
import com.github.m5rian.myra.commands.administrator.welcome.WelcomeChannel;
import com.github.m5rian.myra.commands.administrator.welcome.WelcomeColour;
import com.github.m5rian.myra.commands.administrator.welcome.WelcomeHelp;
import com.github.m5rian.myra.commands.administrator.welcome.WelcomeImage.WelcomeImageBackground;
import com.github.m5rian.myra.commands.administrator.welcome.WelcomeImage.WelcomeImageFont;
import com.github.m5rian.myra.commands.administrator.welcome.WelcomeImage.WelcomeImageHelp;
import com.github.m5rian.myra.commands.administrator.welcome.WelcomeImage.WelcomeImageToggle;
import com.github.m5rian.myra.commands.administrator.welcome.WelcomePreview;
import com.github.m5rian.myra.commands.administrator.welcome.welcomeDirectMessage.WelcomeDirectMessageHelp;
import com.github.m5rian.myra.commands.administrator.welcome.welcomeDirectMessage.WelcomeDirectMessageMessage;
import com.github.m5rian.myra.commands.administrator.welcome.welcomeDirectMessage.WelcomeDirectMessageToggle;
import com.github.m5rian.myra.commands.administrator.welcome.welcomeEmbed.WelcomeEmbedHelp;
import com.github.m5rian.myra.commands.administrator.welcome.welcomeEmbed.WelcomeEmbedMessage;
import com.github.m5rian.myra.commands.administrator.welcome.welcomeEmbed.WelcomeEmbedToggle;
import com.github.m5rian.myra.commands.developer.Blacklist;
import com.github.m5rian.myra.commands.developer.GetInvite;
import com.github.m5rian.myra.commands.developer.SetGuildPremium;
import com.github.m5rian.myra.commands.developer.Shutdown;
import com.github.m5rian.myra.commands.member.Leaderboard;
import com.github.m5rian.myra.commands.member.economy.*;
import com.github.m5rian.myra.commands.member.economy.administrator.Currency;
import com.github.m5rian.myra.commands.member.economy.administrator.EconomySet;
import com.github.m5rian.myra.commands.member.economy.administrator.shop.ShopAdd;
import com.github.m5rian.myra.commands.member.economy.administrator.shop.ShopHelp;
import com.github.m5rian.myra.commands.member.economy.administrator.shop.ShopRemove;
import com.github.m5rian.myra.commands.member.economy.blackjack.BlackJack;
import com.github.m5rian.myra.commands.member.fun.Meme;
import com.github.m5rian.myra.commands.member.fun.TextFormatter;
import com.github.m5rian.myra.commands.member.general.Avatar;
import com.github.m5rian.myra.commands.member.general.Calculate;
import com.github.m5rian.myra.commands.member.general.Emoji;
import com.github.m5rian.myra.commands.member.general.Reminder;
import com.github.m5rian.myra.commands.member.general.information.*;
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
import com.github.m5rian.myra.commands.premium.Unicorn;
import com.github.m5rian.myra.listeners.suggestions.SubmitSuggestion;
import com.github.m5rian.myra.listeners.suggestions.SuggestionsChannel;
import com.github.m5rian.myra.listeners.suggestions.SuggestionsHelp;
import com.github.m5rian.myra.listeners.suggestions.SuggestionsToggle;

public class Registration {
    public static void register() {
        // Register commands
        DiscordBot.COMMAND_SERVICE.registerCommandClasses(
                // Marian
                new SetGuildPremium(),
                new GetInvite(),
                new Shutdown(),
                new Blacklist(),
                // Premium
                new Unicorn(),
                // Administrator
                new Prefix(),
                new Config(),
                new Language(),
                new Say(),
                new Toggle(),
                new GlobalChatChannel(),
                new MusicVotingToggle(),

                new ReactionRolesHelp(),
                new ReactionRolesAdd(),
                new ReactionRolesRemove(),
                //
                new LogChannel(),
                // Help
                new Commands(),
                new Help(),
                new Invite(),
                new Ping(),
                new Support(),
                new Feature(),
                new Report(),
                new Vote(),
                // General
                new InformationHelp(),
                new InformationServer(),
                new InformationUser(),
                new InformationMember(),
                new InformationBot(),

                new Avatar(),
                new Calculate(),
                new Reminder(),
                new Emoji(),
                // Leveling
                new LevelingHelp(),
                new LevelingToggle(),
                new LevelingSet(),
                new LevelingChannel(),

                new LevelingRolesHelp(),
                new LevelingRolesList(),
                new LevelingRolesAdd(),
                new LevelingRolesRemove(),
                new LevelingRolesUnique(),

                new Rank(),
                new Background(),
                new Time(),
                new Leaderboard(),
                // Economy
                new EconomyHelp(),
                new EconomySet(),
                new Currency(),

                new ShopHelp(),
                new ShopAdd(),
                new ShopRemove(),

                new Balance(),
                new Daily(),
                new Streak(),
                new Fish(),
                new BlackJack(),
                new Give(),

                new Buy(),
                // Fun
                new Meme(),
                new TextFormatter(),
                // Suggestions
                new SuggestionsHelp(),
                new SuggestionsChannel(),
                new SuggestionsToggle(),

                new SubmitSuggestion(),
                // Moderation
                new ModerationHelp(),

                new Ban(),
                new Tempban(),
                new Unban(),

                new MuteRole(),
                new Mute(),
                new Tempmute(),
                new Unmute(),

                new Clear(),
                new Kick(),
                new Nick(),
                // Music
                new MusicHelp(),
                new MusicJoin(),
                new MusicLeave(),
                new MusicPlay(),
                new MusicStop(),
                new MusicShuffle(),
                new MusicRepeat(),
                new MusicInformation(),
                new MusicQueue(),
                new MusicSkip(),
                new MusicClearQueue(),
                new MusicFilters(),
                // Autorole
                new AutoRole(),
                // Notification
                new NotificationsHelp(),
                new NotificationsChannel(),
                new NotificationsList(),

                new NotificationsMessageHelp(),
                new Streamer(),
                new NotificationsMessageTwitch(),
                new YouTuber(),
                new NotificationsMessageYoutube(),
                // Welcome
                new WelcomeHelp(),
                new WelcomePreview(),
                new WelcomeChannel(),
                new WelcomeColour(),
                // Welcome Image
                new WelcomeImageHelp(),
                new WelcomeImageToggle(),
                new WelcomeImageBackground(),
                new WelcomeImageFont(),
                // Welcome direct message
                new WelcomeDirectMessageHelp(),
                new WelcomeDirectMessageToggle(),
                new WelcomeDirectMessageMessage(),
                // Welcome embed
                new WelcomeEmbedHelp(),
                new WelcomeEmbedToggle(),
                new WelcomeEmbedMessage()
        );
    }
}
