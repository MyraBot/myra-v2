package com.github.m5rian.myra.database.guild.member;

import com.github.m5rian.myra.database.MongoDb;
import com.github.m5rian.myra.database.guild.LeaderboardType;
import com.mongodb.client.MongoCursor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.mongodb.client.model.Filters.exists;

public class GuildMembers {
    // Variables
    private final MongoDb mongoDb = MongoDb.getInstance();
    private final JDA jda;
    private final String guildId;

    /**
     * @param jda     A {@link JDA} Object.
     * @param guildId The ID of the current guild.
     */
    public GuildMembers(JDA jda, String guildId) {
        this.jda = jda;
        this.guildId = guildId;
    }

    /**
     * @param member The member you want to get the document from.
     * @return Returns a {@link GuildMember} object.
     */
    public GuildMember getMember(Member member) {
        return GuildMember.get(this.guildId, member.getId());
    }

    /**
     * @param memberId The member id you want to get the document from.
     * @return Returns a {@link GuildMember} object.
     */
    public GuildMember getMember(String memberId) {
        Member member = null;
        try {
            member = this.jda.getGuildById(this.guildId).retrieveMemberById(memberId).complete(); // Retrieve member
        } catch (Exception ignored) {

        }
        return GuildMember.get(this.guildId, memberId);
    }

    /**
     * @param type The property to sort the list.
     * @return Returns a list of member documents sorted by the type.
     */
    public List<LeaderboardMember> getLeaderboard(LeaderboardType type) {
        final List<LeaderboardMember> leaderboard = new ArrayList<>(); // Create leaderboard list

        final MongoCursor<Document> iterator = mongoDb.getCollection("users").find(exists(this.guildId)).iterator(); // Create an iterator of all members, who are in the guild
        while (iterator.hasNext()) {
            final Document userDocument = iterator.next(); // Get next user document
            leaderboard.add(new LeaderboardMember(userDocument, guildId));  // Add member to leaderboard
        }
        iterator.close(); // Close iterator

        // Sort leaderboard
        if (type.equals(LeaderboardType.LEVEL)) { // Sort by level
            leaderboard.sort(Comparator.comparing(LeaderboardMember::getXp).reversed()); // Sort list
        } else if (type.equals(LeaderboardType.BALANCE)) { // Sort list by balance
            leaderboard.sort(Comparator.comparing(LeaderboardMember::getBalance).reversed()); // Sort list
        } else if (type.equals(LeaderboardType.VOICE)) { // Sort list by voice call time
            leaderboard.sort(Comparator.comparing(LeaderboardMember::getVoiceCallTime).reversed()); // Sort list
        }

        return leaderboard; // Return leaderboard
    }
}
