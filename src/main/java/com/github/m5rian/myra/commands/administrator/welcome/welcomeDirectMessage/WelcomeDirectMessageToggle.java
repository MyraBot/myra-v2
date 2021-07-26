package com.github.m5rian.myra.commands.administrator.welcome.welcomeDirectMessage;

import com.github.m5rian.myra.utilities.permissions.Administrator;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;

public class WelcomeDirectMessageToggle implements CommandHandler {

@CommandEvent(
        name = "welcome direct message toggle",
        aliases = {"welcome dm toggle"},
        requires = Administrator.class
)
    public void execute(CommandContext ctx) throws Exception {
        // Toggle feature
        MongoGuild.get(ctx.getGuild()).getListenerManager().toggle("welcomeDirectMessage", "\u2709\uFE0F", ctx.getEvent());
    }
}
