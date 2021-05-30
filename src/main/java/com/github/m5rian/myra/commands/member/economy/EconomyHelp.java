package com.github.m5rian.myra.commands.member.economy;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.permissions.Administrator;

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
                                    .setDescription(Lang.lang(ctx).get("description.economySet")),
                            new Usage()
                                    .setUsage("economy currency <currency>")
                                    .setEmoji("new MongoGuild(ctx.getGuild()).getNested(\"economy\").getString(\"currency\")")
                                    .setDescription(Lang.lang(ctx).get("description.economy.currency")))
                    .send();
        }
    }
}
