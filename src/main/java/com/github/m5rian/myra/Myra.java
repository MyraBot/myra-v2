package com.github.m5rian.myra;

import com.github.m5rian.jdaCommandHandler.commandServices.ICommandService;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.natanbc.lavadsp.Converter;
import com.mongodb.client.MongoDatabase;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.JDA;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import spark.Spark;

import java.util.Properties;

/**
 * @author Marian
 * <p>
 * Main class of the Myra bot.
 */
public class Myra {

    /**
     * Starts the {@link DiscordBot} and the {@link WebServer}.
     *
     * @param args Non-required {@link String} arguments.
     */
    public static void main(String[] args) {
        // Startup
        final Properties properties = Utilities.getProperties(); // Get properties
        System.out.println(properties.getProperty("ascii"));
        String versions = Utilities.getVersions("\033[0;92m%-25s%s\033[0m\n",
                Myra.class,
                JDA.class,
                ICommandService.class,
                AudioPlayer.class,
                Converter.class,
                MongoDatabase.class,
                JSONObject.class,
                Jsoup.class,
                Spark.class,
                Logger.class);
        System.out.println(versions);
        Config.setup();

        new Thread(DiscordBot::new, "Discord bot").start(); // Start Discord bot
        new Thread(WebServer::new, "Spark Web server").start(); // Start web server
    }

}
