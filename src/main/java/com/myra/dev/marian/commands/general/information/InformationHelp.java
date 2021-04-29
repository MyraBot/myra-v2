package com.myra.dev.marian.commands.general.information;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;

public class InformationHelp implements CommandHandler {

@CommandEvent(
        name = "information",
        aliases = {"info"},
        channel = Channel.GUILD
)
    public void execute(CommandContext ctx) throws Exception {
        // No arguments
        if (ctx.getArguments().length != 0) return;

        // Command usage
        new CommandUsage(ctx.getEvent())
                .setCommand("information")
                .addUsages(
                        new Usage()
                                .setUsage("information user (user)")
                                .setEmoji("\uD83D\uDC64")
                                .setDescription("Get information about a user"),
                        new Usage()
                                .setUsage("information member (member)")
                                .setEmoji("\uD83D\uDC6A")
                                .setDescription("Get information about a server member"),
                        new Usage()
                                .setUsage("information server")
                                .setEmoji("\uD83D\uDDFA")
                                .setDescription("Get information about this server"),
                        new Usage()
                                .setUsage("information bot")
                                .setEmoji("\uD83D\uDD0C")
                                .setDescription("Get information me :)")
                ).send();
    }
}
