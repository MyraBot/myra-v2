package com.myra.dev.marian.commands.economy;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.Config;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.database.guild.member.GuildMember;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

@CommandSubscribe(
        name = "give",
        aliases = {"transfer", "pay"}
)
public class Give implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Usage
        if (ctx.getArguments().length != 2) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("give")
                    .addUsages(new Usage()
                            .setUsage("give <user> <balance>")
                            .setEmoji("\uD83D\uDCB8")
                            .setDescription("Give credits to other users"))
                    .send();
        }
        // Get user
        final Member recipient = Utilities.getUtils().getMember(ctx.getEvent(), ctx.getArguments()[0], "give", "\uD83D\uDCB8");
        if (recipient == null) return;

        // Amount of money aren't digits
        if (!ctx.getArguments()[1].matches("\\d+")) {
            new Error(ctx.getEvent())
                    .setCommand("give")
                    .setEmoji("\uD83D\uDCB8")
                    .setMessage("Invalid number")
                    .send();
            return;
        }

        final GuildMember dbRecipient = new MongoGuild(ctx.getGuild()).getMembers().getMember(recipient); // Get member in database
        final GuildMember dbAuthor = new MongoGuild(ctx.getGuild()).getMembers().getMember(ctx.getMember()); // Get author in database
        final int amount = Integer.parseInt(ctx.getArguments()[1]); // Money to transfer

        // User is bot
        if (recipient.getUser().isBot()) {
            new Error(ctx.getEvent())
                    .setCommand("give")
                    .setEmoji("\uD83D\uDCB8")
                    .setMessage("I wouldn't do that, bots can cheat infinite money... So why giving them even more?")
                    .send();
            return;
        }
        // Don't have enough money
        if (dbAuthor.getBalance() < amount) {
            new Error(ctx.getEvent())
                    .setCommand("give")
                    .setEmoji("\uD83D\uDCB8")
                    .setMessage("You don't have enough money")
                    .send();
            return;
        }
        // Balance limit would be reached
        if (dbRecipient.getBalance() + amount > Config.ECONOMY_MAX) {
            new Error(ctx.getEvent())
                    .setCommand("give")
                    .setEmoji("\uD83D\uDCB8")
                    .setMessage("What are you doing? The member you want to give money is already rich")
                    .send();
            return;
        }

        // Recipient is author
        if (recipient == ctx.getMember()) {
            // Success message
            EmbedBuilder success = new EmbedBuilder()
                    .setAuthor("give", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().blue)
                    .setDescription("No idea why you've done that...");
            ctx.getChannel().sendMessage(success.build()).queue();
        }

        else {
            // Transfer money
            dbAuthor.setBalance(dbAuthor.getBalance() - amount); // Remove money of author
            dbRecipient.setBalance(dbRecipient.getBalance() + amount); // Add money to given member
            // Success message
            EmbedBuilder success = new EmbedBuilder()
                    .setAuthor("give", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().blue)
                    .setDescription(ctx.getAuthor().getAsMention() + " gave you `" + Utilities.getUtils().formatNumber(Integer.parseInt(ctx.getArguments()[1])) + "` " + new MongoGuild(ctx.getGuild()).getNested("economy").getString("currency"));
            ctx.getChannel().sendMessage(recipient.getAsMention()).embed(success.build()).queue();
        }
    }
}
