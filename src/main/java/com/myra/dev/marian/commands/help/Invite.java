package com.myra.dev.marian.commands.help;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import com.myra.dev.marian.utilities.CommandEmbeds;

public class Invite implements CommandHandler {

@CommandEvent(
        name = "invite"
)
    public void execute(CommandContext ctx) throws Exception {
        ctx.getChannel().sendMessage(new CommandEmbeds(ctx.getGuild(), ctx.getEvent().getJDA(), ctx.getAuthor(), ctx.getPrefix()).inviteJda().build()).queue();
    }
}
