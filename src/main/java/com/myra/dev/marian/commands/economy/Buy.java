package com.myra.dev.marian.commands.economy;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.Myra;
import com.myra.dev.marian.commands.economy.administrator.shop.ShopRolesManager;
import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.database.allMethods.GetMember;
import com.myra.dev.marian.database.documents.ShopRolesDocument;
import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.Optional;

@CommandSubscribe(
        name = "buy",
        channel = Channel.GUILD
)
public class Buy implements Command {

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
            shop.appendDescription("\nTo buy a role use `" + ctx.getPrefix() + "buy <role>`");

            // Send message
            ctx.getChannel().sendMessage(shop.build()).queue();
            return;
        }


        final Utilities utilities = Utilities.getUtils();
        // Get role name
        if (utilities.getRole(ctx.getEvent(), ctx.getArgumentsRaw(), "buy", "\uD83D\uDED2") == null) return;
        final Role role = utilities.getRole(ctx.getEvent(), ctx.getArgumentsRaw(), "buy", "\uD83D\uDED2");

        // Get role document
        final Optional<ShopRolesDocument> find = roles.stream()
                .filter(r -> r.getId().equals(role.getId()))
                .findFirst();

        if (find.isEmpty()) {
            new Error(ctx.getEvent())
                    .setCommand("buy")
                    .setEmoji("\uD83D\uDED2")
                    .setMessage("Tried buying admin? Pfff *nice idea*")
                    .send();
            return;
        }
        final ShopRolesDocument roleInfo = find.get();


        final Database db = new Database(ctx.getGuild());
        // Sell role
        if (ctx.getMember().getRoles().contains(ctx.getGuild().getRoleById(roleInfo.getId()))) {
            final int balance = db.getMembers().getMember(ctx.getMember()).getInteger("balance"); // Get members balance
            final int sellPrice = roleInfo.getPrice() / 2; // Get price to sell role

            // Maximum amount of money would be reached
            if (balance + sellPrice > Config.ECONOMY_MAX) {
                final int sellingPrice = Config.ECONOMY_MAX - balance; // Get price to sell the role

                EmbedBuilder removeRole = new EmbedBuilder()
                        .setAuthor("buy", null, ctx.getAuthor().getEffectiveAvatarUrl())
                        .setColor(Utilities.getUtils().blue)
                        .setDescription("You already own this role. You can't get the full amount of money, because you would be too rich. Do you want to sell this role for `" + sellingPrice + "` " + db.getNested("economy").getString("currency"));
                ctx.getChannel().sendMessage(removeRole.build()).queue();
            }
            // Maximum amount of money wouldn't be reached
            else {
                EmbedBuilder removeRole = new EmbedBuilder()
                        .setAuthor("buy", null, ctx.getAuthor().getEffectiveAvatarUrl())
                        .setColor(Utilities.getUtils().blue)
                        .setDescription("You already own this role. Do you want to sell this role for " + sellPrice + " " + db.getNested("economy").getString("currency"));
                ctx.getChannel().sendMessage(removeRole.build()).queue(message -> {
                    // Event waiter
                    Myra.WAITER.waitForEvent(
                            GuildMessageReceivedEvent.class, // Event to wait for
                            e -> !e.getAuthor().isBot()
                                    && e.getAuthor().getIdLong() == ctx.getAuthor().getIdLong(),
                            e -> { // On event
                                final String response = e.getMessage().getContentRaw(); // Get response

                                // Sell role
                                if (response.equalsIgnoreCase("yes") || response.equalsIgnoreCase("ye")) {
                                    final String currency = db.getNested("economy").getString("currency"); // Get currency
                                    new Success(ctx.getEvent())
                                            .setCommand("buy")
                                            .setEmoji("\uD83D\uDED2")
                                            .setMessage(String.format("Sold **%s** for `%s`%s", role.getAsMention(), sellPrice, currency))
                                            .send();
                                    ctx.getGuild().removeRoleFromMember(ctx.getMember(), role).queue(); // Remove role

                                    db.getMembers().getMember(ctx.getMember()).setBalance(balance - sellPrice);
                                }
                                // Cancel selling role
                                else if (response.equalsIgnoreCase("no") || response.equalsIgnoreCase("nope")) {
                                    new Success(ctx.getEvent())
                                            .setCommand("buy")
                                            .setEmoji("\uD83D\uDED2")
                                            .setMessage(String.format("Canceled selling **%s**... Maybe next time?", role.getAsMention()))
                                            .send();
                                }
                            }
                    );
                });
            }
        }

        // Buy role
        else {
            final GetMember member = db.getMembers().getMember(ctx.getMember()); // Get member in db
            // Not enough money
            if (member.getBalance() < roleInfo.getPrice()) {
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
            member.setBalance(balance - roleInfo.getPrice());
            // Send success message
            EmbedBuilder success = new EmbedBuilder()
                    .setAuthor("buy", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().blue)
                    .setDescription("You successfully bought " + role.getAsMention());
            ctx.getChannel().sendMessage(success.build()).queue();
        }
    }
}
