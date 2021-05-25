package com.myra.dev.marian.commands.economy;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.Config;
import com.myra.dev.marian.commands.economy.administrator.shop.ShopRolesManager;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.database.guild.ShopRolesDocument;
import com.myra.dev.marian.database.guild.member.GuildMember;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Format;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.myra.dev.marian.utilities.language.Lang.lang;

public class Buy implements CommandHandler {

    @CommandEvent(
            name = "buy",
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        final List<ShopRolesDocument> roles = ShopRolesManager.getInstance().getRoles(ctx.getGuild()); // Get buyable roles

        // Show shop
        if (ctx.getArguments().length == 0) {
            // Create message for shop
            final Success shop = new Success(ctx.getEvent())
                    .setCommand("shop")
                    .setEmoji("\uD83D\uDED2");

            // Add roles
            for (ShopRolesDocument role : roles) {
                // Role is invalid
                if (ctx.getGuild().getRoleById(role.getId()) == null) {
                    ShopRolesManager.getInstance().removeRole(ctx.getGuild(), role.getId()); // Remove role
                }
                // Role is valid
                else {
                    shop.appendMessage("• " + ctx.getGuild().getRoleById(role.getId()).getAsMention() + " - " + Format.number(role.getPrice()) + "\n"); // Add role to shop message
                }
            }
            shop.appendMessage("\n").appendMessage(lang(ctx).get("command.economy.buy.usage") // Add buy usage
                    .replace("{$prefix}", ctx.getPrefix()))
                    .send(); // Send shop overview
            return;
        }

        // Get provided role
        final Role role = Utilities.getRole(ctx.getEvent(), ctx.getArgumentsRaw(), "buy", "\uD83D\uDED2");
        if (role == null) return;

        // Get role document
        final Optional<ShopRolesDocument> find = roles.stream()
                .filter(r -> r.getId().equals(role.getId()))
                .findFirst();
        // Role is not for sale
        if (find.isEmpty()) {
            new Error(ctx.getEvent())
                    .setCommand("buy")
                    .setEmoji("\uD83D\uDED2")
                    .setMessage(lang(ctx).get("command.economy.buy.error.doesntExist"))
                    .send();
            return;
        }
        // Bot can't modify role
        if (!ctx.getGuild().getSelfMember().canInteract(role)) {
            new Error(ctx.getEvent())
                    .setCommand("buy")
                    .setEmoji("\uD83D\uDED2")
                    .setMessage(lang(ctx).get("error.roleHierarchy"))
                    .send();
            return;
        }

        final MongoGuild db = new MongoGuild(ctx.getGuild());
        final ShopRolesDocument roleInfo = find.get(); // Get right shop role
        // Sell role
        if (ctx.getMember().getRoles().contains(ctx.getGuild().getRoleById(roleInfo.getId()))) {
            final int balance = db.getMembers().getMember(ctx.getMember()).getBalance(); // Get members balance
            final String currency = db.getNested("economy").getString("currency"); // Get guild currency
            int sellPrice = roleInfo.getPrice() / 2; // Get price to sell role

            // Maximum amount of an would be reached
            if (balance + sellPrice > Config.ECONOMY_MAX) {
                sellPrice = Config.ECONOMY_MAX - balance; // Get missing balance to reach limit

                new Success(ctx.getEvent())
                        .setCommand("buy")
                        .setEmoji("\uD83D\uDED2")
                        .setMessage(lang(ctx).get("command.economy.buy.error.tooRich")
                                .replace("{$price}", Format.number(sellPrice)) // Selling price
                                .replace("{$currency}", currency)) // Server currency
                        .send();
            }
            // Maximum amount of money wouldn't be reached
            else {
                final Success confirmation = new Success(ctx.getEvent())
                        .setCommand("buy")
                        .setEmoji("\uD83D\uDED2")
                        .setMessage(lang(ctx).get("command.economy.buy.message.sell")
                                .replace("{$price}", Format.number(sellPrice)));// Selling price

                final int finalSellPrice = sellPrice; // Create final variable of selling price
                ctx.getChannel().sendMessage(confirmation.getEmbed().build()).queue(message -> {
                    // Event waiter
                    ctx.getWaiter().waitForEvent(GuildMessageReceivedEvent.class)
                            .setCondition(e -> !e.getAuthor().isBot() && e.getAuthor().getIdLong() == ctx.getAuthor().getIdLong())
                            .setAction(e -> {
                                final String response = e.getMessage().getContentRaw().toLowerCase(); // Get response as lower case

                                // Sell role
                                if (Arrays.stream(lang(ctx).getArray("array.yes")).anyMatch(word -> word.equalsIgnoreCase(response))) {
                                    new Success(ctx.getEvent())
                                            .setCommand("buy")
                                            .setEmoji("\uD83D\uDED2")
                                            .setMessage(String.format("Sold **%s** for `%s`%s", role.getAsMention(), finalSellPrice, currency))
                                            .send();
                                    ctx.getGuild().removeRoleFromMember(ctx.getMember(), role).queue(); // Remove role

                                    db.getMembers().getMember(ctx.getMember()).setBalance(balance + finalSellPrice); // Update balance
                                }
                                // Cancel selling role
                                else if (Arrays.stream(lang(ctx).getArray("array.no")).anyMatch(word -> word.equalsIgnoreCase(response))) {
                                    new Success(ctx.getEvent())
                                            .setCommand("buy")
                                            .setEmoji("\uD83D\uDED2")
                                            .setMessage(lang(ctx).get("command.economy.buy.message.canceled")
                                                    .replace("{$role}", role.getAsMention()))
                                            .send();
                                }
                            })
                            .load();
                });
            }
        }

        // Buy role
        else {
            final GuildMember member = db.getMembers().getMember(ctx.getMember()); // Get member in db
            // Not enough money
            if (member.getBalance() < roleInfo.getPrice()) {
                new Error(ctx.getEvent())
                        .setCommand("buy")
                        .setEmoji("\uD83D\uDED2")
                        .setMessage(lang(ctx).get("error.lessMoney"))
                        .send();
                return;
            }

            // Add role
            ctx.getGuild().addRoleToMember(ctx.getMember(), role).queue();
            // Remove balance
            final int balance = member.getBalance();
            member.setBalance(balance - roleInfo.getPrice());
            // Send success message
            EmbedBuilder success = new EmbedBuilder()
                    .setAuthor("buy", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.blue)
                    .setDescription(lang(ctx).get("command.economy.buy.message.bought")
                            .replace("{$role}", role.getAsMention()));
            ctx.getChannel().sendMessage(success.build()).queue();
        }
    }
}
