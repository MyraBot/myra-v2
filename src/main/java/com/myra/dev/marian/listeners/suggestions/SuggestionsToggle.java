package com.myra.dev.marian.listeners.suggestions;

import com.myra.dev.marian.database.guild.MongoGuild;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.permissions.Administrator;

@CommandSubscribe(
        name = "suggestions toggle",
        requires = Administrator.class
)
public class SuggestionsToggle implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        //toggle feature
        new MongoGuild(ctx.getGuild()).getListenerManager().toggle("suggestions", "\uD83D\uDDF3", ctx.getEvent());
    }
}
