package com.github.m5rian.myra.commands.member.help;

import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.CommandEmbeds;

public class Support implements CommandHandler {

    @CommandEvent(
            name = "support",
            aliases = {"support server", "bugs"},
            emoji = "\u26A0\uFE0F",
            description = "description.help.support"
    )
    public void execute(CommandContext ctx) throws Exception {
        //check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Send message
        ctx.getChannel().sendMessage(new CommandEmbeds(ctx).supportServer().build()).queue();
    }
}
