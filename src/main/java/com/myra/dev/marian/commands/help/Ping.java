package com.myra.dev.marian.commands.help;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import net.dv8tion.jda.api.EmbedBuilder;

public class Ping implements CommandHandler {

    @CommandEvent(
            name = "ping",
            aliases = {"latency"}
    )
    public void execute(CommandContext ctx) throws Exception {
        final long time = System.currentTimeMillis();
        EmbedBuilder success = new Success(ctx.getEvent())
                .setCommand("ping")
                .setEmoji("\uD83C\uDFD3")
                .setMessage("**Pong!**")
                .getEmbed();
        ctx.getChannel().sendMessage(success.build()).queue(message -> {
            final long commandPing = time - ctx.getEvent().getMessage().getTimeCreated().toInstant().toEpochMilli();
            final long apiPing = System.currentTimeMillis() - time;

            success
                    .addField("\uD83D\uDD29 │ Ping", "**`" + commandPing + "`**", true)
                    .addField("\uD83D\uDD2D │ Api ping", "**`" + apiPing + "`**", true);
            message.editMessage(success.build()).queue();
        });

    }
}
