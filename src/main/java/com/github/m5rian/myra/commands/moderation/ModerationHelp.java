package com.github.m5rian.myra.commands.moderation;

import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.CommandEmbeds;
import com.github.m5rian.myra.utilities.permissions.Moderator;

public class ModerationHelp implements CommandHandler {

    @CommandEvent(
            name = "moderation",
            aliases = {"mod"},
            emoji = "\uD83D\uDD28",
            description = "description.mod.mod",
            requires = Moderator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Send message
        ctx.getChannel().sendMessage(new CommandEmbeds(ctx).moderation().build()).queue();
    }
}