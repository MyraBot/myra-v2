package com.github.m5rian.myra.database.guild.member;

import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.DiscordBot;
import com.github.m5rian.myra.database.MongoDb;
import com.github.m5rian.myra.database.MongoDocuments;
import com.github.m5rian.myra.listeners.leveling.Leveling;
import com.github.m5rian.myra.utilities.Utilities;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

public class GuildMember {
    // Variables
    private final MongoDb mongoDb = MongoDb.getInstance(); // A mongodb instance
    private final String guildId; // The ID of the guild
    private final String memberId; // The ID of the member
    private Document userDocument; // The document of the user
    private Document memberDocument; // The guild specific document of the member
    private boolean bot = false; // Is the member a bot?

    /**
     * This method searches for the {@link GuildMember#userDocument} and the {@link GuildMember#memberDocument}.
     * If there are no documents, it will create the missing ones.
     * Also the member gets checked on {@link User#isBot()}.
     *
     * @param guildId  The ID of the guild.
     * @param memberId The ID of the member.
     */
    public GuildMember(String guildId, String memberId) {
        this.guildId = guildId;
        this.memberId = memberId;

        final User user = DiscordBot.shardManager.retrieveUserById(this.memberId).complete(); // Retrieve member
        // User is a bot
        if (user.isBot()) {
            this.bot = true;
            return;
        }

        // Member hasn't a user document
        if (mongoDb.getCollection("users").find(eq("userId", this.memberId)).first() == null) {
            final Document userDocument = MongoDocuments.createUserDocument(user); // Create document for user
            mongoDb.getCollection("users").insertOne(userDocument); // Insert document
        }
        this.userDocument = mongoDb.getCollection("users").find(eq("userId", this.memberId)).first(); // Get user document

        // Member hasn't a guild document
        if (userDocument.get(this.guildId) == null) {
            final Document guildMemberDocument = MongoDocuments.createGuildMemberDocument(); // Create document for guild
            userDocument.put(this.guildId, guildMemberDocument); // Add document for guild to user document
            mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.memberId), userDocument); // Update database
        }
        this.memberDocument = userDocument.get(this.guildId, Document.class); // Get the guild document of the member
    }

    public static GuildMember get(String guildId, String memberId) {
        return Config.CACHE_MEMBER.get(guildId + ":" + memberId);
    }

    public static GuildMember get(Member member) {
        return Config.CACHE_MEMBER.get(member.getGuild().getId() + ":" + member.getId());
    }

    public Document getMemberDocument() {
        return this.memberDocument;
    }

    /**
     * @return Is the member a bot?
     */
    public boolean isBot() {
        return this.bot;
    }

    /**
     * Is null if member is a bot.
     *
     * @return Returns the xp of the member
     */
    public Long getXp() {
        return (bot ? null : Utilities.getBsonLong(memberDocument, "xp"));
    }

    /**
     * @param xp Experience you want the member to have.
     */
    public void setXp(long xp) {
        if (bot) return; // Member is bot

        memberDocument.replace("xp", xp); // Set xp
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.memberId), userDocument); // Update database
    }

    /**
     * Add a certain amount of experience points to a member.
     *
     * @param xpToAdd Experience to add.
     */
    public void addXp(int xpToAdd) {
        if (bot) return; // Member is bot

        memberDocument.replace("xp", getXp() + xpToAdd); // Add xp
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.memberId), userDocument); // Update database
    }

    /**
     * Is null if member is a bot.
     *
     * @return Returns the level of the member
     */
    public Integer getLevel() {
        return (bot ? null : memberDocument.getInteger("level"));
    }

    /**
     * Put a member on a specific level.
     *
     * @param level The level you want to set the member.
     */
    public void setLevel(int level) {
        memberDocument.replace("level", level); // Update level
        // Update xp
        final long xp = new Leveling().getXpFromLevel(level); // Get xp from level
        memberDocument.replace("xp", xp); // Replace xp
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.memberId), userDocument); // Update database
    }

    /**
     * Get the rank of a member.
     *
     * @return Returns the placement of a members level.
     */
    public int getRank() {
        List<LeaderboardMember> leaderboard = new ArrayList<>(); // Create leaderboard

        final MongoCursor<Document> iterator = mongoDb.getCollection("users").find(exists(this.guildId)).iterator(); // Create an iterator of all members, who are in the guild
        while (iterator.hasNext()) {
            final Document userDocument = iterator.next(); // Get next user document
            leaderboard.add(new LeaderboardMember(userDocument, this.guildId));  // Add member to leaderboard
        }
        iterator.close(); // Close the iterator

        leaderboard.sort(Comparator.comparing(LeaderboardMember::getXp).reversed()); // Sort list

        // Get rank
        int rank = 0;
        // Search for member
        for (LeaderboardMember memberDocument : leaderboard) {
            if (memberDocument.getId().equals(this.memberId)) {
                rank = leaderboard.indexOf(memberDocument) + 1;
                break;
            }
        }

        return rank; // Return rank
    }

    /**
     * @return Returns the time a member spent in voice calls in millis.
     */
    public Long getVoiceTime() {
        return bot ? null : Utilities.getBsonLong(memberDocument, "voiceCallTime");
    }

    /**
     * @param millis The amount of time a member spent in voice calls.
     */
    public void setVoiceTime(long millis) {
        memberDocument.replace("voiceCallTime", millis); // Replace current time
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.memberId), userDocument); // Update database
    }

    /**
     * @return Returns the amount of messages a member wrote.
     */
    public Long getMessages() {
        return (bot ? null : Utilities.getBsonLong(memberDocument, "messages"));
    }

    /**
     * Add one message to the total amount of written messages.
     */
    public void addMessage() {
        final long messageCount = Utilities.getBsonLong(memberDocument, "messages") + 1L;
        //System.out.println("old count " + Utilities.getBsonLong(memberDocument, "messages"));
        //System.out.println("new count" + messageCount);

        memberDocument.replace("messages", messageCount); // Add one message
        final Document $set = new Document("$set", new Document(this.guildId, memberDocument));
        mongoDb.getCollection("users").updateOne(Filters.eq("userId", this.memberId), $set);
        //   mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.memberId), userDocument); // Update database
    }

    public void setMessageCount(long amount) {
        memberDocument.replace("messages", amount); // Add one message
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.memberId), userDocument); // Update database
    }

    /**
     * @return Returns the balance of a member.
     */
    public Integer getBalance() {
        return (bot ? null : memberDocument.getInteger("balance"));
    }

    /**
     * Set a members balance to a specific amount.
     *
     * @param balance The amount of balance you want the member to have.
     */
    public void setBalance(int balance) {
        memberDocument.replace("balance", balance); // Replace current balance
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.memberId), userDocument); // Update database
    }

    /**
     * @return Returns the last claimed daily reward as a long since unix epoch.
     */
    public Long getLastClaim() {
        return (bot ? null : memberDocument.getLong("lastClaim"));
    }

    /**
     * Update the last claimed reward to the current time since unix epoch.
     */
    public void updateClaimedReward() {
        memberDocument.replace("lastClaim", System.currentTimeMillis()); // Update last claim time
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.memberId), userDocument); // Update database
    }

    /**
     * @return Returns the current daily streak of a member
     */
    public Integer getDailyStreak() {
        return (bot ? null : memberDocument.getInteger("dailyStreak"));
    }

    /**
     * @param streak The number how often a member got his daily reward in a row.
     */
    public void setDailyStreak(int streak) {
        memberDocument.replace("dailyStreak", streak); // Replace current streak
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.memberId), userDocument); // Update database
    }

    /**
     * If no custom rank background is set it will return "default".
     *
     * @return Returns a url of the current rank background.
     */
    public String getRankBackground() {
        return (bot ? null : memberDocument.getString("rankBackground"));
    }

    /**
     * @param url Image url of the new rank background.
     */
    public void setRankBackground(String url) {
        memberDocument.replace("rankBackground", url); // Replace current streak
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", this.memberId), userDocument); // Update database
    }
}
