package com.myra.dev.marian;

import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.CommandService;
import com.github.m5rian.jdaCommandHandler.commandServices.DefaultCommandServiceBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.myra.dev.marian.management.Listeners;
import com.myra.dev.marian.management.Registration;
import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.ConsoleColours;
import com.myra.dev.marian.utilities.Prefix;
import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.permissions.Marian;
import com.myra.dev.marian.utilities.permissions.Moderator;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Myra {
    public static ShardManager shardManager;

    private final static String TOKEN = "NzE4NDQ0NzA5NDQ1NjMyMTIy.Xto9xg.ScXvpTLGPkMBp0EP-mlLUCErI8Y";
    private final static String LOADING_STATUS = "loading bars fill";
    private final static String OFFLINE_INFO = ConsoleColours.RED + "Bot offline" + ConsoleColours.RESET;

    public static final EventWaiter WAITER = new EventWaiter();
    public static final CommandService COMMAND_SERVICE = new DefaultCommandServiceBuilder()
            .setDefaultPrefix(Config.prefix)
            .setVariablePrefix(new Prefix())
            .registerRoles(
                    new Marian(),
                    new Administrator(),
                    new Moderator()
            )
            .build();

    private Myra() throws LoginException, RateLimitedException {
        DefaultShardManagerBuilder jda = DefaultShardManagerBuilder.createDefault(TOKEN)
                // Disable unnecessary intents
                .disableIntents(
                        GatewayIntent.GUILD_BANS,
                        GatewayIntent.GUILD_EMOJIS,
                        GatewayIntent.GUILD_WEBHOOKS,
                        GatewayIntent.GUILD_INVITES,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_MESSAGE_TYPING,
                        GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                        GatewayIntent.DIRECT_MESSAGE_TYPING
                )
                .enableIntents(
                        // Enabled events
                        GatewayIntent.GUILD_MEMBERS,// Enabling events with members (Member join, leave, ...)
                        GatewayIntent.GUILD_MESSAGES, // Enabling message events (send, edit, delete, ...)
                        GatewayIntent.GUILD_MESSAGE_REACTIONS, // Reaction add remove bla bla
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_PRESENCES, // Is needed for the CLIENT_STATUS CacheFlag
                        GatewayIntent.GUILD_EMOJIS // Emote add/update/delete events. Also is needed for the CacheFlag
                )
                .enableCache(
                        CacheFlag.EMOTE,
                        CacheFlag.CLIENT_STATUS,
                        CacheFlag.VOICE_STATE
                )
                .disableCache(
                        CacheFlag.ACTIVITY,
                        CacheFlag.MEMBER_OVERRIDES,
                        CacheFlag.ROLE_TAGS
                )
                .setStatus(OnlineStatus.IDLE)
                .setActivity(Activity.watching(LOADING_STATUS))

                .addEventListeners(
                        WAITER,
                        new Listeners(),
                        new CommandHandler(COMMAND_SERVICE)
                );

        shardManager = jda.build(); // Build JDA
        Registration.register(); // Register commands and listeners
        consoleListener(); // Add console listener
    }

    // Main method
    public static void main(String[] args) throws LoginException, RateLimitedException {
        new Myra();
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
                        // Set status to "offline"
                        shardManager.setStatus(OnlineStatus.OFFLINE);
                        // Stop shard manager
                        shardManager.shutdown();
                        System.out.println(OFFLINE_INFO);
                        // Stop jar file from running
                        System.exit(0);
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