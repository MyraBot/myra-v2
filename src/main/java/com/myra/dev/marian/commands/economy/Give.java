package com.myra.dev.marian.commands.economy;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.Config;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.database.guild.member.GuildMember;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Format;
import com.myra.dev.marian.utilities.Utilities;
import static com.myra.dev.marian.utilities.language.Lang.*;
import net.dv8tion.jda.api.entities.Member;

public class Give implements CommandHandler {

    @CommandEvent(
            name = "give",
            aliases = {"transfer", "pay"}
    )
    public void execute(CommandContext ctx) throws Exception {
        // Usage
        if (ctx.getArguments().length != 2) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("give")
                    .addUsages(new Usage()
                            .setUsage("give <user> <balance>")
                            .setEmoji("\uD83D\uDCB8")
                            .setDescription(lang(ctx).get("description.economy.give")))
                    .send();
            return;
        }

        // Get provided member
        final Member recipient = Utilities.getMember(ctx.getEvent(), ctx.getArguments()[0], "give", "\uD83D\uDCB8");
        if (recipient == null) return;

        final Error error = new Error(ctx.getEvent())
                .setCommand("give")
                .setEmoji("\uD83D\uDCB8");

        // Amount of money aren't digits
        if (!ctx.getArguments()[1].matches("\\d+")) {
            error.setMessage(lang(ctx).get("error.invalid")).send();
            return;
        }

        final GuildMember dbRecipient = new MongoGuild(ctx.getGuild()).getMembers().getMember(recipient); // Get member in database
        final GuildMember dbAuthor = new MongoGuild(ctx.getGuild()).getMembers().getMember(ctx.getMember()); // Get author in database
        final int amount = Integer.parseInt(ctx.getArguments()[1]); // Money to transfer

        // User is bot
        if (recipient.getUser().isBot()) {
            error.setMessage(lang(ctx).get("command.economy.give.error.bots")).send();
            return;
        }
        // Don't have enough money
        if (dbAuthor.getBalance() < amount) {
            error.setMessage(lang(ctx).get("error.lessMoney")).send();
            return;
        }
        // Balance limit would be reached
        if (dbRecipient.getBalance() + amount > Config.ECONOMY_MAX) {
            error.setMessage(lang(ctx).get("command.economy.give.error.balanceLimit")).send();
            return;
        }

        // Recipient is author
        if (recipient == ctx.getMember()) {
            // Success
            new Success(ctx.getEvent())
                    .setCommand("give")
                    .setEmoji("\uD83D\uDCB8")
                    .setMessage(lang(ctx).get("command.economy.give.message.giveToAuthor"))
                    .send();
        } else {
            // Transfer money
            dbAuthor.setBalance(dbAuthor.getBalance() - amount); // Remove money of author
            dbRecipient.setBalance(dbRecipient.getBalance() + amount); // Add money to given member
            // Success message
            new Success(ctx.getEvent())
                    .setCommand("give")
                    .setEmoji("\uD83D\uDCB8")
                    .setMessage(lang(ctx).get("command.economy.give.message.success")
                            .replace("{$giver}", ctx.getMember().getAsMention()) // Member who gifts money
                            .replace("{$balance}", Format.number(Integer.parseInt(ctx.getArguments()[1]))) // Amount of transferred money
                            .replace("{$currency}", new MongoGuild(ctx.getGuild()).getNested("economy").getString("currency")))
                    .send();
        }
    }
}
