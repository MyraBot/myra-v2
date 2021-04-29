package com.myra.dev.marian.listeners.welcome.welcomeDirectMessage;

import com.myra.dev.marian.database.guild.MongoGuild;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import com.myra.dev.marian.utilities.permissions.Administrator;

public class WelcomeDirectMessageToggle implements CommandHandler {

@CommandEvent(
        name = "welcome direct message toggle",
        aliases = {"welcome dm toggle"},
        requires = Administrator.class
)
    public void execute(CommandContext ctx) throws Exception {
        //toggle feature
        new MongoGuild(ctx.getGuild()).getListenerManager().toggle("welcomeDirectMessage", "\u2709\uFE0F", ctx.getEvent());
    }
}
