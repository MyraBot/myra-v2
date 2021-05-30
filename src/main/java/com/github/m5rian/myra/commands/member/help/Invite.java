package com.github.m5rian.myra.commands.member.help;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.CommandEmbeds;

public class Invite implements CommandHandler {

@CommandEvent(
        name = "invite"
)
    public void execute(CommandContext ctx) throws Exception {
        ctx.getChannel().sendMessage(new CommandEmbeds(ctx.getGuild(),  ctx.getAuthor()).inviteJda().build()).queue();
    }
}
