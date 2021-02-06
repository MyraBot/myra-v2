package com.myra.dev.marian.commands.economy;

import com.myra.dev.marian.commands.economy.administrator.shop.ShopRolesManager;
import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.database.allMethods.GetMember;
import com.myra.dev.marian.database.documents.ShopRolesDocument;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;

import java.util.HashMap;
import java.util.List;

@CommandSubscribe(
        name = "buy"
)
public class Buy implements Command {
    final public static HashMap<String, ShopRolesDocument> eventWaiter = new HashMap<>();

    @Override
    public void execute(CommandContext ctx) throws Exception {
        final List<ShopRolesDocument> roles = ShopRolesManager.getInstance().getRoles(ctx.getGuild()); // Get buyable roles

        // Show shop
        if (ctx.getArguments().length == 0) {
            // Create embed builder
            EmbedBuilder shop = new EmbedBuilder()
                    .setAuthor("shop", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().blue)
                    .setDescription("");

            // Add roles
            for (ShopRolesDocument role : roles) {
                // Role is invalid
                if (ctx.getGuild().getRoleById(role.getId()) == null) {
                    ShopRolesManager.getInstance().removeRole(ctx.getGuild(), role.getId()); // Remove role
                    continue;
                }
                shop.appendDescription("• " + ctx.getGuild().getRoleById(role.getId()).getAsMention() + " - " + Utilities.getUtils().formatNumber(role.getPrice()) + "\n");
            }
            shop.appendDescription("\nTo buy a role use `.buy <role>`");

            // Send message
            ctx.getChannel().sendMessage(shop.build()).queue();
            return;
        }

        // Get role name
        if (Utilities.getUtils().getRole(ctx.getEvent(), ctx.getArguments()[0], "buy", "\uD83D\uDED2") == null) return;
        final Role role = Utilities.getUtils().getRole(ctx.getEvent(), ctx.getArguments()[0], "buy", "\uD83D\uDED2");
        // Get role document
        ShopRolesDocument shopRole = null;
        for (ShopRolesDocument r : roles) {
            if (r.getId().equals(role.getId())) {
                shopRole = r;
            }
        }
        // Role isn't in the shop
        if (shopRole == null) {
            new Error(ctx.getEvent())
                    .setCommand("buy")
                    .setEmoji("\uD83D\uDED2")
                    .setMessage("Tried buying admin? Pfff *nice idea*")
                    .send();
            return;
        }
        // Member already owns this role
        if (ctx.getMember().getRoles().contains(ctx.getGuild().getRoleById(shopRole.getId()))) {
            eventWaiter.put(ctx.getMember().getId(), shopRole); // Add message to event waiter
            final int balance = new Database(ctx.getGuild()).getMembers().getMember(ctx.getMember()).getInteger("balance"); // Get members balance

            // Maximum amount of money would be reached
            if (balance + shopRole.getPrice() / 2 > Config.ECONOMY_MAX) {
                final int sellingPrice = Config.ECONOMY_MAX - balance; // Get price to sell the role

                EmbedBuilder removeRole = new EmbedBuilder()
                        .setAuthor("buy", null, ctx.getAuthor().getEffectiveAvatarUrl())
                        .setColor(Utilities.getUtils().blue)
                        .setDescription("You already own this role. You can't get the full amount of money, because you would be too richt. Do you want to sell this role for `" + sellingPrice + "` " + new Database(ctx.getGuild()).getNested("economy").getString("currency"));
                ctx.getChannel().sendMessage(removeRole.build()).queue();
            }
            // Maximum amount of money wouldn't be reached
            else {
                EmbedBuilder removeRole = new EmbedBuilder()
                        .setAuthor("buy", null, ctx.getAuthor().getEffectiveAvatarUrl())
                        .setColor(Utilities.getUtils().blue)
                        .setDescription("You already own this role. Do you want to sell this role for " + shopRole.getPrice() / 2 + " " + new Database(ctx.getGuild()).getNested("economy").getString("currency"));
                ctx.getChannel().sendMessage(removeRole.build()).queue();
            }
            return;
        }
        final GetMember member = new Database(ctx.getGuild()).getMembers().getMember(ctx.getMember()); // Get member in database
        // Not enough money
        if (member.getBalance() < shopRole.getPrice()) {
            new Error(ctx.getEvent())
                    .setCommand("buy")
                    .setEmoji("\uD83D\uDED2")
                    .setMessage("You don't have enough money")
                    .send();
            return;
        }
        // Add role
        try {
            ctx.getGuild().addRoleToMember(ctx.getMember(), role).queue();
        } catch (Exception e) {
            // Role to buy is higher than bot
            if (e.toString().startsWith("net.dv8tion.jda.api.exceptions.HierarchyException: Can't modify a role with higher or equal highest role than yourself!")) {
                new Error(ctx.getEvent())
                        .setCommand("buy")
                        .setEmoji("\uD83D\uDED2")
                        .setMessage("I couldn't assign this role, my role needs to be higher than the one you tried to buy")
                        .send();
                return;
            } else e.printStackTrace();
        }
        // Remove balance
        final int balance = member.getBalance();
        member.setBalance(balance - shopRole.getPrice());
        // Send success message
        EmbedBuilder success = new EmbedBuilder()
                .setAuthor("buy", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("You successfully bought " + role.getAsMention());
        ctx.getChannel().sendMessage(success.build()).queue();
    }
}
