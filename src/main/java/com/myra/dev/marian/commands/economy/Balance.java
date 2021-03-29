package com.myra.dev.marian.commands.economy;

import com.myra.dev.marian.database.allMethods.Database;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

@CommandSubscribe(
        name = "balance",
        aliases = {"bal", "money"}
)
public class Balance implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        // Get database
        Database db = new Database(ctx.getGuild());
        // Get currency
        String currency = db.getNested("economy").getString("currency").toString();
        // Usage
        if (ctx.getArguments().length > 1) {
            // When 'EconomySet' class is meant
            if (ctx.getArguments()[0].equalsIgnoreCase("set")) return;
            // Usage
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("balance", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "balance <user>`", currency + " │ Shows how many " + currency + " you have.", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
// Show balance
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
