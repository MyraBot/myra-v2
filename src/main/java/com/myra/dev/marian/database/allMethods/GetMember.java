package com.myra.dev.marian.database.allMethods;

import com.mongodb.client.MongoCursor;
import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.database.MongoDbDocuments;
import com.myra.dev.marian.database.documents.MemberDocument;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bson.Document;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;

public class GetMember {
    //variables
    private MongoDb mongoDb;
    private Guild guild;
    private Member member;
    private Document userDocument;
    private Document memberDocument;

    //constructor
    public GetMember(MongoDb mongoDb, Guild guild, Member member) {
        if (member.getUser().isBot()) return;
        this.mongoDb = mongoDb;
        this.guild = guild;
        this.member = member;
        // Member hasn't a document
        if (mongoDb.getCollection("users").find(eq("userId", member.getId())).first() == null) {
            final Document userDocument = MongoDbDocuments.createUserDocument(member.getUser()); // Create document for user
            mongoDb.getCollection("users").insertOne(userDocument); // Insert document
        }
        this.userDocument = mongoDb.getCollection("users").find(eq("userId", member.getId())).first(); // Get user document
        // Member hasn't a guild document
        if (userDocument.get(guild.getId()) == null) {
            // Creating new guild document
            final Document guildMemberDocument = MongoDbDocuments.createGuildMemberDocument(member); // Create document for guild
            userDocument.put(guild.getId(), guildMemberDocument); // Add document for guild to user document
            mongoDb.getCollection("users").findOneAndReplace(eq("userId", member.getId()), userDocument); // Update database
        }
        this.memberDocument = (Document) userDocument.get(guild.getId()); // Get the guild document of the member
    }

    //get xp
    public int getXp() {
        return memberDocument.getInteger("xp");
    }

    // Add experience
    public void addXp(int xpToAdd) {
        memberDocument.replace("xp", memberDocument.getInteger("xp") + xpToAdd); // Add xp
        mongoDb.getCollection("users").findOneAndReplace(mongoDb.getCollection("users").find(eq("userId", member.getId())).first(), userDocument); // Update database
    }

    //get level
    public int getLevel() {
        return memberDocument.getInteger("level");
    }

    // Set level
    public void setLevel(int level) {
        //update level
        memberDocument.replace("level", level);
        //update xp
        double xp;
        //parabola
        double squaredNumber = Math.pow(level, 2);
        double exactXp = squaredNumber * 5;
        //round off
        DecimalFormat f = new DecimalFormat("###");
        xp = Double.parseDouble(f.format(exactXp));
        //round down number
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
        //convert to int and remove the '.0'
        int newXp = Integer.parseInt(String.valueOf(xp).replace(".0", ""));
        //replace xp
        memberDocument.replace("xp", newXp);
        //update Document
        mongoDb.getCollection("users").findOneAndReplace(mongoDb.getCollection("users").find(eq("userId", member.getId())).first(), userDocument); // Update database
    }

    //get rank
    public int getRank() {
        List<MemberDocument> leaderboard = new ArrayList<>(); // Create leaderboard

        final MongoCursor<Document> iterator = mongoDb.getCollection("users").find(exists(guild.getId())).iterator(); // Create an iterator of all members, who are in the guild
        while (iterator.hasNext()) {
            final Document document = iterator.next(); // Get next user document
            leaderboard.add(new MemberDocument(document, guild));  // Add member to leaderboard
        }
        iterator.close(); // Close the iterator

        Collections.sort(leaderboard, Comparator.comparing(MemberDocument::getXp).reversed());   // Sort list
        // Get rank
        int rank = 0;
        // Search for member
        for (MemberDocument memberDocument : leaderboard) {
            if (memberDocument.getId().equals(member.getId())) {
                rank = leaderboard.indexOf(memberDocument) + 1;
                break;
            }
        }
        // Return rank
        return rank;
    }

    //get balance
    public int getBalance() {
        return memberDocument.getInteger("balance");
    }

    // Set balance
    public void setBalance(int balance) {
        //replace balance
        memberDocument.replace("balance", balance);
        //update Document
        mongoDb.getCollection("users").findOneAndReplace(mongoDb.getCollection("users").find(eq("userId", member.getId())).first(), userDocument); // Update database
    }

    // Get last claimed reward
    public long getLastClaim() {
        return memberDocument.getLong("lastClaim");
    }

    // Update last claimed reward
    public void updateClaimedReward() {
        // Replace 'lastClaim'
        memberDocument.replace("lastClaim", System.currentTimeMillis());
        // Update guild document
        mongoDb.getCollection("users").findOneAndReplace(mongoDb.getCollection("users").find(eq("userId", member.getId())).first(), userDocument); // Update database
    }

    // Update last claimed reward
    public void setDailyStreak(int newStreak) {
        // Replace 'dailyStreak' with new value
        memberDocument.replace("dailyStreak", newStreak);
        // Update guild document
        mongoDb.getCollection("users").findOneAndReplace(mongoDb.getCollection("users").find(eq("userId", member.getId())).first(), userDocument); // Update database
    }

    public String getString(String key) {
        return memberDocument.getString(key);
    }

    public void setString(String key, String value) {
        memberDocument.replace(key, value); // Replace value
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", member.getId()), userDocument); // Update database
    }

    public Integer getInteger(String key) {
        return memberDocument.getInteger(key);
    }

    public void setInteger(String key, Integer value) {
        memberDocument.replace(key, value); // Replace value
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", member.getId()), userDocument); // Update database
    }

    public Long getLong(String key) {
        return memberDocument.getLong(key);
    }

    public void setLong(String key, Long value) {
        memberDocument.replace(key, value); // Replace value
        mongoDb.getCollection("users").findOneAndReplace(eq("userId", member.getId()), userDocument); // Update database
    }
}
