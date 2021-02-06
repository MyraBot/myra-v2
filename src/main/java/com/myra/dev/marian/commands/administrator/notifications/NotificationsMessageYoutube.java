package com.myra.dev.marian.commands.administrator.notifications;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.myra.dev.marian.database.allMethods.Database;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.permissions.Administrator;

@CommandSubscribe(
        name = "notifications message youtube",
        aliases = {"notification message youtube"},
        requires = Administrator.class,
        channel = Channel.GUILD
)
public class NotificationsMessageYoutube implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("notifications message youtube")
                    .addUsages(
                            new Usage()
                                    .setUsage("notifications message twitch <message>")
                                    .setDescription("Add a notification message, which is send once your streamer is live")
                                    .setEmoji("\\\uD83D\uDCFA"))
                    .addInformation(String.format("Use variables to customize your message%n" +
                            "%n{youtuber} - Name of youtuber" +
                            "%n{title} - Name of the video"))
                    .send();
            return;
        }

        // Update database
        new Database(ctx.getGuild()).getNested("notifications").setString("youtubeMessage", ctx.getArgumentsRaw());
        // Set success message
        new Success(ctx.getEvent())
                .setCommand("notifications message youtube")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setEmoji("\\\uD83D\uDCFA")
                .setMessage(String.format("You changed the twitch notifications message to:%n%s", ctx.getArgumentsRaw()))
                .send();
    }
}
