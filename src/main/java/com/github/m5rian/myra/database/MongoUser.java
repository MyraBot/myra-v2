package com.github.m5rian.myra.database;

import com.github.m5rian.myra.utilities.UserBadge;
import com.github.m5rian.myra.utilities.Utilities;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoUser {
    private final MongoDb mongoDb = MongoDb.getInstance();
    private final JDA jda;
    private final String userId;
    private boolean bot = false;
    private boolean unavailable = false;
    private Document document;

    public MongoUser(JDA jda, String userId) {
        this.jda = jda;
        this.userId = userId;

        // Retrieve user
        jda.retrieveUserById(this.userId).queue(user -> {
                    // User is bot
                    if (user.isBot()) {
                        this.bot = true;
                    }
                    // User isn't a bot
                    else {
                        // User hasn't a user document
                        if (mongoDb.getCollection("users").find(eq("userId", user.getId())).first() == null) {
                            final Document userDocument = MongoDocuments.createUserDocument(user); // Create document for user
                            mongoDb.getCollection("users").insertOne(userDocument); // Insert document
                        }
                        this.document = mongoDb.getCollection("users").find(eq("userId", user.getId())).first(); // Get user document
                    }
                },
                error -> this.unavailable = true);
    }

    public MongoUser(User user) {
        this.jda = user.getJDA();
        this.userId = user.getId();

        // User is bot
        if (user.isBot()) {
            this.bot = true;
        }
        // User isn't a bot
        else {
            // User hasn't a user document
            if (mongoDb.getCollection("users").find(eq("userId", user.getId())).first() == null) {
                final Document userDocument = MongoDocuments.createUserDocument(user); // Create document for user
                mongoDb.getCollection("users").insertOne(userDocument); // Insert document
            }
            this.document = mongoDb.getCollection("users").find(eq("userId", user.getId())).first(); // Get user document
        }
    }

    public void updateUserData() {
        if (this.bot || this.unavailable) return;

        this.jda.retrieveUserById(userId).queue(user -> {
            document.replace("name", user.getName()); // Set name
            document.replace("discriminator", user.getDiscriminator()); // Set discriminator
            document.replace("avatar", user.getEffectiveAvatarUrl()); // Set avatar
            final List<UserBadge> badges = UserBadge.getUserBadges(user);
            final List<String> badgesString = new ArrayList<>(); // Create list for all badges as string
            for (UserBadge badge : badges) {
                badgesString.add(badge.getName()); // Add badge
            }
            document.replace("badges", badgesString); // Set badges

            mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.userId), document); // Update database
        });
    }

    /**
     * Is null if user is a bot.
     *
     * @return Returns the name of the user.
     */
    public String getName() {
        return (bot || unavailable ? null : document.getString("name"));
    }

    /**
     * Change a users name in the database.
     *
     * @param name New name.
     */
    public void setName(String name) {
        if (bot) return;

        document.replace("name", name); // Set name
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.userId), document); // Update database
    }

    /**
     * Is null if user is a bot.
     *
     * @return Returns the discriminator of the user.
     */
    public String getDiscriminator() {
         return (bot || unavailable ? null : document.getString("discriminator"));
    }

    /**
     * Change a users discriminator in the database.
     *
     * @param discriminator New discriminator.
     */
    public void setDiscriminator(String discriminator) {
        if (bot) return;

        document.replace("discriminator", discriminator); // Set discriminator
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.userId), document); // Update database
    }

    /**
     * Is null if user is a bot.
     *
     * @return Returns the avatar of the user.
     */
    public String getAvatar() {
         return (bot || unavailable ? null : document.getString("avatar"));
    }

    /**
     * Change a users avatar in the database.
     *
     * @param avatar New avatar.
     */
    public void setAvatar(String avatar) {
        if (bot) return;

        document.replace("avatar", avatar); // Set avatar
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.userId), document); // Update database
    }

    /**
     * Is null if user is a bot.
     *
     * @return Returns all flags of the user.
     */
    public List<UserBadge> getBadges() {
        if (bot) return null;

        final List<UserBadge> badges = new ArrayList<>(); // List for all badges
        final List<String> badgesRaw = document.getList("badges", String.class); // Get badges

        for (String badgeRaw : badgesRaw) {
            final UserBadge badge = UserBadge.find(badgeRaw); // Get badge as UserBadge
            badges.add(badge); // Add badge to list
        }
        return badges;
    }

    /**
     * Set the badges of the user.
     *
     * @param badges All badges of the user.
     */
    public void setBadges(List<UserBadge> badges) {
        if (bot) return;

        final List<String> badgesString = new ArrayList<>(); // Create list for all badges as string
        for (UserBadge badge : badges) {
            badgesString.add(badge.getName()); // Add badge
        }
        document.replace("badges", badgesString); // Set badges
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.userId), document); // Update database
    }

    /**
     * Is null if user is a bot.
     *
     * @return Returns the xp of the user.
     */
    public Integer getXp() {
         return (bot || unavailable ? null : document.getInteger("xp"));
    }

    /**
     * Add a certain amount of experience points to a user.
     *
     * @param xpToAdd Experience to add.
     */
    public void addXp(int xpToAdd) {
        if (bot) return; // User is bot

        document.replace("xp", Utilities.getBsonLong(document, "xp") + xpToAdd); // Add xp
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.userId), document); // Update database
    }

    /**
     * @return Returns the amount of messages a user wrote.
     */
    public Integer getMessages() {
         return (bot || unavailable ? null : document.getInteger("messages"));
    }

    /**
     * Add one message to the total amount of written messages.
     */
    public void addMessage() {
        if (bot) return; // User is bot

        document.replace("messages", document.getInteger("messages") + 1); // Add one message
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.userId), document); // Update database
    }

}
