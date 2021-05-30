package com.github.m5rian.myra.commands.member.economy.administrator.shop;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.permissions.Administrator;

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
                                    .setDescription(Lang.lang(ctx).get("description.shopAdd")),
                            new Usage().setUsage("shop remove <role>")
                                    .setEmoji("\u274C")
                                    .setDescription(Lang.lang(ctx).get("description.shopRemove")))
                    .send();
            return;
        }
    }
}
