package com.myra.dev.marian.commands.economy.administrator.shop;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Utilities;
import static com.myra.dev.marian.utilities.language.Lang.*;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.entities.Role;

public class ShopRemove implements CommandHandler {

    @CommandEvent(
            name = "shop remove",
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Usage
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("shop remove")
                    .addUsages(new Usage()
                            .setUsage("shop remove <role>")
                            .setEmoji("\u274C")
                            .setDescription(lang(ctx).get("description.shopRemove")))
                    .send();
            return;
        }
        // Get role
        final Role role = Utilities.getRole(ctx.getEvent(), ctx.getArguments()[0], "shop add", "\u26FD");
        if (role == null) return;

        ShopRolesManager.getInstance().removeRole(ctx.getGuild(), role.getId()); // Remove Role from shop
        // Send success message
        new Success(ctx.getEvent())
                .setCommand("shop remove")
                .setMessage(lang(ctx).get("command.economy.shop.remove.info.success")
                        .replace("{$role}", role.getAsMention())) // Removed shop role
                .send();
    }
}
