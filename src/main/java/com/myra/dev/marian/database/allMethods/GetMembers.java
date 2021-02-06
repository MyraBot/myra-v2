package com.myra.dev.marian.database.allMethods;

import com.mongodb.client.MongoCursor;
import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.database.documents.MemberDocument;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.mongodb.client.model.Filters.exists;

public class GetMembers {
    //variables
    private MongoDb mongoDb;
    private Guild guild;

    //constructor
    public GetMembers(MongoDb mongoDb, Guild guild) {
        this.mongoDb = mongoDb;
        this.guild = guild;
    }

    /**
     * methods
     */
    //get member
    public GetMember getMember(Member member) {
        return new GetMember(mongoDb, guild, member);
    }

    //get sorted members
    public List<MemberDocument> getLeaderboard(LeaderboardType type) {
        //create leaderboard
        List<MemberDocument> leaderboard = new ArrayList<>();

        MongoCursor<Document> iterator = mongoDb.getCollection("users").find(exists(guild.getId())).iterator(); // Create an iterator of all members, who are in the guild
        while (iterator.hasNext()) {
            final Document document = iterator.next(); // Get next user document
            leaderboard.add(new MemberDocument(document, guild));  // Add member to leaderboard
        }
        iterator.close(); // Close iterator

        // Sort leaderboard
        if (type.equals(LeaderboardType.LEVEL)) { // Sort by level
            Collections.sort(leaderboard, Comparator.comparing(MemberDocument::getXp).reversed()); // Sort list
        }
        else if (type.equals(LeaderboardType.BALANCE)) { // Sort list by balance
            Collections.sort(leaderboard, Comparator.comparing(MemberDocument::getBalance).reversed()); // Sort list
        }

        return leaderboard; // Return leaderboard
    }
}
