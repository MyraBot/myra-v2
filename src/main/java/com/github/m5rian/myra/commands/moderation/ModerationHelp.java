package com.github.m5rian.myra.commands.moderation;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.CommandEmbeds;
import com.github.m5rian.myra.utilities.permissions.Moderator;

public class ModerationHelp implements CommandHandler {

    @CommandEvent(
            name = "moderation",
            aliases = {"mod"},
            requires = Moderator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Send message
        ctx.getChannel().sendMessage(new CommandEmbeds(ctx.getGuild(), ctx.getAuthor()).moderation().build()).queue();
    }
}