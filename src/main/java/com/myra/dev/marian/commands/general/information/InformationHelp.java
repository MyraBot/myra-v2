package com.myra.dev.marian.commands.general.information;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import static com.myra.dev.marian.utilities.language.Lang.*;

public class InformationHelp implements CommandHandler {

    @CommandEvent(
            name = "information",
            aliases = {"info"},
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length == 0) {
            // Command usage
            new CommandUsage(ctx.getEvent())
                    .setCommand("information")
                    .addUsages(
                            new Usage()
                                    .setUsage("information user (user)")
                                    .setEmoji("\uD83D\uDC64")
                                    .setDescription(lang(ctx).get("description.general.info.user")),
                            new Usage()
                                    .setUsage("information member (member)")
                                    .setEmoji("\uD83D\uDC6A")
                                    .setDescription(lang(ctx).get("description.general.info.member")),
                            new Usage()
                                    .setUsage("information server")
                                    .setEmoji("\uD83D\uDDFA")
                                    .setDescription(lang(ctx).get("description.general.info.server")),
                            new Usage()
                                    .setUsage("information bot")
                                    .setEmoji("\uD83D\uDD0C")
                                    .setDescription(lang(ctx).get("description.general.info.bot")))
                    .send();
        }
    }
}
