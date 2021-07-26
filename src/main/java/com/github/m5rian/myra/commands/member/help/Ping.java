package com.github.m5rian.myra.commands.member.help;

import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.language.Lang;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class Ping implements CommandHandler {

    @CommandEvent(
            name = "ping",
            aliases = {"latency"},
            emoji = "\uD83C\uDFD3",
            description = "description.help.ping"
    )
    public void execute(CommandContext ctx) throws Exception {
        ctx.getBot().getRestPing().queue(ping -> {
            info(ctx).addField(new MessageEmbed.Field(
                    "\uD83D\uDD29 │ " + Lang.lang(ctx).get("command.help.ping.ping"),
                    Lang.lang(ctx).get("command.help.ping.ping.value").replace("{$latency}", Long.toString(ping)),
                    false))
                    .send();
        });
    }
}
