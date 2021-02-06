package com.myra.dev.marian.database.managers;

import com.myra.dev.marian.database.MongoDb;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class NotificationsYoutubeManager {
    private final static NotificationsYoutubeManager INSTANCE = new NotificationsYoutubeManager();

    public static NotificationsYoutubeManager getInstance() {
        return INSTANCE;
    }

    private final static MongoDb db = MongoDb.getInstance(); // Get database

    public List<String> getYoutubers(Guild guild) {
        final Document document = db.getCollection("guilds").find(eq("guildId", guild.getId())).first(); // Get guild document
        final Document notifications = (Document) document.get("notifications"); // Get notifications document
        final List<String> youtuber = notifications.getList("youtube", String.class); // Get all youtubers in a list

        return youtuber; // Return the list of youtubers
    }

    public void addYoutuber(String channelId, Guild guild) {
        final Document document = db.getCollection("guilds").find(eq("guildId", guild.getId())).first(); // Get guild document
        final Document notifications = (Document) document.get("notifications"); // Get notifications document
        final List<String> youtuber = notifications.getList("youtube", String.class); // Get all youtubers in a list

        youtuber.add(channelId); // Add youtuber to list

        db.getCollection("guilds").findOneAndReplace(db.getCollection("guilds").find(eq("guildId", guild.getId())).first(), document); //Update guild document
    }

    public void removeYoutuber(String channelId, Guild guild) {
        final Document document = db.getCollection("guilds").find(eq("guildId", guild.getId())).first(); // Get guild document
        final Document notifications = (Document) document.get("notifications"); // Get notifications document
        final List<String> youtuber = notifications.getList("youtube", String.class); // Get all youtubers in a list

        youtuber.remove(channelId); // Remove youtuber from list

        db.getCollection("guilds").findOneAndReplace(db.getCollection("guilds").find(eq("guildId", guild.getId())).first(), document); //Update guild document
    }
}