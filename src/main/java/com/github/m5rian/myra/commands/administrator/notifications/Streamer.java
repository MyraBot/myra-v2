package com.github.m5rian.myra.commands.administrator.notifications;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.managers.NotificationsTwitchManager;
import com.github.m5rian.myra.utilities.APIs.Twitch;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONObject;

import java.util.List;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Streamer implements CommandHandler {

    @CommandEvent(
            name = "notification twitch",
            aliases = {"notification live", "notifications twitch", "notifications live"},
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Usage
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("notifications twitch")
                    .addUsages(new Usage()
                            .setUsage("notification twitch <streamer>")
                            .setEmoji("\uD83D\uDCE1")
                            .setDescription(lang(ctx).get("description.notificationsTwitch")))
                    .send();
            return;
        }

        // Add or remove streamer
        final JSONObject channelInformation = new Twitch().getChannel(ctx.getArguments()[0]); // Get channel information
        if (channelInformation == null) {
            new Error(ctx.getEvent())
                    .setCommand("notifications twitch")
                    .setEmoji("\uD83D\uDCE1")
                    .setMessage(lang(ctx).get("command.notifications.twitch.notFound")
                            .replace("{$url}", "www.twitch.tv/**__m5rian__**")) // Replace url
                    .send();
            return;
        }
        // Create embed
        EmbedBuilder success = new EmbedBuilder()
                .setAuthor("notifications twitch", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.blue);

        final String channelName = channelInformation.getString("user"); // Get channel name
        final List<String> streamers = NotificationsTwitchManager.getInstance().getStreamers(ctx.getGuild()); // Get current streamers

        // Streamer is already subscribed
        if (streamers.contains(channelName)) {
            NotificationsTwitchManager.getInstance().removeStreamer(ctx.getGuild(), channelName); // Remove streamer from the database
            // Complete embed
            success.addField("\uD83D\uDD15 │ " + lang(ctx).get("command.notifications.twitch.removed.name"),
                    lang(ctx).get("command.notifications.twitch.removed.value").replace("{$streamer}", channelName),
                    false)
                    .setThumbnail(channelInformation.getString("profilePicture"));
        }
        // Add streamer
        else {
            // Streamer limit reached
            if (NotificationsTwitchManager.getInstance().getStreamers(ctx.getGuild()).size() >= 100) {
                error(ctx).setDescription(lang(ctx).get("command.notifications.twitch.error.limit"))
                        .send();
                return;
            }

            NotificationsTwitchManager.getInstance().addStreamer(ctx.getGuild(), channelName); // Remove streamer from the database
            // Complete embed
            success.addField("\uD83D\uDD14 │ " + lang(ctx).get("command.notifications.twitch.added.name"),
                    lang(ctx).get("command.notifications.twitch.added.value").replace("{$streamer}", channelName),
                    false)
                    .setThumbnail(channelInformation.getString("profilePicture"));
        }

        ctx.getChannel().sendMessage(success.build()).queue(); //sent message
    }
}