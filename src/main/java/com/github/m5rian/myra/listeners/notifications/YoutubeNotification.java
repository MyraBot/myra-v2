package com.github.m5rian.myra.listeners.notifications;

import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.MongoDb;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.database.managers.NotificationsYoutubeManager;
import com.github.m5rian.myra.utilities.APIs.youtube.Youtube;
import com.github.m5rian.myra.utilities.APIs.youtube.YtChannel;
import com.github.m5rian.myra.utilities.APIs.youtube.YtVideo;
import com.github.m5rian.myra.utilities.APIs.youtube.YtVideos;
import com.github.m5rian.myra.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import org.bson.Document;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class YoutubeNotification implements CommandHandler {
    private static final MongoDb mongoDb = MongoDb.getInstance(); // Get database

    public static void start(ReadyEvent event) {
        final int start = 5 - LocalDateTime.now().getMinute() % 5;

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {   // Loop
            try {
                final long lastRefresh = mongoDb.getCollection("config").find().first().getLong("youtube refresh"); // Get last youtube refresh

                final Iterator<Guild> guilds = event.getJDA().getGuilds().iterator(); // Create an iterator for the guilds
                while (guilds.hasNext()) { // Loop through every guild
                    final Guild guild = guilds.next(); // Get next guild
                    MongoGuild db = MongoGuild.get(guild); // Get database

                    List<String> youtubers = NotificationsYoutubeManager.getInstance().getYoutubers(guild); // Get all youtubers
                    if (youtubers.isEmpty()) continue;  // No streamers are set

                    String channelRaw = db.getNested("notifications").getString("channel"); // Get notifications channel
                    // If no notifications channel is set
                    if (channelRaw.equals("not set")) continue;
                    TextChannel channel = guild.getTextChannelById(channelRaw); // Get notifications channel

                    // For each youtuber
                    for (String channelId : youtubers) {
                        final YtVideos latestVideos = Youtube.getLatestVideos(channelId); // Get latest videos
                        // For every video
                        for (YtVideo video : latestVideos.getVideos()) {
                            final YtChannel youtuber = latestVideos.getChannel(); // Get youtuber
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
                                    .setAuthor(youtuber.getChannelName(), "https://www.youtube.com/watch?v=" + video.getId())
                                    .setColor(Utilities.blue)
                                    .setDescription(Utilities.hyperlink(video.getTitle(), "https://www.youtube.com/watch?v=" + video.getId()) + "\n")
                                    //.setThumbnail(youtuber.getAvatar())
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
        }, start, 15, TimeUnit.MINUTES);
    }
}
