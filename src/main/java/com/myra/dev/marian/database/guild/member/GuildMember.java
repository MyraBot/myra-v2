package com.myra.dev.marian.database.guild.member;

import com.mongodb.client.MongoCursor;
import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.database.MongoDocuments;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;

public class GuildMember {
    // Variables
    private MongoDb mongoDb; // A mongodb instance
    private Guild guild; // The guild of the member
    private Member member; // The member itself
    private Document userDocument; // The document of the user
    private Document memberDocument; // The guild specific document of the member
    private boolean bot = false; // Is the member a bot?

    /**
     * This method searches for the {@link GuildMember#userDocument} and the {@link GuildMember#memberDocument}.
     * If there are no documents, it will create the missing ones.
     * Also the member gets checked on {@link User#isBot()}.
     *
     * @param mongoDb A {@link MongoDb} instance.
     * @param guild   The Guild.
     * @param member  The member itself.
     */
    public GuildMember(MongoDb mongoDb, Guild guild, Member member) {
        // Member is bot
        if (member.getUser().isBot()) {
            this.bot = true;
            return;
        }

        this.mongoDb = mongoDb;
        this.guild = guild;
        this.member = member;

        // Member hasn't a user document
        if (mongoDb.getCollection("users").find(eq("userId", member.getId())).first() == null) {
            final Document userDocument = MongoDocuments.createUserDocument(member.getUser()); // Create document for user
            mongoDb.getCollection("users").insertOne(userDocument); // Insert document
        }
        this.userDocument = mongoDb.getCollection("users").find(eq("userId", member.getId())).first(); // Get user document

        // Member hasn't a guild document
        if (userDocument.get(guild.getId()) == null) {
            final Document guildMemberDocument = MongoDocuments.createGuildMemberDocument(member); // Create document for guild
            userDocument.put(guild.getId(), guildMemberDocument); // Add document for guild to user document
            mongoDb.getCollection("users").findOneAndReplace(eq("userId", member.getId()), userDocument); // Update database
        }
        this.memberDocument = userDocument.get(guild.getId(), Document.class); // Get the guild document of the member
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
    public Integer getXp() {
        return (bot ? null : memberDocument.getInteger("xp"));
    }

    /**
     * @param xp Experience you want the member to have.
     */
    public void setXp(long xp) {
        if (bot) return; // Member is bot

        memberDocument.replace("xp", xp); // Set xp
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", member.getId()), userDocument); // Update database
    }

    /**
     * Add a certain amount of experience points to a member.
     *
     * @param xpToAdd Experience to add.
     */
    public void addXp(int xpToAdd) {
        if (bot) return; // Member is bot

        memberDocument.replace("xp", memberDocument.getInteger("xp") + xpToAdd); // Add xp
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", member.getId()), userDocument); // Update database
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
        double xp;
        // Parabola
        double squaredNumber = Math.pow(level, 2);
        double exactXp = squaredNumber * 5;
        // Round off
        DecimalFormat f = new DecimalFormat("###");
        xp = Double.parseDouble(f.format(exactXp));
        // Round down number
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);

        int newXp = Integer.parseInt(String.valueOf(xp).replace(".0", "")); // Convert to int and remove the '.0'
        memberDocument.replace("xp", newXp); // Replace xp
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", member.getId()), userDocument); // Update database
    }

    /**
     * Get the rank of a member.
     *
     * @return Returns the placement of a members level.
     */
    public int getRank() {
        List<LeaderboardMember> leaderboard = new ArrayList<>(); // Create leaderboard

        final MongoCursor<Document> iterator = mongoDb.getCollection("users").find(exists(guild.getId())).iterator(); // Create an iterator of all members, who are in the guild
        while (iterator.hasNext()) {
            final Document document = iterator.next(); // Get next user document
            leaderboard.add(new LeaderboardMember(document, guild));  // Add member to leaderboard
        }
        iterator.close(); // Close the iterator

        leaderboard.sort(Comparator.comparing(LeaderboardMember::getXp).reversed()); // Sort list

        // Get rank
        int rank = 0;
        // Search for member
        for (LeaderboardMember memberDocument : leaderboard) {
            if (memberDocument.getId().equals(member.getId())) {
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
        return (bot ? null : memberDocument.getLong("voiceCallTime"));
    }

    /**
     * @param millis The amount of time a member spent in voice calls.
     */
    public void setVoiceTime(long millis) {
        memberDocument.replace("voiceCallTime", millis); // Replace current time
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", member.getId()), userDocument); // Update database
    }

    /**
     * @return Returns the amount of messages a member wrote.
     */
    public Integer getMessages() {
        return (bot ? null : memberDocument.getInteger("messages"));
    }

    /**
     * Add one message to the total amount of written messages.
     */
    public void addMessage() {
        memberDocument.replace("messages", memberDocument.getInteger("messages") + 1); // Add one message
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", member.getId()), userDocument); // Update database
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
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", member.getId()), userDocument); // Update database
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
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", member.getId()), userDocument); // Update database
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
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", member.getId()), userDocument); // Update database
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
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", member.getId()), userDocument); // Update database
    }
}
