package com.myra.dev.marian.listeners.welcome.WelcomeImage;

import com.myra.dev.marian.Myra;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.concurrent.TimeUnit;

public class WelcomeImageFont implements CommandHandler {
    private final String[] emojis = {
            "1\uFE0F\u20E3", // 1️⃣
            "2\uFE0F\u20E3", // 2️⃣
            "3\uFE0F\u20E3" // 3️⃣
    };


@CommandEvent(
        name = "welcome image font",
        requires = Administrator.class
)
    public void execute(CommandContext ctx) throws Exception {
        Utilities utilities = Utilities.getUtils(); // Get utilities
        //change font
        EmbedBuilder fontSelection = new EmbedBuilder()
                .setAuthor("welcome image font", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(utilities.blue)
                .addField("fonts",
                        "1\uFE0F\u20E3 default" +
                                "\n2\uFE0F\u20E3 modern" +
                                "\n3\uFE0F\u20E3 handwritten",
                        false);
        ctx.getChannel().sendMessage(fontSelection.build()).queue(message -> { // Send message
            //add reactions
            message.addReaction(emojis[0]).queue();
            message.addReaction(emojis[1]).queue();
            message.addReaction(emojis[2]).queue();

            // Event waiter
            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e ->  !e.getUser().isBot()
                            && e.getUser() == ctx.getAuthor()
                            && e.getMessageId().equals(message.getId()))
                    .setAction(e -> {
                        final MongoGuild db = new MongoGuild(e.getGuild()); // Get database
                        final String reaction = e.getReactionEmote().getEmoji(); // Get reacted emoji

                        Success success = new Success(ctx.getEvent())
                                .setCommand("welcome image font")
                                .setEmoji("\uD83D\uDDDB")
                                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl());
                        // Fonts
                        if (reaction.equals(emojis[0])) {
                            db.getNested("welcome").setString("welcomeImageFont", "default"); // Update database
                            success.setMessage("You have changed the font to `default`").send();
                        }
                        if (reaction.equals(emojis[1])) {
                            db.getNested("welcome").setString("welcomeImageFont", "modern"); // Update database
                            success.setMessage("You have changed the font to `modern`").send();
                        }
                        if (reaction.equals(emojis[2])) {
                            db.getNested("welcome").setString("welcomeImageFont", "handwritten"); // Update database
                            success.setMessage("You have changed the font to `handwritten`").send();
                        }
                    })
                    .setTimeout(30L, TimeUnit.SECONDS)
                    .setTimeoutAction(() -> {
                        message.clearReactions().queue(); // Clear reactions
                        // Send timeout error
                        new Error(ctx.getEvent())
                                .setCommand("welcome image font")
                                .setEmoji("\uD83D\uDDDB")
                                .setMessage("You took too long to react")
                                .send();
                    })
                    .load();
        });
    }
}
