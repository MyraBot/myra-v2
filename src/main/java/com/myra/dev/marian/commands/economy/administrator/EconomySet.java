package com.myra.dev.marian.commands.economy.administrator;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.Config;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

public class EconomySet implements CommandHandler {

@CommandEvent(
        name = "economy set",
        aliases = {"balance set", "bal set", "money set"},
        requires = Administrator.class
)
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        // Usage
        if (ctx.getArguments().length != 2) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("economy set", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "economy set <user> <balance>`", "\uD83D\uDC5B │ Change a users balance", false)
                    .setFooter("Use: + / -, to add and subtract money");
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
// Change balance
        final Member member = utilities.getMember(ctx.getEvent(), ctx.getArguments()[0], "economy set", "\uD83D\uDC5B");
        if (member == null) return;

        final MongoGuild db = new MongoGuild(ctx.getGuild()); // Get database
        int updatedBalance = db.getMembers().getMember(member).getBalance(); // Get old balance

        long amount = Long.parseLong(ctx.getArguments()[1]); // Get amount of money to set/add/remove
        if (amount > Config.ECONOMY_MAX || amount < -Config.ECONOMY_MAX) { // Limit would be reached
            new Error(ctx.getEvent())
                    .setCommand("economy set")
                    .setEmoji("\uD83D\uDC5B")
                    .setMessage("You can set an amount of money between +" + Config.ECONOMY_MAX + " and -" + Config.ECONOMY_MAX)
                    .send();
            return;
        }

        // Add balance
        if (ctx.getArguments()[1].matches("[+]\\d+")) { // Amount of money is too much
            updatedBalance += Integer.parseInt(ctx.getArguments()[1].substring(1)); // Add balance
        }
        // Subtract balance
        else if (ctx.getArguments()[1].matches("[-]\\d+")) {
            updatedBalance -= Integer.parseInt(ctx.getArguments()[1].substring(1)); // Subtract balance
        }
        // Set balance
        else if (ctx.getArguments()[1].matches("\\d+")) {
            updatedBalance = Integer.parseInt(ctx.getArguments()[1]); // Set new balance
        }
        // Error
        else {
            new Error(ctx.getEvent())
                    .setCommand("economy set")
                    .setEmoji("\uD83D\uDC5B")
                    .setMessage("Invalid operator")
                    .setFooter("Please use `+` to add money, `-` to subtract money or leave the operators out to set an exact amount of money")
                    .send();
            return;
        }
        // Balance limit would be reached
        if (updatedBalance > Config.ECONOMY_MAX) {
            new Error(ctx.getEvent())
                    .setCommand("economy set")
                    .setEmoji("\uD83D\uDC5B")
                    .setMessage("No, that's too much money")
                    .send();
            return;
        }
        // Change balance in database
        db.getMembers().getMember(member).setBalance(updatedBalance);
        // Success
        Success success = new Success(ctx.getEvent())
                .setCommand("economy set")
                .setEmoji("\uD83D\uDC5B")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setMessage(member.getAsMention() + "has now `" + utilities.formatNumber(updatedBalance) + "` " + db.getNested("economy").getString("currency"));
        success.send();
    }
}
