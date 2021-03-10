package com.myra.dev.marian.commands.administrator.notifications;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.myra.dev.marian.database.managers.NotificationsTwitchManager;
import com.myra.dev.marian.database.managers.NotificationsYoutubeManager;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.utilities.APIs.youTube.YouTube;
import com.myra.dev.marian.utilities.MessageReaction;
import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.List;

@CommandSubscribe(
        name = "notifications list",
        aliases = {"notification list"},
        requires = Administrator.class,
        channel = Channel.GUILD
)
public class NotificationsList implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Create embed
        EmbedBuilder streamers = new EmbedBuilder()
                .setAuthor("notifications list", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue);

        // Get streamers
        List<String> streamersList = new NotificationsTwitchManager().getStreamers(ctx.getGuild());
        //if there are no streamers
        if (streamersList.isEmpty()) {
            streamers.addField("\uD83D\uDD14 │ Streamers:", "No streamers have been set up yet", false);
            ctx.getChannel().sendMessage(streamers.build()).queue();
            return;
        }
        String streamersString = "";
        //streamer names
        for (String streamer : streamersList) {
            streamersString += "• " + streamer + "\n";
        }
        streamers.addField("\uD83D\uDD14 │ Streamers:", streamersString, false);
        final Message message = ctx.getChannel().sendMessage(streamers.build()).complete(); // Send message
        message.addReaction("\uD83D\uDCE1").queue(); // Twitch reaction
        message.addReaction("\uD83D\uDCFA").queue(); // Youtube reaction

        MessageReaction.add(ctx.getGuild(), "notification list", message, ctx.getAuthor(),true, "\uD83D\uDCE1", "\uD83D\uDCFA");
    }

    public void switchList(GuildMessageReactionAddEvent event) throws Exception {
        if (!MessageReaction.check(event, "notification list", false)) return;

        // Twitch
        if (event.getReactionEmote().getEmoji().equals("\uD83D\uDCE1")) {
            // Create embed
            EmbedBuilder list = new EmbedBuilder()
                    .setAuthor("notifications list", null, event.getUser().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().blue);

// Get streamers
            // No streamer has been set up yet
            if (NotificationsTwitchManager.getInstance().getStreamers(event.getGuild()).isEmpty()) {
                list.addField("\uD83D\uDD14 │ Streamers:", "No streamers have been set up yet", false);
            }
            // Streamers have been set up
            else {
                String streamers = "";
                for (String streamer : NotificationsTwitchManager.getInstance().getStreamers(event.getGuild())) {
                    streamers += "• " + streamer + "\n";
                }
                list.addField("\uD83D\uDD14 │ Streamers:", streamers, false);
            }
            event.retrieveMessage().complete().editMessage(list.build()).queue(); // Edit message
        }

        // Youtube
        else if (event.getReactionEmote().getEmoji().equals("\uD83D\uDCFA")) {
            // Create embed
            EmbedBuilder list = new EmbedBuilder()
                    .setAuthor("notifications list", null, event.getUser().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().blue);

// Get youtubers
            // No streamer has been set up yet
            if (NotificationsYoutubeManager.getInstance().getYoutubers(event.getGuild()).isEmpty()) {
                list.addField("\\\uD83D\uDCFA │ YouTubers:", "No youtubers have been set up yet", false);
            }
            // Youtubers have been set up
            else {
                String youtubers = "";
                for (String youtuberId : NotificationsYoutubeManager.getInstance().getYoutubers(event.getGuild())) {
                    final String channelName = YouTube.getApi().getChannel(youtuberId).getChannelName(); // Get youtube channel name
                    youtubers += "• " + channelName + "\n";
                }
                list.addField("\\\uD83D\uDCFA │ YouTubers:", youtubers, false);
            }
            event.retrieveMessage().complete().editMessage(list.build()).queue(); // Edit message
        }
        event.getReaction().removeReaction(event.getUser()).queue(); // Remove reaction
    }
}
