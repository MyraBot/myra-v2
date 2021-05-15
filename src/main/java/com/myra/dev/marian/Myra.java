package com.myra.dev.marian;

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
        new DiscordBot(); // Start Discord bot
        new WebServer(); // Start web server
    }

}
