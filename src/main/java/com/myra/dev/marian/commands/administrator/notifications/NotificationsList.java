package com.myra.dev.marian.commands.administrator.notifications;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.Myra;
import com.myra.dev.marian.database.managers.NotificationsTwitchManager;
import com.myra.dev.marian.database.managers.NotificationsYoutubeManager;
import com.myra.dev.marian.utilities.APIs.youTube.YouTube;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandSubscribe(
        name = "notifications list",
        aliases = {"notification list"},
        requires = Administrator.class,
        channel = Channel.GUILD
)
public class NotificationsList implements Command {
    private final String[] emojis = {
            "\uD83D\uDCE1", // Twitch
            "\uD83D\uDCFA" // YouTube
    };

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
        ctx.getChannel().sendMessage(streamers.build()).queue(message -> { // Send message
            message.addReaction("\uD83D\uDCE1").queue(); // Twitch reaction
            message.addReaction("\uD83D\uDCFA").queue(); // Youtube reaction

            switchList(ctx.getEvent(), message); // Reactions
        });
    }

    public void switchList(MessageReceivedEvent messageEvent, Message message) {
        Myra.WAITER.waitForEvent(
                GuildMessageReactionAddEvent.class, // Event to wait for
                e -> !e.getUser().isBot()
                        && e.getUser().getIdLong() == messageEvent.getAuthor().getIdLong()
                        && Arrays.asList(emojis).contains(e.getReactionEmote().getEmoji()),
                e -> { // Fires on event
                    // Create embed
                    EmbedBuilder list = new EmbedBuilder()
                            .setAuthor("notifications list", null, e.getUser().getEffectiveAvatarUrl())
                            .setColor(Utilities.getUtils().blue);

                    // Twitch
                    if (e.getReactionEmote().getEmoji().equals("\uD83D\uDCE1")) {
                        // No streamer has been set up yet
                        if (NotificationsTwitchManager.getInstance().getStreamers(e.getGuild()).isEmpty()) {
                            list.addField("\uD83D\uDD14 │ Streamers:", "No streamers have been set up yet", false);
                        }
                        // Streamers have been set up
                        else {
                            String streamers = "";
                            for (String streamer : NotificationsTwitchManager.getInstance().getStreamers(e.getGuild())) {
                                streamers += "• " + streamer + "\n";
                            }
                            list.addField("\uD83D\uDD14 │ Streamers:", streamers, false);
                        }

                        message.editMessage(list.build()).queue(); // Edit message
                    }

                    // Youtube
                    else if (e.getReactionEmote().getEmoji().equals("\uD83D\uDCFA")) {
                        // No streamer has been set up yet
                        if (NotificationsYoutubeManager.getInstance().getYoutubers(e.getGuild()).isEmpty()) {
                            list.addField("\\\uD83D\uDCFA │ YouTubers:", "No youtubers have been set up yet", false);
                        }
                        // Youtubers have been set up
                        else {
                            String youtubers = "";
                            for (String youtuberId : NotificationsYoutubeManager.getInstance().getYoutubers(e.getGuild())) {
                                final String channelName = YouTube.getApi().getChannel(youtuberId).getChannelName(); // Get youtube channel name
                                youtubers += "• " + channelName + "\n";
                            }
                            list.addField("\\\uD83D\uDCFA │ YouTubers:", youtubers, false);
                        }

                        message.editMessage(list.build()).queue(); // Edit message
                    }

                    e.getReaction().removeReaction(e.getUser()).queue(); // Remove reaction
                    switchList(messageEvent, message);
                },
                30L, TimeUnit.SECONDS, // Timeout
                () -> message.clearReactions().queue()
        );
    }

}
