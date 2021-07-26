package com.github.m5rian.myra.commands.administrator.notifications;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.managers.NotificationsYoutubeManager;
import com.github.m5rian.myra.utilities.APIs.youtube.deprecated.Youtube;
import com.github.m5rian.myra.utilities.APIs.youtube.deprecated.data.YoutubeChannel;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.permissions.Administrator;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class YouTuber implements CommandHandler {

    @CommandEvent(
            name = "notification youtube",
            aliases = {"notifications youtube", "notification youtuber", "notifications youtuber"},
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("notification youtube")
                    .addUsages(new Usage()
                            .setUsage("notifications youtube <youtube channel>")
                            .setEmoji("\\\uD83D\uDCFA")
                            .setDescription(Lang.lang(ctx).get("description.notificationsYoutube")))
                    .send();
            return;
        }

        final String query = Utilities.getString(ctx.getArguments()); // Get the arguments as one string
        YoutubeChannel channel;
        // Get channel by url
        if (query.matches(Utilities.URL_PATTERN))
            channel = Youtube.getChannels(query.split("/")[4]).get(0); // Get channel information
            // Search channel by query
        else channel = Youtube.searchChannel(query).get(0); // Get channel information

        // Remove youtuber
        if (NotificationsYoutubeManager.getInstance().getYoutubers(ctx.getGuild()).contains(channel.getId())) {
            NotificationsYoutubeManager.getInstance().removeYoutuber(channel.getId(), ctx.getGuild()); // Remove youtuber from notifications list
            Youtube.unsubscribe(channel.getId()); // Unsubscribe from that channel

            new Success(ctx.getEvent())
                    .setCommand("notifications youtube")
                    .setHyperLink("https://www.youtube.com/channel/" + channel.getId())
                    .setMessage(Lang.lang(ctx).get("command.notifications.youtube.info.removed")
                            .replace("{$channel}", channel.getName()))
                    .send();
        }
        // Add youtuber
        else {
            // Youtuber limit reached
            if (NotificationsYoutubeManager.getInstance().getYoutubers(ctx.getGuild()).size() >= 100) {
                error(ctx).setDescription(lang(ctx).get("command.notifications.youtube.error.limit"))
                        .send();
                return;
            }

            NotificationsYoutubeManager.getInstance().addYoutuber(channel.getId(), ctx.getGuild()); // Add youtuber to notifications list
            Youtube.unsubscribe(channel.getId()); // Subscribe to the channel

            new Success(ctx.getEvent())
                    .setCommand("notifications youtube")
                    .setHyperLink("https://www.youtube.com/channel/" + channel.getId())
                    .setMessage(Lang.lang(ctx).get("command.notifications.youtube.info.added")
                            .replace("{$channel}", channel.getName()))
                    .send();
        }
    }
}
