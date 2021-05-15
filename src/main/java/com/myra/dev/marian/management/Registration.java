package com.myra.dev.marian.management;

import com.myra.dev.marian.DiscordBot;
import com.myra.dev.marian.commands.Leaderboard;
import com.myra.dev.marian.commands.administrator.*;
import com.myra.dev.marian.commands.administrator.leveling.LevelingChannel;
import com.myra.dev.marian.commands.administrator.leveling.LevelingHelp;
import com.myra.dev.marian.commands.administrator.leveling.LevelingSet;
import com.myra.dev.marian.commands.administrator.leveling.LevelingToggle;
import com.myra.dev.marian.commands.administrator.leveling.levelingRoles.*;
import com.myra.dev.marian.commands.administrator.notifications.*;
import com.myra.dev.marian.commands.administrator.reactionRoles.ReactionRolesAdd;
import com.myra.dev.marian.commands.administrator.reactionRoles.ReactionRolesHelp;
import com.myra.dev.marian.commands.administrator.reactionRoles.ReactionRolesRemove;
import com.myra.dev.marian.commands.economy.*;
import com.myra.dev.marian.commands.economy.administrator.Currency;
import com.myra.dev.marian.commands.economy.administrator.EconomySet;
import com.myra.dev.marian.commands.economy.administrator.shop.ShopAdd;
import com.myra.dev.marian.commands.economy.administrator.shop.ShopHelp;
import com.myra.dev.marian.commands.economy.administrator.shop.ShopRemove;
import com.myra.dev.marian.commands.economy.blackjack.BlackJack;
import com.myra.dev.marian.commands.fun.Meme;
import com.myra.dev.marian.commands.fun.TextFormatter;
import com.myra.dev.marian.commands.general.Avatar;
import com.myra.dev.marian.commands.general.Calculate;
import com.myra.dev.marian.commands.general.Emoji;
import com.myra.dev.marian.commands.general.Reminder;
import com.myra.dev.marian.commands.general.information.*;
import com.myra.dev.marian.commands.help.*;
import com.myra.dev.marian.commands.leveling.Background;
import com.myra.dev.marian.commands.leveling.Rank;
import com.myra.dev.marian.commands.leveling.Time;
import com.myra.dev.marian.commands.moderation.Clear;
import com.myra.dev.marian.commands.moderation.Kick;
import com.myra.dev.marian.commands.moderation.ModerationHelp;
import com.myra.dev.marian.commands.moderation.Nick;
import com.myra.dev.marian.commands.moderation.ban.Ban;
import com.myra.dev.marian.commands.moderation.ban.Tempban;
import com.myra.dev.marian.commands.moderation.ban.Unban;
import com.myra.dev.marian.commands.moderation.mute.Mute;
import com.myra.dev.marian.commands.moderation.mute.MuteRole;
import com.myra.dev.marian.commands.moderation.mute.Tempmute;
import com.myra.dev.marian.commands.moderation.mute.Unmute;
import com.myra.dev.marian.commands.music.*;
import com.myra.dev.marian.commands.premium.Unicorn;
import com.myra.dev.marian.listeners.suggestions.SubmitSuggestion;
import com.myra.dev.marian.listeners.suggestions.SuggestionsChannel;
import com.myra.dev.marian.listeners.suggestions.SuggestionsHelp;
import com.myra.dev.marian.listeners.suggestions.SuggestionsToggle;
import com.myra.dev.marian.listeners.welcome.WelcomeChannel;
import com.myra.dev.marian.listeners.welcome.WelcomeColour;
import com.myra.dev.marian.listeners.welcome.WelcomeHelp;
import com.myra.dev.marian.listeners.welcome.WelcomeImage.WelcomeImageBackground;
import com.myra.dev.marian.listeners.welcome.WelcomeImage.WelcomeImageFont;
import com.myra.dev.marian.listeners.welcome.WelcomeImage.WelcomeImageHelp;
import com.myra.dev.marian.listeners.welcome.WelcomeImage.WelcomeImageToggle;
import com.myra.dev.marian.listeners.welcome.WelcomePreview;
import com.myra.dev.marian.listeners.welcome.welcomeDirectMessage.WelcomeDirectMessageHelp;
import com.myra.dev.marian.listeners.welcome.welcomeDirectMessage.WelcomeDirectMessageMessage;
import com.myra.dev.marian.listeners.welcome.welcomeDirectMessage.WelcomeDirectMessageToggle;
import com.myra.dev.marian.listeners.welcome.welcomeEmbed.WelcomeEmbedHelp;
import com.myra.dev.marian.listeners.welcome.welcomeEmbed.WelcomeEmbedMessage;
import com.myra.dev.marian.listeners.welcome.welcomeEmbed.WelcomeEmbedToggle;
import com.myra.dev.marian.marian.GetInvite;
import com.myra.dev.marian.marian.MariansDiscordEmbeds;
import com.myra.dev.marian.marian.SetGuildPremium;
import com.myra.dev.marian.marian.Shutdown;

public class Registration {
    public static void register() {
        // Register commands
        DiscordBot.COMMAND_SERVICE.registerCommandClasses(
                // Marian
                new SetGuildPremium(),
                new MariansDiscordEmbeds(),
                new GetInvite(),
                new Shutdown(),
                // Premium
                new Unicorn(),
                // Administrator
                new Prefix(),
                new Config(),
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
                //new MusicFilters(),
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
