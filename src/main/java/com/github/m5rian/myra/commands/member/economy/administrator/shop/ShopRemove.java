package com.github.m5rian.myra.commands.member.economy.administrator.shop;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.Utilities;
import static com.github.m5rian.myra.utilities.language.Lang.*;

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
