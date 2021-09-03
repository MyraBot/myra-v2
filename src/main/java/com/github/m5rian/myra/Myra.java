package com.github.m5rian.myra;

import com.github.m5rian.myra.management.Listeners;
import org.slf4j.LoggerFactory;

/**
 * @author Marian
 * <p>
 * Main class of the Myra bot.
 */
public class Myra {

    public static Config config = new Config();

    /**
     * Starts the {@link DiscordBot} and the {@link WebServer}.
     *
     * @param args Non-required {@link String} arguments.
     */
    public static void main(String[] args) {
        Listeners.listenToShutdown();

        new Thread(DiscordBot::new, "Discord bot").start(); // Start Discord bot
        new Thread(WebServer::new, "Spark Web server").start(); // Start web server

        if (config.isInDev()) LoggerFactory.getLogger(Myra.class).info("Loaded as development bot!");
    }

}
