package com.myra.dev.marian.commands.economy.administrator;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.Config;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Format;
import com.myra.dev.marian.utilities.Utilities;
import static com.myra.dev.marian.utilities.language.Lang.*;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.entities.Member;

public class EconomySet implements CommandHandler {

    @CommandEvent(
            name = "economy set",
            aliases = {"balance set", "bal set", "money set"},
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Usage
        if (ctx.getArguments().length != 2) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("economy set")
                    .addUsages(new Usage()
                            .setUsage("economy set <user> <balance>")
                            .setEmoji("\uD83D\uDC5B")
                            .setDescription(lang(ctx).get("description.economySet")))
                    .addInformation(lang(ctx).get("command.economy.set.info.info"))
                    .send();
            return;
        }

        // Get provided member
        final Member member = Utilities.getMember(ctx.getEvent(), ctx.getArguments()[0], "economy set", "\uD83D\uDC5B");
        if (member == null) return;

        // Input balance aren't numbers
        if (!ctx.getArguments()[1].matches("\\d+")) {
            new Error(ctx.getEvent())
                    .setCommand("economy set")
                    .setEmoji("\uD83D\uDC5B")
                    .setMessage("error.invalid")
                    .send();
            return;
        }

        final MongoGuild db = new MongoGuild(ctx.getGuild()); // Get database
        long balance = db.getMembers().getMember(member).getBalance(); // Get current balance
        final long input = Long.parseLong(ctx.getArguments()[1].substring(1)); // Get input number without operator
        // Add balance
        if (ctx.getArguments()[1].matches("[+]\\d+")) balance += input;
            // Subtract balance
        else if (ctx.getArguments()[1].matches("[-]\\d+")) balance -= input;
            // Set balance
        else if (ctx.getArguments()[1].matches("\\d+")) balance = input;

            // Invalid operator
        else {
            new Error(ctx.getEvent())
                    .setCommand("economy set")
                    .setEmoji("\uD83D\uDC5B")
                    .setMessage(lang(ctx).get("error.invalid.operator"))
                    .setFooter("Please use `+` to add money, `-` to subtract money or leave the operators out to set an exact amount of money")
                    .send();
            return;
        }

        // Upper balance limit would be reached
        if (balance > Config.ECONOMY_MAX) {
            new Error(ctx.getEvent())
                    .setCommand("economy set")
                    .setEmoji("\uD83D\uDC5B")
                    .setMessage(lang(ctx).get("command.economy.set.error.limit1"))
                    .send();
            return;
        }
        // Lower balance limit would be reached
        if (balance < -Config.ECONOMY_MAX) {
            new Error(ctx.getEvent())
                    .setCommand("economy set")
                    .setEmoji("\uD83D\uDC5B")
                    .setMessage(lang(ctx).get("command.economy.set.error.limit2"))
                    .send();
            return;
        }

        db.getMembers().getMember(member).setBalance(Math.toIntExact(balance)); // Change balance in database
        // Success
        new Success(ctx.getEvent())
                .setCommand("economy set")
                .setEmoji("\uD83D\uDC5B")
                .setMessage(lang(ctx).get("command.economy.set.success")
                        .replace("{$member}", member.getAsMention())
                        .replace("{$balance}", Format.number(Math.toIntExact(balance))) // New balance
                        .replace("{$currency}", db.getNested("economy").getString("currency"))) // Guild currency
                .send();
    }
}
