package com.myra.dev.marian.database;

import net.dv8tion.jda.api.entities.User;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class MongoUser {
    private final MongoDb mongoDb = MongoDb.getInstance();
    private final User user;
    private boolean bot = false;
    private Document document;

    public MongoUser(User user) {
        this.user = user;

        // User is bot
        if (user.isBot()) {
            this.bot = true;
            return;
        }

        // User hasn't a user document
        if (mongoDb.getCollection("users").find(eq("userId", user.getId())).first() == null) {
            final Document userDocument = MongoDocuments.createUserDocument(user); // Create document for user
            mongoDb.getCollection("users").insertOne(userDocument); // Insert document
        }
        this.document = mongoDb.getCollection("users").find(eq("userId", user.getId())).first(); // Get user document
    }

    /**
     * Is null if user is a bot.
     *
     * @return Returns the name of the user.
     */
    public String getName() {
        return (bot ? null : document.getString("name"));
    }

    /**
     * Change a users name in the database.
     *
     * @param name New name.
     */
    public void setName(String name) {
        if (bot) return;

        document.replace("name", name); // Set name
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.user.getId()), document); // Update database
    }

    /**
     * Is null if user is a bot.
     *
     * @return Returns the discriminator of the user.
     */
    public String getDiscriminator() {
        return (bot ? null : document.getString("discriminator"));
    }

    /**
     * Change a users discriminator in the database.
     *
     * @param discriminator New discriminator.
     */
    public void setDiscriminator(String discriminator) {
        if (bot) return;

        document.replace("discriminator", discriminator); // Set discriminator
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.user.getId()), document); // Update database
    }

    /**
     * Is null if user is a bot.
     *
     * @return Returns the avatar of the user.
     */
    public String getAvatar() {
        return (bot ? null : document.getString("avatar"));
    }

    /**
     * Change a users avatar in the database.
     *
     * @param avatar New avatar.
     */
    public void setAvatar(String avatar) {
        if (bot) return;

        document.replace("avatar", avatar); // Set avatar
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.user.getId()), document); // Update database
    }

    /**
     * Is null if user is a bot.
     *
     * @return Returns the xp of the user.
     */
    public Integer getXp() {
        return (bot ? null : document.getInteger("xp"));
    }

    /**
     * Add a certain amount of experience points to a user.
     *
     * @param xpToAdd Experience to add.
     */
    public void addXp(int xpToAdd) {
        if (bot) return; // User is bot

        document.replace("xp", document.getInteger("xp") + xpToAdd); // Add xp
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.user.getId()), document); // Update database
    }

    /**
     * @return Returns the amount of messages a user wrote.
     */
    public Integer getMessages() {
        return (bot ? null : document.getInteger("messages"));
    }

    /**
     * Add one message to the total amount of written messages.
     */
    public void addMessage() {
        if (bot) return; // User is bot

        document.replace("messages", document.getInteger("messages") + 1); // Add one message
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", user.getId()), document); // Update database
    }

}
