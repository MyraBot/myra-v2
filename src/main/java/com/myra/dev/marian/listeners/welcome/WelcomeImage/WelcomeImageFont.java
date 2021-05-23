package com.myra.dev.marian.listeners.welcome.WelcomeImage;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.concurrent.TimeUnit;

import static com.myra.dev.marian.utilities.language.Lang.lang;

public class WelcomeImageFont implements CommandHandler {
    private final String[] emojis = {
            "1\uFE0F\u20E3", // 1️⃣
            "2\uFE0F\u20E3", // 2️⃣
            "3\uFE0F\u20E3" // 3️⃣
    };

    @CommandEvent(
            name = "welcome image font",
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Font selection
        final Success fontSelection = new Success(ctx.getEvent())
                .setCommand("welcome image font")
                .addField(lang(ctx).get("command.welcome.image.fonts"),
                        "1\uFE0F\u20E3 " + lang(ctx).get("command.welcome.image.fonts.default") + // Default
                                "\n2\uFE0F\u20E3 " + lang(ctx).get("command.welcome.image.fonts.modern") + // Modern
                                "\n3\uFE0F\u20E3 " + lang(ctx).get("command.welcome.image.fonts.handwritten"));// Handwritten
        ctx.getChannel().sendMessage(fontSelection.getEmbed().build()).queue(message -> { // Send message
            // Add reactions
            message.addReaction(emojis[0]).queue();
            message.addReaction(emojis[1]).queue();
            message.addReaction(emojis[2]).queue();

            // Event waiter
            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e -> !e.getUser().isBot()
                            && e.getUserIdLong() == ctx.getAuthor().getIdLong()
                            && e.getMessageIdLong() == message.getIdLong())
                    .setAction(e -> {
                        final MongoGuild db = new MongoGuild(e.getGuild()); // Get database
                        final String reaction = e.getReactionEmote().getEmoji(); // Get reacted emoji

                        String font = "deafult";
                        // Fonts
                        if (reaction.equals(emojis[0])) font = "default";
                        if (reaction.equals(emojis[1])) font = "modern";
                        if (reaction.equals(emojis[2])) font = "handwritten";

                        db.getNested("welcome").setString("welcomeImageFont", font); // Update database
                        new Success(ctx.getEvent())
                                .setCommand("welcome image font")
                                .setEmoji("\uD83D\uDDDB")
                                .setMessage(lang(ctx).get("command.welcome.image.fonts.success")
                                        .replace("{$font}", font))
                                .send();
                    })
                    .setTimeout(30L, TimeUnit.SECONDS)
                    .setTimeoutAction(() -> {
                        message.clearReactions().queue(); // Clear reactions
                        // Send timeout error
                        new Error(ctx.getEvent())
                                .setCommand("welcome image font")
                                .setEmoji("\uD83D\uDDDB")
                                .setMessage(lang(ctx).get("error.timeout"))
                                .send();
                    })
                    .load();
        });
    }
}
