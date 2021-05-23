package com.myra.dev.marian.commands.administrator.notifications;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import static com.myra.dev.marian.utilities.language.Lang.*;
import com.myra.dev.marian.utilities.permissions.Administrator;

public class NotificationsMessageYoutube implements CommandHandler {

    @CommandEvent(
            name = "notifications message youtube",
            aliases = {"notification message youtube"},
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("notifications message youtube")
                    .addUsages(new Usage()
                            .setUsage("notifications message youtube <message>")
                            .setDescription(lang(ctx).get("description.notificationsMessage.youtube"))
                            .setEmoji("\\\uD83D\uDCFA"))
                    .addInformation(lang(ctx).get("info.variables") + "\n" +
                            "\n{streamer} - " + lang(ctx).get("command.notifications.messageYoutube.variable.youtuber") +
                            "\n{game} - " + lang(ctx).get("command.notifications.messageYoutube.variable.title"))
                    .send();
            return;
        }

        // Update database
        new MongoGuild(ctx.getGuild()).getNested("notifications").setString("youtubeMessage", ctx.getArgumentsRaw());
        // Set success message
        new Success(ctx.getEvent())
                .setCommand("notifications message youtube")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setEmoji("\\\uD83D\uDCFA")
                .setMessage(lang(ctx).get("command.notifications.messageYoutube.success")
                        .replace("{$message}", ctx.getArgumentsRaw())) // New notification message
                .send();
    }
}
