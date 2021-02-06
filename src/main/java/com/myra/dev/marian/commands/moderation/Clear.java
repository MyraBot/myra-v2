package com.myra.dev.marian.commands.moderation;


import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Moderator;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "clear",
        aliases = {"purge", "delete"},
        requires = Moderator.class
)
public class Clear implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        Utilities utilities = Utilities.getUtils(); // Get utilities
        // Command usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("clear", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "clear <amount>`", "\uD83D\uDDD1 │ clear", true);
            ctx.getChannel().sendMessage(embed.build()).queue();
            return;
        }
// Clear messages
        // If amount isn't a number
        if (!ctx.getArguments()[0].matches("\\d+")) {
            //TODO ERROR
        }
        // Delete messages
        try {
            // Retrieve messages
            ctx.getChannel().getHistory().retrievePast(Integer.parseInt(ctx.getArguments()[0]) + 1).queue(messages -> {
                messages.forEach(message -> ctx.getChannel().deleteMessageById(message.getIdLong()).queue()); // Delete messages
            });
            // Success information
            Success success = new Success(ctx.getEvent())
                    .setCommand("clear")
                    .setEmoji("\uD83D\uDDD1")
                    .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                    .setMessage("`" + ctx.getArguments()[0] + "` messages have been deleted")
                    .delete();
            success.send();
        }
        // Errors
        catch (Exception exception) {
            //to many messages
            if (ctx.getArguments()[0].equals("0") || exception.toString().startsWith("java.lang.IllegalArgumentException: Message retrieval")) {
                new Error(ctx.getEvent())
                        .setCommand("clear")
                        .setEmoji("\uD83D\uDDD1")
                        .setMessage("Invalid amount of messages")
                        .setFooter("You can only delete an amount between 1 and 100 messages")
                        .send();
            }
            //message too late
            else {
                new Error(ctx.getEvent())
                        .setCommand("clear")
                        .setEmoji("\uD83D\uDDD1")
                        .setMessage("You selected too old messages")
                        .setFooter("I can't delete messages older than 2 weeks")
                        .send();
            }
        }
    }
}
