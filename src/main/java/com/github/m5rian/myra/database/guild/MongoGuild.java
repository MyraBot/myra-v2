package com.github.m5rian.myra.database.guild;

import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.database.MongoDb;
import com.github.m5rian.myra.database.guild.member.GuildMembers;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoGuild {
    private final MongoDb mongoDb = MongoDb.getInstance();
    private final JDA jda;
    private final String guildId;
    private final Document document;
    /**
     * @param jda     A {@link JDA} Object.
     * @param guildId The guild to search for.
     */
    public MongoGuild(JDA jda, String guildId) {
        this.jda = jda;
        this.guildId = guildId;
        this.document = mongoDb.getCollection("guilds").find(eq("guildId", this.guildId)).first();
    }

    /**
     * @param guild The guild to search for.
     */
    public MongoGuild(Guild guild) {
        this.jda = guild.getJDA();
        this.guildId = guild.getId();
        this.document = mongoDb.getCollection("guilds").find(eq("guildId", this.guildId)).first();
    }

    public static MongoGuild get(String guildId) {
        return Config.CACHE_GUILD.get(guildId);
    }

    public static MongoGuild get(Guild guild) {
        return Config.CACHE_GUILD.get(guild.getId());
    }

    /**
     * @param key The key to search for.
     * @return Returns a {@link String} given by a key.
     */
    public String getString(String key) {
        return this.document.getString(key);
    }

    /**
     * Replace a string in the guild document.
     *
     * @param key   The key to search for.
     * @param value The new value.
     */
    public void setString(String key, String value) {
        this.document.replace(key, value); // Replace value
        mongoDb.getCollection("guilds").findOneAndReplace(eq("guildId", this.guildId), this.document); // Update database
    }

    /**
     * @param key The key to search for.
     * @return Returns the value, found by the given key.
     */
    public Long getLong(String key) {
        return this.document.getLong(key);
    }

    /**
     * Replace a long in the guild document.
     *
     * @param key   The key to search for.
     * @param value The new value.
     */
    public void setLong(String key, Long value) {
        this.document.replace(key, value); // Replace value
        mongoDb.getCollection("guilds").findOneAndReplace(eq("guildId", this.guildId), this.document); // Update database
    }

    /**
     * @param key The key to search for.
     * @return Returns the value, found by the given key.
     */
    public boolean getBoolean(String key) {
        return this.document.getBoolean(key);
    }

    /**
     * Replace a boolean in the guild document.
     *
     * @param key   The key to search for.
     * @param value The new value.
     */
    public void setBoolean(String key, boolean value) {
        this.document.replace(key, value); // Replace value
        mongoDb.getCollection("guilds").findOneAndReplace(eq("guildId", this.guildId), this.document); // Update database
    }

    /**
     * @param key   The key to search for.
     * @param clazz The Class type to return.
     * @param <T>   The returned class type.
     * @return Returns the value of the given key as the specified class type.
     */
    public <T> T get(String key, Class<T> clazz) {
        return this.document.get(key, clazz);
    }

    /**
     * @param key   The key to search for.
     * @param clazz The Class type to return.
     * @param <T>   The returned class type.
     * @return Returns a list of the class type T, found by the given key.
     */

    public <T> List<T> getList(String key, Class<T> clazz) {
        return this.document.getList(key, clazz);
    }

    /**
     * Replace a list of the class type T.
     *
     * @param key   The key to search for.
     * @param value The new list.
     * @param <T>   The class type of which the list will be.
     */
    public <T> void setList(String key, List<T> value) {
        this.document.replace(key, value); // Replace value
        mongoDb.getCollection("guilds").findOneAndReplace(eq("guildId", this.guildId), this.document); // Update database
    }

    /**
     * Set a key to null.
     *
     * @param key The key to find.
     */
    public void setNull(String key) {
        this.document.replace(key, null); // Replace value
        // Update database
        mongoDb.getCollection("guilds").findOneAndReplace(eq("guildId", this.guildId), this.document);
    }

    /**
     * Get a nested object from the guild document.
     *
     * @param nested The nested object key.
     * @return Returns a {@link Document}, which matches the key of nested.
     */
    public Nested getNested(String nested) {
        return new Nested(mongoDb, this.guildId, nested);
    }

    /**
     * Get the members of the guild.
     *
     * @return Returns a {@link GuildMembers} object.
     */
    public GuildMembers getMembers() {
        return new GuildMembers(this.jda, this.guildId);
    }

    /**
     * @return Returns the {@link GuildListeners} for the current guild.
     */
    public GuildListeners getListenerManager() {
        return new GuildListeners(mongoDb, this.guildId);
    }

    /**
     * @return Returns the {@link GuildLeveling} for the current guild
     */
    public GuildLeveling getLeveling() {
        return new GuildLeveling(this.guildId);
    }
}
