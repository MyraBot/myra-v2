package com.github.m5rian.myra;

import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.Cache;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.util.concurrent.TimeUnit;

public class Config {

    public static final Integer WEB_SERVER_PORT = 1027;
    public static final String SERVER_ADDRESS = "http://www.myra.bot";
    public static final String YOUTUBE_KEY = "AIzaSyARpgQKLyve0fUSxEjzN-tAzRIGc2QlZxo";
    public static final String DEFAULT_PREFIX = "~";
    public static final String PREMIUM = "⋆";
    public static final String MARIAN_ID = "639544573114187797";
    public static final String MYRA_ID = "718444709445632122";
    public static final String MARIAN_SERVER_ID = "642809436515074053";
    public static final String MYRA_SERVER_ID = "774269364244971571";
    public static final String MARIANS_DISCORD_INVITE = "https://discord.gg/nG4uKuB";
    public static final String MYRA_STAFF_ROLE = "715545225057140736";
    public static final String MYRA_TRANSLATOR_ROLE = "849614228917583929";
    public static final String MYRA_PARTNER_ROLE = "842074433308196954";
    public static final String MYRA_BUG_WEBHOOK = "https://discord.com/api/v8/webhooks/849948508760834058/F-jHtylLUFEfnKYNyJynVEvKZ5M-2LIsAMH4VN0s1gdCBzOLSYRHFCkWy4WmUOHHgz-7";
    public static final String MYRA_FEATURE_WEBHOOK = "https://discord.com/api/v8/webhooks/788769270384558120/A_6jJ1gstVcqih6lD8pTIAereQBhTJRn9vtbljqevVQ4uiOXAEXPTWZBh6n99ZJJrwPd";
    public static final String REACTION_PREFIX = "RE:";
    public static final Integer ECONOMY_MAX = 1000000000;
    public static final boolean UPDATE_GUILDS = false;
    public static final boolean UPDATE_USERS = false;
    public static final String DEFAULT_AVATAR = "https://cdn.discordapp.com/embed/avatars/4.png";
    public static final String DISCORD_PROFILE_URL = "https://discord.com/users/";
    // Caches
    public static final Cache<String, String> CACHE_PREFIX = new Cache<String, String>()
            .setLoadFunction(guildId -> {
                final Guild guild = DiscordBot.shardManager.getGuildById(guildId); // Get guild
                final JDA jda = guild.getJDA(); // Get jda of guild
                return new MongoGuild(jda, guildId).getString("prefix");
            })
            .setTimeout(5L, TimeUnit.MINUTES);
    public static final Cache<String, MongoGuild> CACHE_GUILD = new Cache<String, MongoGuild>()
            .setLoadFunction(guildId -> {
                final Guild guild = DiscordBot.shardManager.getGuildById(guildId); // Get guild
                final JDA jda = guild.getJDA(); // Get jda of guild
                return new MongoGuild(jda, guildId);
            })
            .setTimeout(5L, TimeUnit.MINUTES);
    public static String LOCAL_ADDRESS;
    public static Long startUp;

    public static void setup() {
        final String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            LOCAL_ADDRESS = SERVER_ADDRESS;
        } else {
            LOCAL_ADDRESS = "http://www.myra.bot";
        }
    }
}
