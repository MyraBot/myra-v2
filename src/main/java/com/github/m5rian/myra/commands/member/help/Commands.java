package com.github.m5rian.myra.commands.member.help;

import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.CommandEmbeds;
import com.github.m5rian.myra.database.guild.MongoGuild;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Commands implements CommandHandler {
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

    @CommandEvent(
            name = "commands",
            aliases = {"command"},
            emoji = "\uD83D\uDCC3",
            description = "description.help.commands"
    )
    public void execute(CommandContext ctx) throws Exception {
        // Menu
        ctx.getChannel().sendMessage(new CommandEmbeds(ctx).commands().build()).queue(message -> {
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
            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e -> !e.getUser().isBot() // No bot
                            && e.getMessageId().equals(message.getId()) // Same message
                            && e.getUserIdLong() == ctx.getAuthor().getIdLong() // Same author
                            && Arrays.asList(emojis).contains(e.getReactionEmote().getEmoji())) // Match emoji
                    .setAction(e -> {
                        final String prefix = MongoGuild.get(e.getGuild()).getString("prefix"); // Get Prefix
                        final CommandEmbeds embed = new CommandEmbeds(ctx); // Get Embeds
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
                    })
                    .setTimeout(30L, TimeUnit.SECONDS)
                    .setTimeoutAction(() -> message.clearReactions().queue())
                    .load();
        });

    }
}