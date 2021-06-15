package com.github.m5rian.myra;

import com.github.m5rian.jdaCommandHandler.CommandListener;
import com.github.m5rian.jdaCommandHandler.commandMessages.CommandMessageFactory;
import com.github.m5rian.jdaCommandHandler.commandMessages.CommandUsageFactory;
import com.github.m5rian.jdaCommandHandler.commandServices.DefaultCommandService;
import com.github.m5rian.jdaCommandHandler.commandServices.DefaultCommandServiceBuilder;
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
import com.github.m5rian.myra.database.MongoDb;
import com.github.m5rian.myra.database.MongoDbUpdate;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.listeners.suggestions.SubmitSuggestion;
import com.github.m5rian.myra.listeners.suggestions.SuggestionsChannel;
import com.github.m5rian.myra.listeners.suggestions.SuggestionsHelp;
import com.github.m5rian.myra.listeners.suggestions.SuggestionsToggle;
import com.github.m5rian.myra.management.Listeners;
import com.github.m5rian.myra.utilities.ConsoleColours;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import com.github.m5rian.myra.utilities.permissions.Marian;
import com.github.m5rian.myra.utilities.permissions.Moderator;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bson.Document;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class DiscordBot {
    public static final DefaultCommandService COMMAND_SERVICE = new DefaultCommandServiceBuilder()
            .setDefaultPrefix(Config.DEFAULT_PREFIX)
            .setVariablePrefix(guild -> Config.CACHE_PREFIX.get(guild.getId()))
            .allowMention()
            .registerCommandClasses(
                    // Marian
                    new SetGuildPremium(),
                    new GetInvite(),
                    new Shutdown(),
                    new Blacklist(),
                    // Premium
                    new Unicorn(),
                    // Administrator
                    new Prefix(),
                    new com.github.m5rian.myra.commands.administrator.Config(),
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
                    new WelcomeEmbedMessage())
            .setUserBlacklist(() -> {
                final List<String> userIds = new ArrayList<>();
                final List<Document> userBlacklist = MongoDb.getInstance().getCollection("config").find(Filters.eq("document", "blacklist")).first().getList("users", Document.class);
                userBlacklist.forEach(user -> userIds.add(user.getString("userId")));
                return userIds;
            })
            .setInfoFactory(new CommandMessageFactory()
                    .setAuthor(ctx -> ctx.getCommand().name())
                    .setAuthorAvatar(ctx -> ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColourHex(ctx -> String.valueOf(Utilities.blue)))
            .setUsageFactory(new CommandUsageFactory()
                    .setDefaultEmbed(ctx -> new EmbedBuilder()
                            .setAuthor(ctx.getCommand().name(), null, ctx.getAuthor().getEffectiveAvatarUrl())
                            .setColor(Utilities.gray))
                    .addUsageAsField((ctx, command) -> {
                        try {
                            lang(ctx).get(command.description());
                        }catch (Exception e) {
                            System.out.println(command.name());
                        }
                        final String description = lang(ctx).get(command.description())
                                .replace("{$myra.name}", ctx.getBotMember().getEffectiveName())
                                .replace("{$guild.currency}", MongoGuild.get(ctx.getGuild()).getNested("economy").getString("currency"));

                        // Command has no arguments
                        if (command.args().length == 0) {
                            return new MessageEmbed.Field("`" + ctx.getPrefix() + command.name() + "`", command.emoji() + " │ " + description, false);
                        }
                        // Command has arguments
                        else {
                            return new MessageEmbed.Field("`" + ctx.getPrefix() + command.name() + " " + String.join(" ", command.args()) + "`", command.emoji() + " │ " + description, false);
                        }

                    }))
            .build();
    private final static String TOKEN = "NzE4NDQ0NzA5NDQ1NjMyMTIy.Xto9xg.ScXvpTLGPkMBp0EP-mlLUCErI8Y";
    private final static String LOADING_STATUS = "loading bars fill";
    private final static String OFFLINE_INFO = ConsoleColours.RED + "Bot offline" + ConsoleColours.RESET;
    public static ShardManager shardManager;

    /**
     * The {@link MemberCachePolicy} says what to do when we get a member through an event.
     * <p>
     * The {@link ChunkingFilter} says what we should do at boot.
     * Disable caching members on startup, otherwise we would hit instantly the Websockets limit.
     */
    public DiscordBot() {
        COMMAND_SERVICE.registerPermission(
                new Marian(),
                new Administrator(),
                new Moderator());

        final DefaultShardManagerBuilder jda = DefaultShardManagerBuilder.create(TOKEN,
                // Enabled events
                GatewayIntent.GUILD_MEMBERS,// Enabling events with members (Member join, leave, ...)
                GatewayIntent.GUILD_MESSAGES, // Enabling message events (send, edit, delete, ...)
                GatewayIntent.GUILD_MESSAGE_REACTIONS, // Reaction add remove bla bla
                GatewayIntent.GUILD_VOICE_STATES,
                //GatewayIntent.GUILD_PRESENCES, // Is needed for the CLIENT_STATUS CacheFlag
                GatewayIntent.GUILD_EMOJIS) // Emote add/update/delete events. Also is needed for the CacheFlag
                .enableCache(
                        CacheFlag.EMOTE,
                        //CacheFlag.CLIENT_STATUS,
                        CacheFlag.VOICE_STATE)
                .setMemberCachePolicy(MemberCachePolicy.DEFAULT)
                .setChunkingFilter(ChunkingFilter.NONE)
                .setLargeThreshold(50)
                .setStatus(OnlineStatus.IDLE)
                .setActivity(Activity.watching(LOADING_STATUS))
                .addEventListeners(
                        new Listeners(),
                        new CommandListener(COMMAND_SERVICE));

        // Update database
        MongoDbUpdate.update(() -> {
            try {
                shardManager = jda.build(); // Start Bot
                consoleListener(); // Add console listener
            } catch (LoginException e) {
                e.printStackTrace();
            }
        });
    }

    private void consoleListener() {
        String line;
        // Create a Buffered reader, which reads the lines of the console
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            while ((line = reader.readLine()) != null) {
                // Shutdown command
                if (line.equalsIgnoreCase("shutdown")) {
                    if (shardManager != null) {
                        shardManager.setStatus(OnlineStatus.OFFLINE); // Set status to offline
                        shardManager.shutdown(); // Stop Bot
                        System.out.println(OFFLINE_INFO); // Print offline info
                        System.exit(0); // Stop program
                    }
                }
                // Help command
                else {
                    System.out.println("Use " + ConsoleColours.RED + "shutdown" + ConsoleColours.RESET + " to shutdown the program");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}