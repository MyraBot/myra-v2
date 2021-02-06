package com.myra.dev.marian.commands.administrator.notifications;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.permissions.Administrator;

@CommandSubscribe(
        name = "notifications message",
        aliases = {"notification message"},
        requires = Administrator.class,
        channel = Channel.GUILD
)
public class NotificationsMessageHelp implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length == 0) {
            CommandUsage usage = new CommandUsage(ctx.getEvent())
                    .setCommand("notifications message")
                    .addUsages(
                            new Usage()
                                    .setUsage("notifications message twitch <message>")
                                    .setDescription("Add a notification message, which is send once your streamer is live")
                                    .setEmoji("\uD83D\uDCEF"),
                            new Usage()
                                    .setUsage("notifications message youtube <message>")
                                    .setDescription("Add a notification message, which is send once a youtuber uploaded a video")
                                    .setEmoji("\\\uD83D\uDCFA"));
            usage.send();
        }
    }
}
