package com.myra.dev.marian.commands.moderation;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.CommandEmbeds;
import com.myra.dev.marian.utilities.permissions.Moderator;

public class ModerationHelp implements CommandHandler {

@CommandEvent(
        name = "moderation",
        aliases = {"mod"},
        requires = Moderator.class
)
    public void execute(CommandContext ctx) throws Exception {
        // Send message
        ctx.getChannel().sendMessage(new CommandEmbeds(ctx.getGuild(), ctx.getEvent().getJDA(), ctx.getAuthor(), ctx.getPrefix()).moderation().build()).queue();
    }
}