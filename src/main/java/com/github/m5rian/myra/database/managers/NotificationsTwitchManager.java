package com.github.m5rian.myra.database.managers;

import com.github.m5rian.myra.database.MongoDb;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class NotificationsTwitchManager {
    private final static NotificationsTwitchManager INSTANCE = new NotificationsTwitchManager();

    public static NotificationsTwitchManager getInstance() {
        return INSTANCE;
    }

    private final static MongoDb db = MongoDb.getInstance(); // Get database

    public List<String> getStreamers(Guild guild) {
        final Document document = db.getCollection("guilds").find(eq("guildId", guild.getId())).first(); // Get guild document
        final Document notifications = document.get("notifications", Document.class); // Get notifications document
        final List<String> streamers = notifications.getList("twitch", String.class); // Get all streamers in a list

        return streamers; // Return the list of streamers
    }

    public void addStreamer(Guild guild, String name) {
        final Document document = db.getCollection("guilds").find(eq("guildId", guild.getId())).first(); // Get guild document
        final Document notifications = (Document) document.get("notifications"); // Get notifications document
        final List<String> streamer = notifications.getList("twitch", String.class); // Get all streamers in a list

        streamer.add(name); // Add streamer to list

        db.getCollection("guilds").findOneAndReplace(db.getCollection("guilds").find(eq("guildId", guild.getId())).first(), document); //Update guild document
    }

    public void removeStreamer(Guild guild, String name) {
        final Document document = db.getCollection("guilds").find(eq("guildId", guild.getId())).first(); // Get guild document
        final Document notifications = (Document) document.get("notifications"); // Get notifications document
        final List<String> streamer = notifications.getList("twitch", String.class); // Get all streamer in a list

        streamer.remove(name); // Remove streamer from list

        db.getCollection("guilds").findOneAndReplace(db.getCollection("guilds").find(eq("guildId", guild.getId())).first(), document); //Update guild document
    }
}
