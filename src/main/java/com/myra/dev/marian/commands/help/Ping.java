package com.myra.dev.marian.commands.help;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "ping",
        aliases = {"latency"}
)
public class Ping implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        if (!ctx.getAuthor().isBot()) {
            final long time = System.currentTimeMillis();
            EmbedBuilder success = new Success(ctx.getEvent())
                    .setCommand("ping")
                    .setEmoji("\uD83C\uDFD3")
                    .setMessage("Pong!")
                    .getEmbed();
            ctx.getChannel().sendMessage(success.build()).queue(message -> {
                message.editMessage(success.setDescription(String.format("Pong! My ping is `%d ms`", System.currentTimeMillis() - time)).build()).queue();
            });
        }
    }
}
