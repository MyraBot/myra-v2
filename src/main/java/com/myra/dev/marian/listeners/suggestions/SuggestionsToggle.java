package com.myra.dev.marian.listeners.suggestions;

import com.myra.dev.marian.database.guild.MongoGuild;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import com.myra.dev.marian.utilities.permissions.Administrator;

public class SuggestionsToggle implements CommandHandler {

@CommandEvent(
        name = "suggestions toggle",
        requires = Administrator.class
)
    public void execute(CommandContext ctx) throws Exception {
        //toggle feature
        new MongoGuild(ctx.getGuild()).getListenerManager().toggle("suggestions", "\uD83D\uDDF3", ctx.getEvent());
    }
}
