package com.github.m5rian.myra.commands.member.help;

import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.CommandEmbeds;

public class Invite implements CommandHandler {

    @CommandEvent(
            name = "invite",
            emoji = "\u2709\uFE0F",
            description = "description.help.invite"
    )
    public void execute(CommandContext ctx) throws Exception {
        ctx.getChannel().sendMessage(new CommandEmbeds(ctx).inviteJda().build()).queue();
    }
}
