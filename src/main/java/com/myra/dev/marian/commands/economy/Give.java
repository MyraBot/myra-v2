package com.myra.dev.marian.commands.economy;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.database.guild.member.GuildMember;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.Config;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
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
        // Get utilities
        final Utilities utilities = Utilities.getUtils();
        // Usage
        if (ctx.getArguments().length != 2) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("give", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "give <user> <balance>`", "\uD83D\uDCB8 │ Give credits to other users", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
        // Get user
        final Member member = utilities.getMember(ctx.getEvent(), ctx.getArguments()[0], "give", "\uD83D\uDCB8");
        if (member == null) return;
        // Errors
        // Amount of money aren't digits
        if (!ctx.getArguments()[1].matches("\\d+")) {
            new Error(ctx.getEvent())
                    .setCommand("give")
                    .setEmoji("\uD83D\uDCB8")
                    .setMessage("Invalid number")
                    .send();
            return;
        }

        final GuildMember dbMember = new MongoGuild(ctx.getGuild()).getMembers().getMember(member); // Get member in database
        final GuildMember dbAuthor = new MongoGuild(ctx.getGuild()).getMembers().getMember(ctx.getMember()); // Get author in database
        final int amount = Integer.parseInt(ctx.getArguments()[1]); // Money to transfer

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
        if (dbMember.getBalance() + amount > Config.ECONOMY_MAX) {
            new Error(ctx.getEvent())
                    .setMessage("give")
                    .setEmoji("\uD83D\uDCB8")
                    .setMessage("What are you doing? The member you want to give money is already rich")
                    .send();
            return;
        }
        // User is bot
        if (member.getUser().isBot()) {
            new Error(ctx.getEvent())
                    .setMessage("give")
                    .setEmoji("\uD83D\uDCB8")
                    .setMessage("I wouldn't do that, bots can cheat infinite money... So why giving them even more?")
                    .send();
            return;
        }
        // Transfer money
        dbMember.setBalance(dbMember.getBalance() + amount); // Add money to given member
        dbAuthor.setBalance(dbAuthor.getBalance() - amount); // Remove money of author
        // Success message
        EmbedBuilder success = new EmbedBuilder()
                .setAuthor("give", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(utilities.blue)
                .setDescription(ctx.getAuthor().getAsMention() + " gave you `" + utilities.formatNumber(Integer.parseInt(ctx.getArguments()[1])) + "` " + new MongoGuild(ctx.getGuild()).getNested("economy").getString("currency"));
        ctx.getChannel().sendMessage(member.getAsMention()).queue();
        ctx.getChannel().sendMessage(success.build()).queue();
    }
}
