package com.myra.dev.marian.database.guild;

import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.database.guild.member.GuildMembers;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoGuild {
    private final MongoDb mongoDb = MongoDb.getInstance();
    private final Guild guild; // Current guild

    /**
     * @param guild The guild to search for.
     */
    public MongoGuild(Guild guild) {
        this.guild = guild;
    }

    /**
     * @param key The key to search for.
     * @return Returns a {@link String} given by a key.
     */
    public String getString(String key) {
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().getString(key);
    }

    /**
     * Replace a string in the guild document of {@link MongoGuild#guild}.
     *
     * @param key   The key to search for.
     * @param value The new value.
     */
    public void setString(String key, String value) {
        // Replace value
        Document updatedDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        updatedDocument.replace(key, value);
        // Update database
        mongoDb.getCollection("guilds").findOneAndReplace(eq("guildId", guild.getId()), updatedDocument);
    }

    /**
     * @param key The key to search for.
     * @return Returns the value, found by the given key.
     */
    public Long getLong(String key) {
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().getLong(key);
    }

    /**
     * Replace a long in the guild document of {@link MongoGuild#guild}.
     *
     * @param key   The key to search for.
     * @param value The new value.
     */
    public void setLong(String key, Long value) {
        // Replace value
        Document updatedDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        updatedDocument.replace(key, value);
        // Update database
        mongoDb.getCollection("guilds").findOneAndReplace(eq("guildId", guild.getId()), updatedDocument);
    }

    /**
     * @param key The key to search for.
     * @return Returns the value, found by the given key.
     */
    public boolean getBoolean(String key) {
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().getBoolean(key);
    }

    /**
     * Replace a boolean in the guild document of {@link MongoGuild#guild}.
     *
     * @param key   The key to search for.
     * @param value The new value.
     */
    public void setBoolean(String key, boolean value) {
        // Replace value
        Document updatedDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        updatedDocument.replace(key, value);
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
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().get(key, clazz);
    }

    /**
     * @param key   The key to search for.
     * @param clazz The Class type to return.
     * @param <T>   The returned class type.
     * @return Returns a list of the class type T, found by the given key.
     */

    public <T> List<T> getList(String key, Class<T> clazz) {
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().getList(key, clazz);
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
        Document updatedDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        updatedDocument.replace(key, value);
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
        Document updatedDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        updatedDocument.replace(key, null);
        // Update database
        mongoDb.getCollection("guilds").findOneAndReplace(eq("guildId", guild.getId()), updatedDocument);
    }

    /**
     * Get a nested object from the {@link MongoGuild#guild} document.
     *
     * @param nested The nested object key.
     * @return Returns a {@link Document}, which matches the key of nested.
     */
    public Nested getNested(String nested) {
        return new Nested(mongoDb, guild, nested);
    }

    /**
     * Get the members of the guild.
     *
     * @return Returns a {@link GuildMembers} object.
     */
    public GuildMembers getMembers() {
        return new GuildMembers(mongoDb, guild);
    }

    /**
     * @return Returns the {@link GuildListeners} for the {@link MongoGuild#guild}.
     */
    public GuildListeners getListenerManager() {
        return new GuildListeners(mongoDb, guild);
    }

    /**
     * @return Returns the {@link GuildLeveling} for the {@link MongoGuild#guild
     */
    public GuildLeveling getLeveling() {
        return new GuildLeveling(mongoDb, guild);
    }
}
