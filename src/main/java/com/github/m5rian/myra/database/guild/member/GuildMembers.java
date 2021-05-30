package com.github.m5rian.myra.database.guild.member;

import com.github.m5rian.myra.database.MongoDb;
import com.github.m5rian.myra.database.guild.LeaderboardType;
import com.mongodb.client.MongoCursor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.mongodb.client.model.Filters.exists;

public class GuildMembers {
    // Variables
    private final MongoDb mongoDb;
    private final Guild guild;

    /**
     * @param mongoDb A {@link MongoDb} instance.
     * @param guild
     */
    public GuildMembers(MongoDb mongoDb, Guild guild) {
        this.mongoDb = mongoDb;
        this.guild = guild;
    }

    /**
     * Get a document a {@link GuildMembers#guild} member.
     *
     * @param member The member you want to get the document from.
     * @return Returns a {@link GuildMember} object.
     */
    public GuildMember getMember(Member member) {
        return new GuildMember(mongoDb, guild, member);
    }

    /**
     * @param type The property to sort the list.
     * @return Returns a list of member documents sorted by the type.
     */
    public List<LeaderboardMember> getLeaderboard(LeaderboardType type) {
        //create leaderboard
        List<LeaderboardMember> leaderboard = new ArrayList<>();

        MongoCursor<Document> iterator = mongoDb.getCollection("users").find(exists(guild.getId())).iterator(); // Create an iterator of all members, who are in the guild
        while (iterator.hasNext()) {
            final Document document = iterator.next(); // Get next user document
            leaderboard.add(new LeaderboardMember(document, guild));  // Add member to leaderboard
        }
        iterator.close(); // Close iterator

        // Sort leaderboard
        if (type.equals(LeaderboardType.LEVEL)) { // Sort by level
            leaderboard.sort(Comparator.comparing(LeaderboardMember::getXp).reversed()); // Sort list
        }
        else if (type.equals(LeaderboardType.BALANCE)) { // Sort list by balance
            leaderboard.sort(Comparator.comparing(LeaderboardMember::getBalance).reversed()); // Sort list
        }
        else if (type.equals(LeaderboardType.VOICE)) { // Sort list by voice call time
            leaderboard.sort(Comparator.comparing(LeaderboardMember::getVoiceCallTime).reversed()); // Sort list
        }

        return leaderboard; // Return leaderboard
    }
}
