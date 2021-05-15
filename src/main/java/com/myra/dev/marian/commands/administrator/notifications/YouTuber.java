package com.myra.dev.marian.commands.administrator.notifications;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.managers.NotificationsYoutubeManager;
import com.myra.dev.marian.utilities.APIs.youtube.Youtube;
import com.myra.dev.marian.utilities.APIs.youtube.data.YoutubeChannel;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.EmbedBuilder;

public class YouTuber implements CommandHandler {

    @CommandEvent(
            name = "notification youtube",
            aliases = {"notifications youtube", "notification youtuber", "notifications youtuber"},
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception{
        // Usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("notification youtube")
                    .addUsages(new Usage()
                            .setUsage("notifications youtube <youtube channel>")
                            .setEmoji("\\\uD83D\uDCFA")
                            .setDescription("Add and remove auto notifications for YouTube"))
                    .send();
            return;
        }

        final String query = Utilities.getUtils().getString(ctx.getArguments()); // Get the arguments as one string
        YoutubeChannel channel;
        // Get channel by url
        if (query.matches(Utilities.URL_PATTERN))
            channel = Youtube.getChannel(query.split("/")[4]); // Get channel information
            // Search channel by query
        else channel = Youtube.searchChannel(query).get(0); // Get channel information

        // Remove youtuber
        if (NotificationsYoutubeManager.getInstance().getYoutubers(ctx.getGuild()).contains(channel.getId())) {
            NotificationsYoutubeManager.getInstance().removeYoutuber(channel.getId(), ctx.getGuild()); // Remove youtuber from notifications list
            Youtube.unsubscribe(channel.getId()); // Unsubscribe from that channel

            EmbedBuilder success = new EmbedBuilder()
                    .setAuthor("notification youtube", "https://www.youtube.com/channel/" + channel.getId(), ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().blue)
                    .setDescription("Removed **" + channel.getName() + "** from the notifications");
            ctx.getChannel().sendMessage(success.build()).queue(); // Send success message
        } else {
            NotificationsYoutubeManager.getInstance().addYoutuber(channel.getId(), ctx.getGuild()); // Add youtuber to notifications list
            Youtube.unsubscribe(channel.getId()); // Subscribe to the channel

            EmbedBuilder success = new EmbedBuilder()
                    .setAuthor("notification youtube", "https://www.youtube.com/channel/" + channel.getId(), ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().blue)
                    .setDescription("Added **" + channel.getName() + "** to the notifications");
            ctx.getChannel().sendMessage(success.build()).queue(); // Send success message
        }
    }
}
