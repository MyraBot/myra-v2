package com.myra.dev.marian.commands.economy.administrator.shop;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.Config;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;

public class ShopAdd implements CommandHandler {

@CommandEvent(
        name = "shop add",
        requires = Administrator.class
)
    public void execute(CommandContext ctx) throws Exception {
        // Usage
        if (ctx.getArguments().length != 2) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("shop add", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "shop add <role> <price>`", "\u26FD │ Add roles to the shop", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
        // Get role
        if (Utilities.getUtils().getRole(ctx.getEvent(), ctx.getArguments()[0], "shop add", "\u26FD") == null)
            return; // Check for role
        final Role role = Utilities.getUtils().getRole(ctx.getEvent(), ctx.getArguments()[0], "shop add", "\u26FD"); // Store role
        // Price isn't a number
        if (!ctx.getArguments()[1].matches("\\d+")) {
            new Error(ctx.getEvent())
                    .setCommand("shop add")
                    .setEmoji("\u26FD")
                    .setMessage("Invalid number")
                    .send();
            return;
        }
        // Price is more than the maximum amount of money
        if (Integer.parseInt(ctx.getArguments()[1]) > Config.ECONOMY_MAX) {
            new Error(ctx.getEvent())
                    .setCommand("shop add")
                    .setEmoji("\u26FD")
                    .setMessage("This is too expensive")
                    .send();
            return;
        }
        // Add new Role
        ShopRolesManager.getInstance().addRole(ctx.getGuild(), role.getId(), Integer.valueOf(ctx.getArguments()[1]));
        // Send success message
        EmbedBuilder roleAdd = new EmbedBuilder()
                .setAuthor("shop add", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("Added " + role.getAsMention() + " to the shop for " + ctx.getArguments()[1] + " " + new MongoGuild(ctx.getGuild()).getNested("economy").getString("currency"));
        ctx.getChannel().sendMessage(roleAdd.build()).queue();
    }
}
