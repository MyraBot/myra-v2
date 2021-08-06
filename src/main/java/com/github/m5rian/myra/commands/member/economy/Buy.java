package com.github.m5rian.myra.commands.member.economy;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.commandMessages.CommandMessage;
import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.commands.member.economy.administrator.shop.ShopRolesManager;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.database.guild.ShopRolesDocument;
import com.github.m5rian.myra.database.guild.member.GuildMember;
import com.github.m5rian.myra.utilities.Format;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.language.Lang;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Buy implements CommandHandler {

    @CommandEvent(
            name = "buy",
            args = {"<role>"},
            emoji = "\uD83D\uDED2",
            description = "description.economy.buy",
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        final List<ShopRolesDocument> roles = ShopRolesManager.getInstance().getRoles(ctx.getGuild()); // Get buyable roles

        // Show shop
        if (ctx.getArguments().length == 0) {
            final CommandMessage shop = info(ctx); // Crate message for shop
            roles.forEach(role -> {
                // Role is invalid
                if (ctx.getGuild().getRoleById(role.getId()) == null) {
                    ShopRolesManager.getInstance().removeRole(ctx.getGuild(), role.getId()); // Remove role
                }
                // Role is valid
                else {
                    shop.appendDescription("• " + Utilities.mentionRole(role.getId()) + " - " + Format.number(role.getPrice()) + "\n"); // Add role to shop message
                }
            });
            shop.appendDescription("\n").appendDescription(Lang.lang(ctx).get("command.economy.buy.usage") // Add buy usage
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
            error(ctx).setDescription(lang(ctx).get("command.economy.buy.error.doesntExist")).send();
            return;
        }
        // Bot can't modify role
        if (!ctx.getGuild().getSelfMember().canInteract(role)) {
            error(ctx).setDescription(lang(ctx).get("error.roleHierarchy")).send();
            return;
        }

        final MongoGuild db = MongoGuild.get(ctx.getGuild());
        final ShopRolesDocument roleInfo = find.get(); // Get right shop role
        // Sell role
        if (ctx.getMember().getRoles().contains(ctx.getGuild().getRoleById(roleInfo.getId()))) {
            final int balance = GuildMember.get(ctx.getMember()).getBalance(); // Get members balance
            final String currency = db.getNested("economy").getString("currency"); // Get guild currency
            int sellPrice = roleInfo.getPrice() / 2; // Get price to sell role

            // Maximum amount of an would be reached
            if (balance + sellPrice > Config.ECONOMY_MAX) {
                sellPrice = Config.ECONOMY_MAX - balance; // Get missing balance to reach limit

                info(ctx).setDescription(lang(ctx).get("command.economy.buy.error.tooRich")
                        .replace("{$price}", Format.number(sellPrice)) // Selling price
                        .replace("{$currency}", currency)) // Server currency
                        .send();
            }
            // Maximum amount of money wouldn't be reached
            else {
                final EmbedBuilder confirmation = info(ctx).setDescription(lang(ctx).get("command.economy.buy.message.sell")
                        .replace("{$price}", Format.number(sellPrice)) // Selling price
                        .replace("{$currency}", currency)) // Server currency
                        .getEmbed();

                final int finalSellPrice = sellPrice; // Create final variable of selling price
                ctx.getChannel().sendMessageEmbeds(confirmation.build()).queue(message -> {
                    // Event waiter
                    ctx.getWaiter().waitForEvent(GuildMessageReceivedEvent.class)
                            .setCondition(e -> !e.getAuthor().isBot() && e.getAuthor().getIdLong() == ctx.getAuthor().getIdLong())
                            .setAction(e -> {
                                final String response = e.getMessage().getContentRaw().toLowerCase(); // Get response as lower case

                                // Sell role
                                if (Arrays.stream(Lang.lang(ctx).getArray("array.yes")).anyMatch(word -> word.equalsIgnoreCase(response))) {
                                    info(ctx).setDescription(String.format("Sold **%s** for `%s`%s", role.getAsMention(), finalSellPrice, currency)).send();

                                    ctx.getGuild().removeRoleFromMember(ctx.getMember(), role).queue(); // Remove role
                                    GuildMember.get(ctx.getMember()).setBalance(balance + finalSellPrice); // Update balance
                                }
                                // Cancel selling role
                                else if (Arrays.stream(Lang.lang(ctx).getArray("array.no")).anyMatch(word -> word.equalsIgnoreCase(response))) {
                                    info(ctx).setDescription(lang(ctx).get("command.economy.buy.message.canceled")
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
            final GuildMember member = GuildMember.get(ctx.getMember()); // Get member in db
            // Not enough money
            if (member.getBalance() < roleInfo.getPrice()) {
                error(ctx).setDescription(lang(ctx).get("error.lessMoney")).send();
                return;
            }

            ctx.getGuild().addRoleToMember(ctx.getMember(), role).queue(); // Add role
            // Remove balance
            final int balance = member.getBalance();
            member.setBalance(balance - roleInfo.getPrice());
            // Send success message
            info(ctx).setDescription(lang(ctx).get("command.economy.buy.message.bought")
                    .replace("{$role}", role.getAsMention()))
                    .send();
        }
    }
}
