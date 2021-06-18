package com.github.m5rian.myra.commands.member.economy;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.database.guild.member.GuildMember;
import com.github.m5rian.myra.utilities.CommandCooldown;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.language.Lang;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Fish implements CommandHandler {
    private final int maxCatch = 25;

    @CommandEvent(
            name = "fish",
            emoji = "\uD83C\uDFA3",
            description = "description.economy.fish",
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return; // Check for no arguments

        if (!CommandCooldown.getInstance().addCommand(ctx, "fish", 5)) return; //Check for cooldown
        final GuildMember db = GuildMember.get(ctx.getMember()); // Get Member in database
        // Balance limit would be reached
        if (db.getBalance() + maxCatch > Config.ECONOMY_MAX) {
            new Error(ctx.getEvent())
                    .setCommand("fish")
                    .setEmoji("\uD83C\uDFA3")
                    .setMessage(Lang.lang(ctx).get("command.economy.fish.balanceLimit"))
                    .send();
            return;
        }

        // Caught a fish
        if (new Random().nextInt(25) <= 20) {
            final String message = winMessage().get(0); // Get win message
            final int reward = Integer.parseInt(winMessage().get(1)); // Get price
            // Send message
            ctx.getChannel().sendMessage(new EmbedBuilder()
                    .setAuthor("fish", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.blue)
                    .setDescription("\uD83C\uDFA3 │ " + message + " **+ " + reward + "**")
                    .build())
                    .queue();
            db.setBalance(db.getBalance() + reward); // Update balance
        }
        // Didn't catch a fish
        else {
            final String message = loseMessage().get(0); // Get loose message
            final int lostMoney = Integer.parseInt(loseMessage().get(1)); // Get price
            // Send message
            ctx.getChannel().sendMessage(new EmbedBuilder()
                    .setAuthor("fish", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.blue)
                    .setDescription("\uD83C\uDFA3 │ " + message + " **- " + lostMoney + "**")
                    .build())
                    .queue();
            // Get current balance
            final int balance = db.getBalance();
            // Update balance
            db.setBalance(balance - lostMoney);
        }
    }


    private List<String> winMessage() {
        // Set messages                         🐟                           🐠                            🐡                           🐚                           🦀                             🦞                          🦐
        String[] messages = {"You caught a \uD83D\uDC1F", "You caught a \uD83D\uDC20", "You caught a \uD83D\uDC21", "You caught a \uD83D\uDC1A", "You caught a \uD83E\uDD80", "You caught a \uD83E\uDD9E", "You caught a \uD83E\uDD90"};
        Integer[] rewards = {5, 7, 4, 2, 4, 5, 6};
        // Get random number
        Integer random = new Random().nextInt(messages.length);
        // Return the message with the reward
        return Arrays.asList(messages[random], rewards[random].toString());
    }

    private List<String> loseMessage() {
        // Set messages                                             🐋                🐬                                                                                                  🦈                🐙
        String[] messages = {"Your line broke while catching a \uD83D\uDC0B", "A \uD83D\uDC2C played around with the line and you were thrown into the water", "You got bitten by a \uD83E\uDD88", "A \uD83D\uDC19 splashed your eyes and you can no longer see anything"};
        Integer[] rewards = {15, 10, 5, 20};
        // Get random number
        Integer random = new Random().nextInt(messages.length);
        // Return the message with the reward
        return Arrays.asList(messages[random], rewards[random].toString());
    }
}
