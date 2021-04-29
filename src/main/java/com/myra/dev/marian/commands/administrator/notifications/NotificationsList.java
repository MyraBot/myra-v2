package com.myra.dev.marian.commands.administrator.notifications;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
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

public class NotificationsList implements CommandHandler {
    private final String[] emojis = {
            "\uD83D\uDCE1", // Twitch
            "\uD83D\uDCFA" // YouTube
    };


@CommandEvent(
        name = "notifications list",
        aliases = {"notification list"},
        requires = Administrator.class,
        channel = Channel.GUILD
)
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Create embed
        EmbedBuilder streamersEmbed = new EmbedBuilder()
                .setAuthor("notifications list", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue);

        // Get streamers
        List<String> streamersList = new NotificationsTwitchManager().getStreamers(ctx.getGuild());
        //if there are no streamers
        if (streamersList.isEmpty()) {
            streamersEmbed.addField("\uD83D\uDD14 │ Streamers:", "No streamers have been set up yet", false);
            ctx.getChannel().sendMessage(streamersEmbed.build()).queue();
            return;
        }
        String streamersString = "";
        //streamer names
        for (String streamer : streamersList) {
            streamersString += "• " + streamer + "\n";
        }
    streamersEmbed.addField("\uD83D\uDD14 │ Streamers:", streamersString, false);
        ctx.getChannel().sendMessage(streamersEmbed.build()).queue(message -> { // Send message
            message.addReaction("\uD83D\uDCE1").queue(); // Twitch reaction
            message.addReaction("\uD83D\uDCFA").queue(); // Youtube reaction

            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e -> !e.getUser().isBot()
                            && e.getUser().getIdLong() == ctx.getAuthor().getIdLong()
                            && Arrays.asList(emojis).contains(e.getReactionEmote().getEmoji()))
                    .setAction(e -> {
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

                                StringBuilder youtubers = new StringBuilder();
                                for (String youtuberId : NotificationsYoutubeManager.getInstance().getYoutubers(e.getGuild())) {
                                    final String channelName = YouTube.getApi().getChannel(youtuberId).getChannelName(); // Get youtube channel name
                                    youtubers.append("• ").append(channelName).append("\n"); // Append youtuber
                                }
                                list.addField("\\\uD83D\uDCFA │ YouTubers:", youtubers.toString(), false);

                            }

                            message.editMessage(list.build()).queue(); // Edit message
                        }

                        e.getReaction().removeReaction(e.getUser()).queue(); // Remove reaction
                    })
                    .setTimeout(30L, TimeUnit.SECONDS)
                    .setTimeoutAction(() -> message.clearReactions().queue())
                    .load();
        });
    }

}
