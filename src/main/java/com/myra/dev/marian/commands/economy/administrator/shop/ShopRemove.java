package com.myra.dev.marian.commands.economy.administrator.shop;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;

public class ShopRemove implements CommandHandler {

@CommandEvent(
        name = "shop remove",
        requires = Administrator.class
)
    public void execute(CommandContext ctx) throws Exception {
        // Usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("shop remove", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "shop remove <role>`", "\u274C │ Add roles to the shop", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
        // Get role
        if (Utilities.getUtils().getRole(ctx.getEvent(), ctx.getArguments()[0], "shop add", "\u26FD") == null)
            return; // Check for role
        final Role role = Utilities.getUtils().getRole(ctx.getEvent(), ctx.getArguments()[0], "shop add", "\u26FD"); // Store role
        // Remove Role
        ShopRolesManager.getInstance().removeRole(ctx.getGuild(), role.getId());
        // Send success message
        EmbedBuilder removedRole = new EmbedBuilder()
                .setAuthor("shop remove", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("Removed " + role.getAsMention() + " in the shop");
        ctx.getChannel().sendMessage(removedRole.build()).queue();
    }
}
