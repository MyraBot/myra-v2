package com.myra.dev.marian.database.guild;

import com.myra.dev.marian.database.MongoDb;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class Nested {
    // Variables
    private final MongoDb mongoDb;
    private final Guild guild;
    private final String nested;

    /**
     * @param mongoDb A {@link MongoDb} instance.
     * @param guild   The guild of the current document.
     * @param nested  The key of the nested document.
     */
    public Nested(MongoDb mongoDb, Guild guild, String nested) {
        this.mongoDb = mongoDb;
        this.guild = guild;
        this.nested = nested;
    }

    /**
     * @param key The key to search for.
     * @return Returns a {@link String} given by a key.
     */
    public String getString(String key) {
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().get(this.nested, Document.class).getString(key);
    }

    /**
     * Replace a string in the guild document of {@link Nested#guild}.
     *
     * @param key   The key to search for.
     * @param value The new value.
     */
    public void setString(String key, String value) {
        // Replace value
        final Document updatedDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        updatedDocument.get(this.nested, Document.class).replace(key, value); // Update value
        // Update database
        mongoDb.getCollection("guilds").findOneAndReplace(eq("guildId", guild.getId()), updatedDocument);
    }

    /**
     * @param key The key to search for.
     * @return Returns a {@link Integer} given by a key.
     */
    public Integer getInteger(String key) {
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().get(this.nested, Document.class).getInteger(key);
    }

    /**
     * Replace an integer in the guild document of {@link Nested#guild}.
     *
     * @param key   The key to search for.
     * @param value The new value.
     */
    public void setInteger(String key, Integer value) {
        // Replace value
        final Document updatedDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        updatedDocument.get(this.nested, Document.class).replace(key, value); // Update value
        // Update database
        mongoDb.getCollection("guilds").findOneAndReplace(eq("guildId", guild.getId()), updatedDocument);
    }

    /**
     * @param key The key to search for.
     * @return Returns the value, found by the given key.
     */
    public Long getLong(String key) {
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().get(this.nested, Document.class).getLong(key);
    }

    /**
     * Replace a long in the guild document of {@link Nested#guild}.
     *
     * @param key   The key to search for.
     * @param value The new value.
     */
    public void setLong(String key, Long value) {
        // Replace value
        final Document updatedDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        updatedDocument.get(this.nested, Document.class).replace(key, value); // Update value
        // Update database
        mongoDb.getCollection("guilds").findOneAndReplace(eq("guildId", guild.getId()), updatedDocument);
    }

    /**
     * @param key The key to search for.
     * @return Returns the value, found by the given key.
     */
    public boolean getBoolean(String key) {
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().get(this.nested, Document.class).getBoolean(key);
    }

    /**
     * Replace a boolean in the guild document of {@link Nested#guild}.
     *
     * @param key   The key to search for.
     * @param value The new value.
     */
    public void setBoolean(String key, boolean value) {
        // Replace value
        final Document updatedDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        updatedDocument.get(this.nested, Document.class).replace(key, value); // Update value
        // Update database
        mongoDb.getCollection("guilds").findOneAndReplace(eq("guildId", guild.getId()), updatedDocument);
    }

    /**
     * @param key   The key to search for.
     * @param clazz The Class type to return.
     * @param <T>   The returned class type.
     * @return Returns the value of the given key as the specified class type.
     */
    public <T> T get(String key, Class<T> clazz) {
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().get(this.nested, Document.class).get(key, clazz);
    }

    /**
     * @param key   The key to search for.
     * @param clazz The Class type to return.
     * @param <T>   The returned class type.
     * @return Returns a list of the class type T, found by the given key.
     */

    public <T> List<T> getList(String key, Class<T> clazz) {
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().get(this.nested, Document.class).getList(key, clazz);
    }

    /**
     * Replace a list of the class type T.
     *
     * @param key   The key to search for.
     * @param value The new list.
     * @param <T>   The class type of which the list will be.
     */
    public <T> void setList(String key, List<T> value) {
        // Replace value
        final Document updatedDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        updatedDocument.get(this.nested, Document.class).replace(key, value); // Update value
        // Update database
        mongoDb.getCollection("guilds").findOneAndReplace(eq("guildId", guild.getId()), updatedDocument);
    }

    /**
     * Set a key to null.
     *
     * @param key The key to find.
     */
    public void setNull(String key) {
        // Replace value
        Document updatedDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().get(this.nested, Document.class);
        updatedDocument.replace(key, null);
        // Update database
        mongoDb.getCollection("guilds").findOneAndReplace(eq("guildId", guild.getId()), updatedDocument);
    }
}
