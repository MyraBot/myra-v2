package com.github.m5rian.myra.commands.member.economy;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.Format;
import com.github.m5rian.myra.utilities.Utilities;
import net.dv8tion.jda.api.entities.Member;

public class Balance implements CommandHandler {

    @CommandEvent(
            name = "balance",
            aliases = {"bal", "money"},
            args = {"(member)"},
            emoji = "{$guild.currency}",
            description = "description.economy.balance"
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length > 0 && ctx.getArguments()[0].equalsIgnoreCase("set")) return; // "economy set" is meant

        final MongoGuild db = new MongoGuild(ctx.getGuild()); // Get database
        final String currency = db.getNested("economy").getString("currency"); // Get currency
        // Command usage
        if (ctx.getArguments().length > 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("balance")
                    .addUsages(new Usage()
                            .setUsage("balance (user)")
                            .setEmoji(currency)
                            .setDescription(Lang.lang(ctx).get("description.economy.balance")))
                    .send();
            return;
        }

        Member member = ctx.getMember(); // Get self user
        // A different user is given
        if (ctx.getArguments().length == 1) {
            member = Utilities.getMember(ctx.getEvent(), ctx.getArguments()[0], "balance", currency); // Get provided user
            if (member == null) return;
        }

        final Integer balance = db.getMembers().getMember(member).getBalance(); // Get user balance
        // Send balance
        new Success(ctx.getEvent())
                .setCommand("balance")
                .setEmoji(currency)
                .setMessage(Lang.lang(ctx).get("command.economy.balance.success")
                        .replace("{$member}", member.getAsMention()) // Member
                        .replace("{$balance}", Format.number(balance)) // Balance amount
                        .replace("{$currency}", currency)) // Guild currency
                .send();
    }
}
