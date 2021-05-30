package com.github.m5rian.myra.commands.administrator.notifications;


import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import static com.github.m5rian.myra.utilities.language.Lang.*;
import com.github.m5rian.myra.utilities.permissions.Administrator;

public class NotificationsHelp implements CommandHandler {

    @CommandEvent(
            name = "notifications",
            aliases = {"notification"},
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;

        // Send command usages
        new CommandUsage(ctx.getEvent())
                .setCommand("notifications")
                .addUsages(
                        new Usage()
                                .setUsage("notifications twitch <streamer>")
                                .setDescription(lang(ctx).get("description.notificationsTwitch"))
                                .setEmoji("\uD83D\uDCE1"),
                        new Usage()
                                .setUsage("notifications youtube <youtube channel>")
                                .setDescription(lang(ctx).get("description.notificationsYoutube"))
                                .setEmoji("\uD83D\uDCFA"),
                        new Usage()
                                .setUsage("notifications list")
                                .setDescription(lang(ctx).get("description.notificationsList"))
                                .setEmoji("\uD83D\uDD14"),
                        new Usage()
                                .setUsage("notifications channel <channel>")
                                .setDescription(lang(ctx).get("description.notificationsChannel"))
                                .setEmoji("\uD83D\uDCC1"),
                        new Usage()
                                .setUsage("notifications message")
                                .setDescription(lang(ctx).get("description.notificationsMessage"))
                                .setEmoji("\uD83D\uDCEF"))
                .send();
    }
}
