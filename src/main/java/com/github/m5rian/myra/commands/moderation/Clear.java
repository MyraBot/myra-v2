package com.github.m5rian.myra.commands.moderation;


import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.permissions.Moderator;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Clear implements CommandHandler {

    @CommandEvent(
            name = "clear",
            aliases = {"purge", "delete"},
            requires = Moderator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArgumentsRaw().equalsIgnoreCase("queue")) return; // "Clear queue" is meant

        // Command usage
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("clear")
                    .addUsages(new Usage()
                            .setUsage("clear <amount>")
                            .setEmoji("\uD83D\uDDD1")
                            .setDescription(lang(ctx).get("description.mod.clear")))
                    .send();
            return;
        }

        // Amount isn't a number
        if (!ctx.getArguments()[0].matches("\\d+")) {
            new Error(ctx.getEvent())
                    .setCommand("clear")
                    .setEmoji("\uD83D\uDDD1")
                    .setMessage(lang(ctx).get("error.invalid"))
                    .send();
        }

        final int amount = Integer.parseInt(ctx.getArguments()[0]); // Get amount of messages to delete
        // Delete messages
        ctx.getChannel().getIterableHistory()
                .takeAsync(amount + 1)
                .thenAccept(messages -> ctx.getChannel().purgeMessages(messages));

        // Success information
        new Success(ctx.getEvent())
                .setCommand("clear")
                .setEmoji("\uD83D\uDDD1")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setMessage(lang(ctx).get("command.mod.clear.info.success")
                        .replace("{$amount}", String.valueOf(amount))) // Messages amount which got deleted
                .delete()
                .send();
    }
}
