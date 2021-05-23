package com.myra.dev.marian.commands.economy;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import static com.myra.dev.marian.utilities.language.Lang.*;
import com.myra.dev.marian.utilities.permissions.Administrator;

public class EconomyHelp implements CommandHandler {

    @CommandEvent(
            name = "economy",
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length == 0) {
            // Send command usages
            new CommandUsage(ctx.getEvent())
                    .setCommand("notifications")
                    .addUsages(
                            new Usage()
                                    .setUsage("economy set <user> <balance>")
                                    .setEmoji("\uD83D\uDC5B")
                                    .setDescription(lang(ctx).get("description.economySet")),
                            new Usage()
                                    .setUsage("economy currency <currency>")
                                    .setEmoji("new MongoGuild(ctx.getGuild()).getNested(\"economy\").getString(\"currency\")")
                                    .setDescription(lang(ctx).get("description.economy.currency")))
                    .send();
        }
    }
}
