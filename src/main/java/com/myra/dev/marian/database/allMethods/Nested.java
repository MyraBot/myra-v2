package com.myra.dev.marian.database.allMethods;

import com.myra.dev.marian.database.MongoDb;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class Nested {
    //variables
    private MongoDb mongoDb;
    private Guild guild;
    private String nested;

    //constructor
    public Nested(MongoDb mongoDb, Guild guild, String nested) {
        this.mongoDb = mongoDb;
        this.guild = guild;
        this.nested = nested;
    }

    /**
     * @param key  The key to search.
     * @param type Class type to return.
     * @param <T>  The generic.
     * @return Returns a value as the specified class type.
     */
    public <T> T get(String key, Class<T> type) {
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().get(nested, Document.class).get(key, type);
    }

    /**
     * @param key   The key to search.
     * @param value The value to replace the old value of the key.
     */
    public void setString(String key, String value) {
        Document guildDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(); // Get guildDocument
        Document nestedDocument = guildDocument.get(nested, Document.class); // Get nested document
        nestedDocument.replace(key, value); // Replace value

        mongoDb.getCollection("guilds").findOneAndReplace(mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(), guildDocument); // Update database
    }

    /**
     * @param key The key to search.
     * @return Returns a value from the given key.
     */
    public String getString(String key) {
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().get(nested, Document.class).getString(key); // Return value
    }

    public void setInteger(String key, Integer value) {
        Document guildDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(); // Get guildDocument
        Document nestedDocument = guildDocument.get(nested, Document.class); // Get nested document
        nestedDocument.replace(key, value); // Replace value

        mongoDb.getCollection("guilds").findOneAndReplace(mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(), guildDocument); // Update database
    }

    public void setBoolean(String key, boolean value) {
        Document guildDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(); // Get guildDocument
        Document nestedDocument = guildDocument.get(nested, Document.class); // Get nested document
        nestedDocument.replace(key, value); // Replace value

        mongoDb.getCollection("guilds").findOneAndReplace(mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(), guildDocument); // Update database
    }

    public Boolean getBoolean(String key) {
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().get(nested, Document.class).getBoolean(key); // Return value
    }
}
