package com.myra.dev.marian.listeners.welcome.welcomeDirectMessage;

import com.myra.dev.marian.database.allMethods.Database;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.permissions.Administrator;

@CommandSubscribe(
        name = "welcome direct message toggle",
        aliases = {"welcome dm toggle"},
        requires = Administrator.class
)
public class WelcomeDirectMessageToggle implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        //toggle feature
        new Database(ctx.getGuild()).getListenerManager().toggle("welcomeDirectMessage", "\u2709\uFE0F", ctx.getEvent());
    }
}
