package com.github.m5rian.myra.commands.administrator.notifications;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import static com.github.m5rian.myra.utilities.language.Lang.*;
import com.github.m5rian.myra.utilities.permissions.Administrator;

public class NotificationsMessageTwitch implements CommandHandler {

    @CommandEvent(
            name = "notifications message twitch",
            aliases = {"notification message twitch"},
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("notifications message twitch")
                    .addUsages(new Usage()
                            .setUsage("notifications message twitch <message>")
                            .setDescription(lang(ctx).get("description.notificationsMessage.twitch"))
                            .setEmoji("\uD83D\uDCE1"))
                    .addInformation(lang(ctx).get("info.variables") + "\n" +
                            "\n{streamer} - " + lang(ctx).get("command.notifications.messageTwitch.variable.streamer") +
                            "\n{title} - " + lang(ctx).get("command.notifications.messageTwitch.variable.title") +
                            "\n{game} - " + lang(ctx).get("command.notifications.messageTwitch.variable.game"))
                    .send();
            return;
        }

        // Update database
        MongoGuild.get(ctx.getGuild()).getNested("notifications").setString("twitchMessage", ctx.getArgumentsRaw());
        // Set success message
        new Success(ctx.getEvent())
                .setCommand("notifications message twitch")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setEmoji("\uD83D\uDCE1")
                .setMessage(lang(ctx).get("command.notifications.messageTwitch.success")
                        .replace("{$message}", ctx.getArgumentsRaw())) // New notification message
                .send();
    }
}
