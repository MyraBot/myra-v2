package com.myra.dev.marian.listeners.notifications;

import com.mongodb.client.MongoCursor;
import com.myra.dev.marian.DiscordBot;
import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.utilities.APIs.youtube.Youtube;
import com.myra.dev.marian.utilities.APIs.youtube.data.YoutubeChannel;
import com.myra.dev.marian.utilities.APIs.youtube.data.YoutubeVideo;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bson.Document;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class YoutubeNotification {

    public static void onVideoUpload(YoutubeChannel channel, YoutubeVideo video) {
        final EmbedBuilder notification = new EmbedBuilder()
                .setAuthor(channel.getName(), "https://www.youtube.com/channel/" + channel.getId())
                .setColor(Utilities.getUtils().blue)
                .setDescription(Utilities.getUtils().hyperlink(video.getTitle(), "https://www.youtube.com/watch?v=" + video.getId()))
                .setImage(video.getThumbnail())
                .setTimestamp(video.getUploadTime());

        int runs = 0;
        final MongoCursor<Document> guilds = MongoDb.getInstance().getCollection("guilds").find(eq(channel.getId())).iterator();
        while (guilds.hasNext()) {
            runs++;
            final Document guild = guilds.next(); // Get next guild document

            final String channelId = guild.get("notifications", Document.class).getString("channel");
            if (channel.equals("not set")) continue;

            TextChannel textchannel = DiscordBot.shardManager.getGuildById(channelId).getTextChannelById(channelId);
            if (textchannel == null) continue;

            // Get custom message
            String message = null;
            final String messageRaw = guild.get("notifications", Document.class).getString("youtubeMessage");
            if (!messageRaw.equals("not set")) {
                message = messageRaw
                        .replace("{youtuber}", channel.getName())
                        .replace("{title}", video.getTitle());
            }

            // Send notification without custom message
            if (message == null) textchannel.sendMessage(notification.build()).queue();
                // Send notification with custom message
            else textchannel.sendMessage(message).embed(notification.build()).queue();
        }

        // No server subscribed to this youtube channel
        if (runs == 0) Youtube.unsubscribe(channel.getId()); // Unsubscribe
    }

    public static void renewSubscriptions() {
        final MongoCursor<Document> guilds = MongoDb.getInstance().getCollection("guilds").find().iterator();
        while (guilds.hasNext()) {
            Document guild = guilds.next();

            List<String> channels = guild.get("notifications", Document.class).getList("youtube", String.class);
            channels.forEach(Youtube::subscribe); // Subscribe to all channels
        }
    }

}
