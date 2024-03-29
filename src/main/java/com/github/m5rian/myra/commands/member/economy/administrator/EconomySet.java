package com.github.m5rian.myra.commands.member.economy.administrator;

import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.database.guild.member.GuildMember;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Format;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import net.dv8tion.jda.api.entities.Member;

public class EconomySet implements CommandHandler {

    @CommandEvent(
            name = "economy set",
            aliases = {"balance set", "bal set", "money set"},
            args = {"<user>", "<balance>"},
            emoji = "\uD83D\uDC5B",
            description = "description.economySet",
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
                            .setDescription(Lang.lang(ctx).get("description.economySet")))
                    .addInformation(Lang.lang(ctx).get("command.economy.set.info.info"))
                    .send();
            return;
        }

        // Get provided member
        final Member member = Utilities.getMember(ctx.getEvent(), ctx.getArguments()[0], "economy set", "\uD83D\uDC5B");
        if (member == null) return;

        final MongoGuild db = MongoGuild.get(ctx.getGuild()); // Get database
        long balance = GuildMember.get(member).getBalance(); // Get current balance

        // Add balance
        if (ctx.getArguments()[1].matches("\\+\\d+")) {
            balance += Long.parseLong(ctx.getArguments()[1].substring(1)); // Get input number without operator
        }
        // Subtract balance
        else if (ctx.getArguments()[1].matches("-\\d+")) {
            balance -= Long.parseLong(ctx.getArguments()[1].substring(1)); // Get input number without operator
        }
        // Set balance
        else if (ctx.getArguments()[1].matches("\\d+")) {
            balance = Long.parseLong(ctx.getArguments()[1]); // Get input number
        }
        // Invalid operator
        else {
            error(ctx).setMessage(Lang.lang(ctx).get("error.invalid")).send();
            return;
        }

        // Upper balance limit would be reached
        if (balance > Config.ECONOMY_MAX) {
            new Error(ctx.getEvent())
                    .setCommand("economy set")
                    .setEmoji("\uD83D\uDC5B")
                    .setMessage(Lang.lang(ctx).get("command.economy.set.error.limit1"))
                    .send();
            return;
        }
        // Lower balance limit would be reached
        if (balance < -Config.ECONOMY_MAX) {
            new Error(ctx.getEvent())
                    .setCommand("economy set")
                    .setEmoji("\uD83D\uDC5B")
                    .setMessage(Lang.lang(ctx).get("command.economy.set.error.limit2"))
                    .send();
            return;
        }

        GuildMember.get(member).setBalance(Math.toIntExact(balance)); // Change balance in database
        // Success
        new Success(ctx.getEvent())
                .setCommand("economy set")
                .setEmoji("\uD83D\uDC5B")
                .setMessage(Lang.lang(ctx).get("command.economy.set.success")
                        .replace("{$member}", member.getAsMention())
                        .replace("{$balance}", Format.number(Math.toIntExact(balance))) // New balance
                        .replace("{$currency}", db.getNested("economy").getString("currency"))) // Guild currency
                .send();
    }
}
