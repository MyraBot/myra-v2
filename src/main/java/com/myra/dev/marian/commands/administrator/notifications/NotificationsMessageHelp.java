package com.myra.dev.marian.commands.administrator.notifications;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import static com.myra.dev.marian.utilities.language.Lang.*;
import com.myra.dev.marian.utilities.permissions.Administrator;

public class NotificationsMessageHelp implements CommandHandler {

@CommandEvent(
        name = "notifications message",
        aliases = {"notification message"},
        requires = Administrator.class,
        channel = Channel.GUILD
)
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length == 0) {
            CommandUsage usage = new CommandUsage(ctx.getEvent())
                    .setCommand("notifications message")
                    .addUsages(
                            new Usage()
                                    .setUsage("notifications message twitch <message>")
                                    .setDescription(lang(ctx).get("description.notificationsMessage.twitch"))
                                    .setEmoji("\uD83D\uDCEF"),
                            new Usage()
                                    .setUsage("notifications message youtube <message>")
                                    .setDescription(lang(ctx).get("description.notificationsMessage.youtube"))
                                    .setEmoji("\\\uD83D\uDCFA"));
            usage.send();
        }
    }
}
