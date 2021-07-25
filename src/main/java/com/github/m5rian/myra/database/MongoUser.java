package com.github.m5rian.myra.database;

import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.DiscordBot;
import com.github.m5rian.myra.utilities.UserBadge;
import com.github.m5rian.myra.utilities.Utilities;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;

public class MongoUser {
    private final MongoDb mongoDb = MongoDb.getInstance();
    private final String userId;
    private boolean bot = false;
    private boolean unavailable = false;
    private Document document;

    public MongoUser(String userId) {
        this.userId = userId;

        // User hasn't a user document
        if (mongoDb.getCollection("users").find(eq("userId", userId)).first() == null) {
            final User user = DiscordBot.shardManager.retrieveUserById(userId).complete();
            final Document userDocument = MongoDocuments.createUserDocument(user); // Create document for user
            mongoDb.getCollection("users").insertOne(userDocument); // Insert document
        }
        this.document = mongoDb.getCollection("users").find(eq("userId", userId)).first(); // Get user document
    }

    public MongoUser(User user) {
        this.userId = user.getId();

        // User is bot
        if (user.isBot()) this.bot = true;
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

    public static MongoUser get(String userId) {
        return Config.CACHE_USER.get(userId);
    }

    public static MongoUser get(User user) {
        return Config.CACHE_USER.get(user.getId());
    }

    public Document getDocument() {
        return this.document;
    }

    public MongoUser update() {
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.userId), this.document); // Update database
        return this;
    }

    /**
     * Update all database data of the current user.
     * This method automatically calls {@link MongoUser#update()} because of it's async.
     *
     * @return Returns the current {@link MongoUser} object for chaining purpose.
     */
    public MongoUser updateUserData() {
        if (this.bot || this.unavailable) return null;

        clearBadges(); // Clear current badges
        DiscordBot.shardManager.getGuildById(Config.MARIAN_SERVER_ID).retrieveMemberById(this.userId)
                .map(member -> {
                    final List<UserBadge> customBadges = UserBadge.getMyraBadges(member); // Get custom badges of member
                    addBadges(customBadges); // Add all custom badges
                    return member.getUser();
                })
                // If getting the member failed retrieve the user separately
                .onErrorFlatMap(ErrorResponse.UNKNOWN_MEMBER::test, error -> DiscordBot.shardManager.retrieveUserById(this.userId))
                .queue(user -> {
                    final List<UserBadge> discordBadges = UserBadge.getDiscordBadges(user); // Get Discords badges
                    addBadges(discordBadges);

                    setName(user.getName());
                    setDiscriminator(user.getDiscriminator());
                    setAvatar(user.getEffectiveAvatarUrl());
                    update(); // Push changes
                });
        return this;
    }

    /**
     * Is null if user is a bot.
     *
     * @return Returns the name of the user.
     */
    public String getName() {
        return (bot || unavailable ? null : this.document.getString("name"));
    }

    /**
     * Change a users name in the database.
     *
     * @param name New name.
     * @return Returns the current {@link MongoUser} object for chaining purpose.
     */
    public MongoUser setName(String name) {
        if (this.bot || this.unavailable) return null;

        this.document.replace("name", name); // Set name
        return this;
    }

    /**
     * Is null if user is a bot.
     *
     * @return Returns the discriminator of the user.
     */
    public String getDiscriminator() {
        return (bot || unavailable ? null : this.document.getString("discriminator"));
    }

    /**
     * Change a users discriminator in the database.
     *
     * @param discriminator New discriminator.
     * @return Returns the current {@link MongoUser} object for chaining purpose.
     */
    public MongoUser setDiscriminator(String discriminator) {
        if (this.bot || this.unavailable) return null;

        this.document.replace("discriminator", discriminator); // Set discriminator
        return this;
    }

    /**
     * Is null if user is a bot.
     *
     * @return Returns the avatar of the user.
     */
    public String getAvatar() {
        return (bot || unavailable ? null : this.document.getString("avatar"));
    }

    /**
     * Change a users avatar in the database.
     *
     * @param avatar New avatar.
     * @return Returns the current {@link MongoUser} object for chaining purpose.
     */
    public MongoUser setAvatar(String avatar) {
        if (this.bot || this.unavailable) return null;

        this.document.replace("avatar", avatar); // Set avatar
        return this;
    }

    /**
     * Is null if user is a bot.
     *
     * @return Returns all badges of the user.
     */
    public List<UserBadge> getBadges() {
        if (this.bot || this.unavailable) return null;

        final List<UserBadge> badges = new ArrayList<>(); // List for all badges
        final List<String> badgesRaw = this.document.getList("badges", String.class); // Get badges

        for (String badgeRaw : badgesRaw) {
            final UserBadge badge = UserBadge.find(badgeRaw); // Get badge as UserBadge
            badges.add(badge); // Add badge to list
        }
        return badges;
    }

    /**
     * Set the badges of the user.
     * Is null if the user is a bot.
     *
     * @param badges All badges of the user.
     * @return Returns the current {@link MongoUser} object for chaining purpose.
     */
    public MongoUser setBadges(List<UserBadge> badges) {
        if (this.bot || this.unavailable) return null;

        final List<String> badgesString = new ArrayList<>(); // Create list for all badges as string
        for (UserBadge badge : badges) {
            badgesString.add(badge.getName()); // Add badge
        }
        this.document.replace("badges", badgesString); // Set badges
        return this;
    }

    /**
     * Set the badges of the user.
     *
     * @param badges All badges of the user.
     * @return Returns the current {@link MongoUser} object for chaining purpose.
     */
    public MongoUser addBadges(List<UserBadge> badges) {
        if (this.bot || this.unavailable) return null;

        final List<String> currentBadges = this.document.getList("badges", String.class); // Get current badges
        final List<String> newBadges = badges.stream().map(UserBadge::getName).collect(Collectors.toList());
        newBadges.addAll(currentBadges); // Add all old badges to the new ones
        this.document.replace("badges", newBadges); // Set badges
        return this;
    }

    /**
     * Clears all badges of the user.
     *
     * @return Returns the current {@link MongoUser} object for chaining purpose.
     */
    public MongoUser clearBadges() {
        if (this.bot || this.unavailable) return null;

        this.document.getList("badges", String.class).clear(); // Clear list
        return this;
    }

    /**
     * Is null if user is a bot.
     *
     * @return Returns the xp of the user.
     */
    public Integer getXp() {
        return (this.bot || this.unavailable ? null : this.document.getInteger("xp"));
    }

    /**
     * Add a certain amount of experience points to a user.
     *
     * @param xpToAdd Experience to add.
     * @return Returns the current {@link MongoUser} object for chaining purpose.
     */
    public MongoUser addXp(int xpToAdd) {
        if (this.bot || this.unavailable) return null;

        this.document.replace("xp", Utilities.getBsonLong(this.document, "xp") + xpToAdd); // Add xp
        return this;
    }

    /**
     * @return Returns the amount of messages a user wrote.
     */
    public Integer getMessages() {
        return (bot || unavailable ? null : this.document.getInteger("messages"));
    }

    /**
     * Add one message to the total amount of written messages.
     *
     * @return Returns the current {@link MongoUser} object for chaining purpose.
     */
    public MongoUser addMessage() {
        if (this.bot || this.unavailable) return null;

        this.document.replace("messages", this.document.getInteger("messages") + 1); // Add one message
        return this;
    }

}
