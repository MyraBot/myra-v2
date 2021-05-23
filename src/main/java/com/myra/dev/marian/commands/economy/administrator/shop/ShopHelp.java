package com.myra.dev.marian.commands.economy.administrator.shop;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import static com.myra.dev.marian.utilities.language.Lang.*;
import com.myra.dev.marian.utilities.permissions.Administrator;

public class ShopHelp implements CommandHandler {

    @CommandEvent(
            name = "shop",
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 2) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("shop add")
                    .addUsages(new Usage().setUsage("shop add <role> <price>")
                                    .setEmoji("\u26FD")
                                    .setDescription(lang(ctx).get("description.shopAdd")),
                            new Usage().setUsage("shop remove <role>")
                                    .setEmoji("\u274C")
                                    .setDescription(lang(ctx).get("description.shopRemove")))
                    .send();
            return;
        }
    }
}
