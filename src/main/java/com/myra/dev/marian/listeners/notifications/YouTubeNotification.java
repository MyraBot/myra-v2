package com.myra.dev.marian.listeners.notifications;

import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.database.managers.NotificationsYoutubeManager;
import com.myra.dev.marian.utilities.APIs.GoogleYouTube;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import org.bson.Document;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class YouTubeNotification {
    private final MongoDb mongoDb = MongoDb.getInstance(); // Get database

    public void start(ReadyEvent event) throws Exception {
        final int start = 60 - LocalDateTime.now().getMinute() % 60;

        Utilities.TIMER.scheduleAtFixedRate(() -> {   // Loop
            try {
                final Iterator<Guild> guilds = event.getJDA().getGuilds().iterator(); // Create an iterator for the guilds
                while (guilds.hasNext()) { // Loop through every guild
                    final Guild guild = guilds.next(); // Get next guild
                    Database db = new Database(guild); // Get database

                    // Get variables
                    List<String> youtubers = NotificationsYoutubeManager.getInstance().getYoutubers(guild); // Get all streamers
                    String channelRaw = db.getNested("notifications").getString("channel");

                    if (youtubers.isEmpty()) continue;  // No streamers are set

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
                        List<JSONObject> latestVideos = GoogleYouTube.getInstance().getLatestVideos(channelId); // Get the latest videos
                        // For every video
                        for (JSONObject videoInformation : latestVideos) {

                            final JSONObject video = videoInformation.getJSONObject("snippet"); // Get video information
                            final String videoId = videoInformation.getJSONObject("id").getString("videoId"); // Get video id

                            // Get upload time
                            final ZonedDateTime date = ZonedDateTime.parse(video.getString("publishedAt"));
                            long publishedAtInMillis = date.toInstant().toEpochMilli(); // Get upload time in milliseconds

                            // Last youtube check was already made when the video came out
                            if (publishedAtInMillis < mongoDb.getCollection("config").find().first().getLong("youtube refresh"))
                                continue;

                            // Get all values
                            final JSONObject channelInformation = GoogleYouTube.getInstance().getChannelById(video.getString("channelId")); // Get the channel information
                            final String profilePicture = channelInformation.getJSONObject("thumbnails").getJSONObject("medium").getString("url"); // Get profile picture

                            final String channelName = video.getString("channelTitle");
                            final String title = video.getString("title"); // Get video title
                            final String thumbnail = video.getJSONObject("thumbnails").getJSONObject("medium").getString("url"); // Get thumbnail image

                            // Send message
                            final String messageRaw = db.getNested("notifications").getString("youtubeMessage");
                            if (!messageRaw.equals("not set")) {
                                final String message = messageRaw
                                        .replace("{youtuber}", channelName)
                                        .replace("{title}", title);

                                channel.sendMessage(message).queue();
                            }

                            // Create embed
                            EmbedBuilder notification = new EmbedBuilder()
                                    .setAuthor(channelName, "https://www.youtube.com/watch?v=" + videoId, profilePicture)
                                    .setColor(Utilities.getUtils().blue)
                                    .setDescription(Utilities.getUtils().hyperlink(title, "https://www.youtube.com/watch?v=" + videoId) + "\n")
                                    .setThumbnail(profilePicture)
                                    .setImage(thumbnail)
                                    .setTimestamp(date.toInstant());
                            channel.sendMessage(notification.build()).queue(); // Send video notification
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
