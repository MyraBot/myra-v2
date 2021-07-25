package com.github.m5rian.myra;

import com.github.m5rian.myra.management.Listeners;

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
        Listeners.listenToShutdown();
        Config.setup();

        new Thread(DiscordBot::new, "Discord bot").start(); // Start Discord bot
        new Thread(WebServer::new, "Spark Web server").start(); // Start web server
    }

}
