package com.myra.dev.marian.commands.help;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.CommandEmbeds;

public class Support implements CommandHandler {

    @CommandEvent(
            name = "support",
            aliases = {"support server", "bugs"}
    )
    public void execute(CommandContext ctx) throws Exception {
        //check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Send message
        ctx.getChannel().sendMessage(new CommandEmbeds(ctx.getGuild(), ctx.getAuthor()).supportServer().build()).queue();
    }
}
