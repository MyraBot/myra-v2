package com.myra.dev.marian.commands.administrator.notifications;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.managers.NotificationsTwitchManager;
import com.myra.dev.marian.database.managers.NotificationsYoutubeManager;
import com.myra.dev.marian.utilities.APIs.youtube.Youtube;
import com.myra.dev.marian.utilities.APIs.youtube.data.YoutubeChannel;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.myra.dev.marian.utilities.language.Lang.lang;

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

        final List<String> streamerList = new NotificationsTwitchManager().getStreamers(ctx.getGuild()); // Get streamers
        final List<String> youtuberIdList = NotificationsYoutubeManager.getInstance().getYoutubers(ctx.getGuild()); // Get youtubers
        final List<YoutubeChannel> youtuberList = new ArrayList(); // Create list for all youtuber objects
        youtuberIdList.forEach(youtuber -> youtuberList.add(Youtube.getChannel(youtuber))); // Add all youtubers


        // Show by default streamers
        EmbedBuilder streamersEmbed = new EmbedBuilder()
                .setAuthor("notifications list", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.blue);
        // There are no streamers
        if (streamerList.isEmpty()) {
            streamersEmbed.addField(
                    "\uD83D\uDD14 │ " + lang(ctx).get("command.notifications.list.streamers") + ":",
                    lang(ctx).get("command.notifications.list.noStreamers"),
                    false);
        }
        // Streamers got set up
        else {
            final StringBuilder streamersString = new StringBuilder(); // String for all streamers
            streamerList.forEach(streamer -> streamersString.append("• ").append(streamer).append("\n")); // Add all streamers
            streamersEmbed.addField(
                    "\uD83D\uDD14 │ " + lang(ctx).get("command.notifications.list.streamers") + ":",
                    streamersString.toString(),
                    false);
        }

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
                                .setColor(Utilities.blue);

                        // Twitch
                        // Edit message
                        if (e.getReactionEmote().getEmoji().equals("\uD83D\uDCE1")) {
                            // No streamer has been set up yet
                            if (NotificationsTwitchManager.getInstance().getStreamers(e.getGuild()).isEmpty()) {
                                list.addField("\uD83D\uDD14 │ " + lang(ctx).get("command.notifications.list.streamers") + ":",
                                        lang(ctx).get("command.notifications.list.noStreamers"),
                                        false);
                            }
                            // Streamers have been set up
                            else {
                                final StringBuilder streamers = new StringBuilder(); // String for all streamers
                                streamerList.forEach(streamer -> streamers.append("• ").append(streamer).append("\n")); // Add all streamers
                                list.addField("\uD83D\uDD14 │ " + lang(ctx).get("command.notifications.list.streamers") + ":",
                                        streamers.toString(),
                                        false);
                            }
                        }
                        // Youtube
                        else {
                            // No youtubers have been set up yet
                            if (youtuberList.isEmpty()) {
                                list.addField("\uD83D\uDD14 │ " + lang(ctx).get("command.notifications.list.youtubers") + ":",
                                        lang(ctx).get("command.notifications.list.noYoutubers"),
                                        false);
                                list.addField("\\\uD83D\uDCFA │ " + lang(ctx).get("command.notifications.list.youtubers") + ":", "", false);
                            }
                            // Youtubers have been set up
                            else {
                                final StringBuilder youtubers = new StringBuilder(); // String for all youtubers
                                youtuberList.forEach(youtuber -> youtubers.append("• ").append(youtuber.getName()).append("\n")); // Add all youtubers
                                list.addField("\\\uD83D\uDCFA │ " + lang(ctx).get("command.notifications.list.youtubers") + ":", youtubers.toString(), false);
                            }
                        }

                        message.editMessage(list.build()).queue(); // Edit message
                        e.getReaction().removeReaction(e.getUser()).queue(); // Remove reaction
                    })
                    .setTimeout(30L, TimeUnit.SECONDS)
                    .setTimeoutAction(() -> message.clearReactions().queue())
                    .load();
        });
    }

}
