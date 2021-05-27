package com.myra.dev.marian.listeners.welcome.WelcomeImage;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.permissions.Administrator;

public class WelcomeImageToggle implements CommandHandler {

    @CommandEvent(
            name = "welcome image toggle",
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length == 0) {
            // Toggle command
            new MongoGuild(ctx.getGuild()).getListenerManager().toggle("welcomeImage", "\uD83D\uDDBC", ctx.getEvent());
        }
    }
}