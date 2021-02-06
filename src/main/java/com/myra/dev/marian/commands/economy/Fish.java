package com.myra.dev.marian.commands.economy;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.database.allMethods.GetMember;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.CommandCooldown;
import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@CommandSubscribe(
        name = "fish"
)
public class Fish implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        //Check for cooldown
        if (!CommandCooldown.getInstance().addCommand(ctx, "fish", 5)) return;
        // Get randomizer
        Random random = new Random();

        final GetMember db = new Database(ctx.getGuild()).getMembers().getMember(ctx.getMember()); // Get Member in database

        // Balance limit would be reached
        if (db.getInteger("balance") + 7 > Config.ECONOMY_MAX) {
            new Error(ctx.getEvent())
                    .setCommand("fish")
                    .setEmoji("\uD83C\uDFA3")
                    .setMessage("We don't want people to get too rich... What about giving other members money? Then try again!")
                    .send();
            return;
        }

        // Caught a fish
        if (random.nextInt(25) <= 20) {
            // Get win message
            final String message = winMessage().get(0);
            // Get price
            final int reward = Integer.parseInt(winMessage().get(1));
            // Send message
            ctx.getChannel().sendMessage(new EmbedBuilder()
                    .setAuthor("fish", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().blue)
                    .setDescription("\uD83C\uDFA3 │ " + message + " **+ " + reward + "**")
                    .build()
            ).queue();
            // Get current balance
            final int balance = db.getBalance();
            // Update balance
            db.setBalance(balance + reward);
        }
        // Didn't catch a fish
        else {
            // Get win message
            final String message = loseMessage().get(0);
            // Get price
            final int lostMoney = Integer.parseInt(loseMessage().get(1));
            // Send message
            ctx.getChannel().sendMessage(new EmbedBuilder()
                    .setAuthor("fish", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().blue)
                    .setDescription("\uD83C\uDFA3 │ " + message + " **- " + lostMoney + "**")
                    .build()
            ).queue();
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
