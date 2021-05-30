package com.github.m5rian.myra.commands.member.music;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.CommandEmbeds;

public class MusicHelp implements CommandHandler {

    @CommandEvent(
            name = "music",
            aliases = {"radio"},
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // No arguments
        if (ctx.getArguments().length == 0) {
            ctx.getChannel().sendMessage(new CommandEmbeds(ctx.getGuild(), ctx.getAuthor()).music().build()).queue();
        }
    }
}
