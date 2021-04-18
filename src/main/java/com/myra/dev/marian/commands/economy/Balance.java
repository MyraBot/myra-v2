package com.myra.dev.marian.commands.economy;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

@CommandSubscribe(
        name = "balance",
        aliases = {"bal", "money"}
)
public class Balance implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        final Utilities utilities = Utilities.getUtils(); // Get utilities
        final MongoGuild db = new MongoGuild(ctx.getGuild()); // Get database
        final String currency = db.getNested("economy").getString("currency"); // Get currency
        // Command usage
        if (ctx.getArguments().length > 1) {
            // "economy set" is meant
            if (ctx.getArguments()[0].equalsIgnoreCase("set")) return;

            new CommandUsage(ctx.getEvent())
                    .setCommand("balance")
                    .addUsages(new Usage()
                            .setUsage("balance (user)")
                            .setEmoji(currency)
                            .setDescription("Shows how many " + currency + " you have"))
                    .send();
            return;
        }

        // Get self user
        Member member = ctx.getMember();
        // Get given user
        if (ctx.getArguments().length == 1) {
            member = Utilities.getUtils().getMember(ctx.getEvent(), ctx.getArguments()[0], "balance", currency);
            if (member == null) return;
        }

        final String userBalance = utilities.formatNumber(db.getMembers().getMember(member).getBalance()); // Make number looks nicer
        // Send balance
        EmbedBuilder balance = new EmbedBuilder()
                .setAuthor("balance", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(member.getColor())
                .setDescription(member.getAsMention() + "'s balance is `" + userBalance + "` " + currency);
        ctx.getChannel().sendMessage(balance.build()).queue();
    }
}
