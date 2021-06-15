package com.github.m5rian.myra.listeners.suggestions;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import com.github.m5rian.myra.database.guild.MongoGuild;

public class SuggestionsToggle implements CommandHandler {

    @CommandEvent(
            name = "suggestions toggle",
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length == 0) {
            // Toggle feature
            MongoGuild.get(ctx.getGuild()).getListenerManager().toggle("suggestions", "\uD83D\uDDF3", ctx.getEvent());
        }
    }
}
