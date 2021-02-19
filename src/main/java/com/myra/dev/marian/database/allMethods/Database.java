package com.myra.dev.marian.database.allMethods;

import com.myra.dev.marian.database.MongoDb;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class Database {
    // Database
    private final MongoDb mongoDb = MongoDb.getInstance();

    //variable
    private Guild guild;

    //constructor
    public Database(Guild guild) {
        this.guild = guild;
    }

    /**
     * methods
     */

    // Get String
    public String getString(String key) {
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().getString(key);
    }

    //replace String
    public void setString(String key, String value) {
        // Replace value
        Document updatedDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        updatedDocument.replace(key, value);
        // Update database
        mongoDb.getCollection("guilds").findOneAndReplace(mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(), updatedDocument);
    }

    public Long getLong(String key) {
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().getLong(key);
    }

    public void setLong(String key, Long value) {
        // Replace value
        Document updatedDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        updatedDocument.replace(key, value);
        // Update database
        mongoDb.getCollection("guilds").findOneAndReplace(mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(), updatedDocument);
    }

    // Get boolean
    public boolean getBoolean(String key) {
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().getBoolean(key);
    }

    // Set boolean
    public void setBoolean(String key, boolean value) {
        // Replace value
        Document updatedDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        updatedDocument.replace(key, value);
        // Update database
        mongoDb.getCollection("guilds").findOneAndReplace(mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(), updatedDocument);
    }

    /**
     * @param key   The key to search.
     * @param clazz The return type.
     * @param <T>   The returned class type.
     * @return Returns the value of the given key as the specified class type.
     */
    public <T> T get(String key, Class<T> clazz) {
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().get(key, clazz);
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().getList(key, clazz);
    }

    public <T> void setList(String key, List<T> value) {
        // Replace value
        Document updatedDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        updatedDocument.replace(key, value);
        // Update database
        mongoDb.getCollection("guilds").findOneAndReplace(mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(), updatedDocument);
    }

    //replace String
    public void set(String key, Object value) {
        // Replace value
        Document updatedDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        updatedDocument.replace(key, value);
        // Update database
        mongoDb.getCollection("guilds").findOneAndReplace(mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(), updatedDocument);
    }


    //get nested object
    public Nested getNested(String nested) {
        return new Nested(mongoDb, guild, nested);
    }

    //get members
    public GetMembers getMembers() {
        return new GetMembers(mongoDb, guild);
    }

    //get listeners
    public GetListenerManager getListenerManager() {
        return new GetListenerManager(mongoDb, guild);
    }

    // Get Leveling
    public GetLeveling getLeveling() {
        return new GetLeveling(mongoDb, guild);
    }
}
