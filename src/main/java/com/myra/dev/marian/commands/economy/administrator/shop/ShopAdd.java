package com.myra.dev.marian.commands.economy.administrator.shop;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.Config;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Utilities;
import static com.myra.dev.marian.utilities.language.Lang.*;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;

public class ShopAdd implements CommandHandler {

    @CommandEvent(
            name = "shop add",
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 2) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("shop add")
                    .addUsages(new Usage()
                            .setUsage("shop add <role> <price>")
                            .setEmoji("\u26FD")
                            .setDescription(lang(ctx).get("description.shopAdd")))
                    .send();
            return;
        }

        // Get provided role
        final Role role = Utilities.getRole(ctx.getEvent(), ctx.getArguments()[0], "shop add", "\u26FD");
        if (role == null) return;

        // Price isn't a number
        if (!ctx.getArguments()[1].matches("\\d+")) {
            new Error(ctx.getEvent())
                    .setCommand("shop add")
                    .setEmoji("\u26FD")
                    .setMessage(lang(ctx).get("error.invalid"))
                    .send();
            return;
        }
        // Price is more than the maximum amount of money
        if (Integer.parseInt(ctx.getArguments()[1]) > Config.ECONOMY_MAX) {
            new Error(ctx.getEvent())
                    .setCommand("shop add")
                    .setEmoji("\u26FD")
                    .setMessage(lang(ctx).get("command.economy.shop.add.error.tooExpensive"))
                    .send();
            return;
        }

        ShopRolesManager.getInstance().addRole(ctx.getGuild(), role.getId(), Integer.valueOf(ctx.getArguments()[1])); // Add new Role to shop
        // Send success message
        new Success(ctx.getEvent())
                .setCommand("shop add")
                .setMessage(lang(ctx).get("command.economy.shop.add.info.success")
                        .replace("{$role}", role.getAsMention()) // Shop role
                        .replace("{$price}", ctx.getArguments()[1]) // Price of role
                        .replace("{$currency}", new MongoGuild(ctx.getGuild()).getNested("economy").getString("currency"))) // Server currency
                .send();
    }
}
