package com.myra.dev.marian.database;

import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static com.mongodb.client.model.Filters.eq;

public class MongoDbDelete extends ListenerAdapter {
    //database
    private static MongoDb mongoDb;

    //set variable
    public static void setDb(MongoDb db) {
        mongoDb = db;
    }

    public void onGuildLeave(GuildLeaveEvent event) {
        mongoDb.getCollection("guilds").deleteOne(eq("guildId", event.getGuild().getId()));
    }
}
