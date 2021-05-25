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
        // Start Discord bot
        final Thread discord = new Thread(DiscordBot::new);
        discord.setName("Discord Bot");
        // Start web server
        final Thread webServer = new Thread(WebServer::new);
        webServer.setName("Spark Web server");

        discord.start();
        webServer.start();
    }

}
