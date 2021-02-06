package com.myra.dev.marian.commands.help;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.CommandEmbeds;

@CommandSubscribe(
        name = "support",
        aliases = {"support server", "bugs"}
)
public class Support implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        //check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Send message
        ctx.getChannel().sendMessage(new CommandEmbeds(ctx.getGuild(), ctx.getEvent().getJDA(), ctx.getAuthor(), ctx.getPrefix()).supportServer().build()).queue();
    }
}
