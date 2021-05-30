package com.myra.dev.marian;

import com.github.m5rian.jdaCommandHandler.CommandListener;
import com.github.m5rian.jdaCommandHandler.commandMessages.CommandMessageFactory;
import com.github.m5rian.jdaCommandHandler.commandMessages.CommandUsageFactory;
import com.github.m5rian.jdaCommandHandler.commandServices.DefaultCommandService;
import com.github.m5rian.jdaCommandHandler.commandServices.DefaultCommandServiceBuilder;
import com.mongodb.client.model.Filters;
import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.database.MongoDbUpdate;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.management.Listeners;
import com.myra.dev.marian.utilities.ConsoleColours;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.permissions.Marian;
import com.myra.dev.marian.utilities.permissions.Moderator;
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

public class DiscordBot {
    public static ShardManager shardManager;

    private final static String TOKEN = "NzE4NDQ0NzA5NDQ1NjMyMTIy.Xto9xg.ScXvpTLGPkMBp0EP-mlLUCErI8Y";
    private final static String LOADING_STATUS = "loading bars fill";
    private final static String OFFLINE_INFO = ConsoleColours.RED + "Bot offline" + ConsoleColours.RESET;

    public static final DefaultCommandService COMMAND_SERVICE = new DefaultCommandServiceBuilder()
            .setDefaultPrefix(Config.DEFAULT_PREFIX)
            .setVariablePrefix(guild -> new MongoGuild(guild).getString("prefix"))
            .allowMention()
            .setUserBlacklist(() -> {
                final List<String> userIds = new ArrayList<>();
                final List<Document> userBlacklist = MongoDb.getInstance().getCollection("config").find(Filters.eq("document", "blacklist")).first().getList("users", Document.class);
                userBlacklist.forEach(user -> userIds.add(user.getString("userId")));
                return userIds;
            })
            .setInfoFactory(new CommandMessageFactory()
                    .setAuthor(ctx -> ctx.getMethodInfo().getCommand().name())
                    .setAuthorAvatar(ctx -> ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColourHex(ctx -> String.valueOf(Utilities.blue)))
            .setUsageFactory(new CommandUsageFactory()
                    .setDefaultEmbed(ctx -> new EmbedBuilder()
                            .setAuthor(ctx.getMethodInfo().getCommand().name(), null, ctx.getAuthor().getEffectiveAvatarUrl())
                            .setColor(Utilities.gray))
                    .addUsageAsField((ctx, command) -> new MessageEmbed.Field("`" + ctx.getPrefix() + command.name() + command.args() + "`", command.emoji() + " │ " + command.description(), false)))
            .build();

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