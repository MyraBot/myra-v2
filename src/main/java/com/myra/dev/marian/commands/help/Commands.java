package com.myra.dev.marian.commands.help;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.Myra;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.CommandEmbeds;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@CommandSubscribe(
        name = "commands",
        aliases = {"command"}
)
public class Commands implements Command {
    private final String[] emojis = new String[]{
            "\uD83D\uDCD6", // Help
            "\uD83C\uDF88", // General
            "\uD83D\uDD79", // Fun
            "\uD83C\uDFC6", // Leveling
            "\uD83D\uDCB0", // Economy
            "\uD83D\uDCFB", // Music
            "\uD83D\uDD28", // Moderation
            "\uD83D\uDD29" // Administrator
    };

    @Override
    public void execute(CommandContext ctx) throws Exception {
        //menu
        ctx.getChannel().sendMessage(new CommandEmbeds(ctx.getGuild(), ctx.getEvent().getJDA(), ctx.getAuthor(), ctx.getPrefix()).commands().build()).queue(message -> {
            // Add reactions
            message.addReaction(emojis[0]).queue(); // Help
            message.addReaction(emojis[1]).queue(); // General
            message.addReaction(emojis[2]).queue(); // Fun
            message.addReaction(emojis[3]).queue(); // Leveling
            message.addReaction(emojis[4]).queue(); // Economy
            message.addReaction(emojis[5]).queue(); // Music
            message.addReaction(emojis[6]).queue(); // Moderation
            message.addReaction(emojis[7]).queue(); // Administrator
            // Event waiter
            Myra.WAITER.waitForEvent(
                    MessageReactionAddEvent.class, // Event to wait
                    e -> // Requirements
                            !e.getUser().isBot() // No bot
                                    && e.getMessageId().equals(message.getId()) // Same message
                                    && e.getUserIdLong() == ctx.getAuthor().getIdLong() // Same author
                                    && Arrays.stream(emojis).anyMatch(e.getReactionEmote().getEmoji()::equals), // matching emoji

                    e -> { // on event
                        final String prefix = new MongoGuild(e.getGuild()).getString("prefix"); // Get Prefix
                        final CommandEmbeds embed = new CommandEmbeds(e.getGuild(), e.getJDA(), e.getUser(), prefix); // Get Embeds
                        final String reaction = e.getReactionEmote().getEmoji(); // Get reacted emoji

                        // Help commands
                        if (reaction.equals(emojis[0])) message.editMessage(embed.help().build()).queue();
                        // General commands
                        if (reaction.equals(emojis[1])) message.editMessage(embed.general().build()).queue();
                        // Fun commands
                        if (reaction.equals(emojis[2])) message.editMessage(embed.fun().build()).queue();
                        // Leveling commands
                        if (reaction.equals(emojis[3])) message.editMessage(embed.leveling().build()).queue();
                        // Economy commands
                        if (reaction.equals(emojis[4])) message.editMessage(embed.economy().build()).queue();
                        // Music commands
                        if (reaction.equals(emojis[5])) message.editMessage(embed.music().build()).queue();
                        // Moderation commands
                        if (reaction.equals(emojis[6])) message.editMessage(embed.moderation().build()).queue();
                        // Administrator commands
                        if (reaction.equals(emojis[7])) message.editMessage(embed.administrator().build()).queue();

                        message.clearReactions().queue(); // Clear reactions
                    },
                    30L, TimeUnit.SECONDS, // Timeout
                    () -> message.clearReactions().queue()
            );
        });
    }
}