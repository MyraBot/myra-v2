package com.github.m5rian.myra.listeners.notifications;

import com.github.m5rian.myra.database.MongoDb;
import com.github.m5rian.myra.utilities.APIs.youtube.Youtube;
import com.github.m5rian.myra.utilities.APIs.youtube.data.YoutubeChannel;
import com.github.m5rian.myra.utilities.APIs.youtube.data.YoutubeVideo;
import com.mongodb.client.MongoCursor;
import com.github.m5rian.myra.DiscordBot;
import com.github.m5rian.myra.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class YoutubeNotification {
    private static final Logger LOGGER = LoggerFactory.getLogger(YoutubeNotification.class);

    public static void onVideoUpload(YoutubeChannel channel, YoutubeVideo video) {
        final EmbedBuilder notification = new EmbedBuilder()
                .setAuthor(channel.getName(), "https://www.youtube.com/channel/" + channel.getId())
                .setColor(Utilities.blue)
                .setDescription(Utilities.hyperlink(video.getTitle(), "https://www.youtube.com/watch?v=" + video.getId()))
                .setImage(video.getThumbnail())
                .setTimestamp(video.getUploadTime());

        int runs = 0;
        final MongoCursor<Document> guildDocuments = MongoDb.getInstance().getCollection("guilds").find(eq("notifications.youtube", channel.getId())).iterator();
        while (guildDocuments.hasNext()) {
            runs++;
            final Document guildDocument = guildDocuments.next(); // Get next guild document

            final String channelId = guildDocument.get("notifications", Document.class).getString("channel");
            if (channel.equals("not set")) continue;

            final TextChannel textchannel = DiscordBot.shardManager.getGuildById(guildDocument.getString("guildId")).getTextChannelById(channelId);
            if (textchannel == null) continue;

            // Get custom message
            String message = null;
            final String messageRaw = guildDocument.get("notifications", Document.class).getString("youtubeMessage");
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
        int subscriptions = 0;
        while (guilds.hasNext()) {
            final Document guild = guilds.next(); // Get guild document
            final List<String> channels = guild.get("notifications", Document.class).getList("youtube", String.class); // Get all youtubers
            channels.forEach(Youtube::subscribe); // Subscribe to all channels
            subscriptions += channels.size(); // Increase subscription count
        }

        LOGGER.info("Renewed " + subscriptions + " subscriptions"); // Log amount of subscription
    }

}
