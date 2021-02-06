package com.myra.dev.marian.commands.administrator.notifications;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.myra.dev.marian.database.managers.NotificationsTwitchManager;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.APIs.Twitch;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONObject;

import java.util.List;

@CommandSubscribe(
        name = "notification twitch",
        aliases = {"notification live", "notifications twitch", "notifications live"},
        requires = Administrator.class,
        channel = Channel.GUILD
)
public class Streamer implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder notificationUsage = new EmbedBuilder()
                    .setAuthor("notification Twitch", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "notification twitch <streamer>`", "\uD83D\uDCE1 │ Add and remove auto notifications for a twitch streamer", false);
            ctx.getChannel().sendMessage(notificationUsage.build()).queue();
            return;
        }
// Add or remove streamer
        final JSONObject channelInformation = new Twitch().getChannel(ctx.getArguments()[0]); // Get channel information
        if (channelInformation == null) {
            new Error(ctx.getEvent())
                    .setCommand("notifications twitch")
                    .setEmoji("\uD83D\uDCE1")
                    .setMessage("No streamer found" +
                            "\nTry using the end of the url!" +
                            "\nExample: www.twitch.tv/**m5rian**")
                    .send();
            return;
        }
        // Create embed
        EmbedBuilder streamer = new EmbedBuilder()
                .setAuthor("streamers", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue);
        // No streamer found
        if (channelInformation == null) {
            new Error(ctx.getEvent())
                    .setCommand("notifications twitch")
                    .setEmoji("\uD83D\uDCF9")
                    .setMessage("No streamer found")
                    .send();
            return;
        }

        final List<String> streamers = NotificationsTwitchManager.getInstance().getStreamers(ctx.getGuild()); // Get current streamers
        final String channelName = channelInformation.getString("user"); // Get channel name

        // Remove streamer
        if (streamers.contains(channelName)) {
            NotificationsTwitchManager.getInstance().removeStreamer(ctx.getGuild(), channelName); // Remove streamer from the database
            // Complete embed
            streamer
                    .addField("\uD83D\uDD15 │ Removed streamer", "Removed **" + channelName + "**", false)
                    .setThumbnail(channelInformation.getString("profilePicture"));
        }
        // Add streamer
        else {
            NotificationsTwitchManager.getInstance().addStreamer(ctx.getGuild(), channelName); // Remove streamer from the database
            // Complete embed
            streamer
                    .addField("\uD83D\uDD14 │ Added streamer", "Added **" + channelName + "**", false)
                    .setThumbnail(channelInformation.getString("profilePicture"));
        }


        ctx.getChannel().sendMessage(streamer.build()).queue(); //sent message
    }
}