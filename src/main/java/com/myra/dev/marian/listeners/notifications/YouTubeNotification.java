package com.myra.dev.marian.listeners.notifications;

import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.database.managers.NotificationsYoutubeManager;
import com.myra.dev.marian.utilities.APIs.youTube.Channel;
import com.myra.dev.marian.utilities.APIs.youTube.Video;
import com.myra.dev.marian.utilities.APIs.youTube.Videos;
import com.myra.dev.marian.utilities.APIs.youTube.YouTube;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import org.bson.Document;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class YouTubeNotification {
    private final MongoDb mongoDb = MongoDb.getInstance(); // Get database

    public void start(ReadyEvent event) throws Exception {
        final int start = 5 - LocalDateTime.now().getMinute() % 5;

        Utilities.TIMER.scheduleAtFixedRate(() -> {   // Loop
            try {
                final long lastRefresh = mongoDb.getCollection("config").find().first().getLong("youtube refresh"); // Get last youtube refresh

                final Iterator<Guild> guilds = event.getJDA().getGuilds().iterator(); // Create an iterator for the guilds
                while (guilds.hasNext()) { // Loop through every guild
                    final Guild guild = guilds.next(); // Get next guild
                    Database db = new Database(guild); // Get database

                    List<String> youtubers = NotificationsYoutubeManager.getInstance().getYoutubers(guild); // Get all youtubers
                    if (youtubers.isEmpty()) continue;  // No streamers are set

                    String channelRaw = db.getNested("notifications").getString("channel"); // Get notifications channel
                    // If no notifications channel is set
                    if (channelRaw.equals("not set")) {
                        new Error(null)
                                .setCommand("notifications")
                                .setEmoji("\uD83D\uDD14")
                                .setAvatar(guild.getIconUrl())
                                .setMessage("No notifications channel specified")
                                .setChannel(guild.getDefaultChannel())
                                .send();
                        continue;
                    }
                    TextChannel channel = guild.getTextChannelById(channelRaw); // Get notifications channel

                    // For each youtuber
                    for (String channelId : youtubers) {
                        final Videos latestVideos = YouTube.getApi().getLatestVideos(channelId); // Get latest videos
                        // For every video
                        for (Video video : latestVideos.getVideos()) {
                            final Channel youtuber = latestVideos.getChannel(); // Get youtuber
                            long publishedAtInMillis = video.getUploadTime().toInstant().toEpochMilli(); // Get upload time in milliseconds

                            // Last youtube check was already made when the video came out
                            if (publishedAtInMillis < lastRefresh) continue;



                            // Send message
                            String message = null;
                            final String messageRaw = db.getNested("notifications").getString("youtubeMessage");
                            if (!messageRaw.equals("not set")) {
                                message = messageRaw
                                        .replace("{youtuber}", youtuber.getChannelName())
                                        .replace("{title}", video.getTitle());
                            }

                            // Create embed
                            EmbedBuilder notification = new EmbedBuilder()
                                    .setAuthor(youtuber.getChannelName(), "https://www.youtube.com/watch?v=" + video.getId(), youtuber.getAvatar())
                                    .setColor(Utilities.getUtils().blue)
                                    .setDescription(Utilities.getUtils().hyperlink(video.getTitle(), "https://www.youtube.com/watch?v=" + video.getId()) + "\n")
                                    .setThumbnail(youtuber.getAvatar())
                                    .setImage(video.getThumbnail())
                                    .setTimestamp(video.getUploadTime().toInstant());

                            // Send notification without custom message
                            if (message == null) channel.sendMessage(notification.build()).queue();
                                // Send notification with custom message
                            else channel.sendMessage(message).embed(notification.build()).queue();
                        }
                    }
                }
                // Update last refresh in database
                final Document updatedDocument = mongoDb.getCollection("config").find().first(); // Get config document
                updatedDocument.replace("youtube refresh", System.currentTimeMillis()); // Update last check
                mongoDb.getCollection("config").findOneAndReplace(mongoDb.getCollection("config").find().first(), updatedDocument); // Update document

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, start, 120, TimeUnit.MINUTES);
    }
}
