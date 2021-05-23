package com.myra.dev.marian.commands.help;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import static com.myra.dev.marian.utilities.language.Lang.*;

public class Ping implements CommandHandler {

    @CommandEvent(
            name = "ping",
            aliases = {"latency"}
    )
    public void execute(CommandContext ctx) throws Exception {
        final long time = System.currentTimeMillis();
        final Success ping = new Success(ctx.getEvent())
                .setCommand("ping")
                .setEmoji("\uD83C\uDFD3");

        ctx.getChannel().sendMessage(ping
                .setMessage(lang(ctx).get("command.help.ping.pong"))
                .getEmbed()
                .build()).queue(message -> {
            final long commandPing = time - ctx.getEvent().getMessage().getTimeCreated().toInstant().toEpochMilli();
            final long apiPing = System.currentTimeMillis() - time;
            // Ping
            ping.addInlineField("\uD83D\uDD29 │ " + lang(ctx).get("command.help.ping.ping"),
                    lang(ctx).get("command.help.ping.ping.value")
                            .replace("{$latency}", String.valueOf(commandPing))); // Ping
            // Discord ping
            ping.addInlineField("\uD83D\uDD2D │ " + lang(ctx).get("command.help.ping.apiPing"),
                    lang(ctx).get("command.help.ping.apiPing.value")
                            .replace("{$latency}", String.valueOf(apiPing))); // Api ping

            message.editMessage(ping.getEmbed().build()).queue(); // Edit message
        });

    }
}
