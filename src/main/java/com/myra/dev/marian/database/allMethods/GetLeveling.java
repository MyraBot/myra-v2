package com.myra.dev.marian.database.allMethods;

import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.database.documents.LevelingRolesDocument;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class GetLeveling {
    // Variables
    private MongoDb mongoDb;
    private Guild guild;

    // Constructor
    public GetLeveling(MongoDb mongoDb, Guild guild) {
        this.mongoDb = mongoDb;
        this.guild = guild;
    }

    // Get leveling roles
    public List<LevelingRolesDocument> getLevelingRoles() {
        // Get guild document
        Document guildDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        // Get leveling document
        Document levelingDocument = (Document) guildDocument.get("leveling");
        // Get leveling roles
        Document levelingRoles = (Document) levelingDocument.get("roles");
        // Create list
        List<LevelingRolesDocument> roles = new ArrayList<>();
        // Add every role to List
        for (String key : levelingRoles.keySet()) {
            // Get leveling role
            Document roleDocument = (Document) levelingRoles.get(key);
            // Add Object to List
            roles.add(new LevelingRolesDocument(roleDocument));
        }
        return roles;
    }

    // Get leveling role
    public Document getLevelingRoles(String role) {
        // Get guild document
        Document guildDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        // Get leveling document
        Document levelingDocument = (Document) guildDocument.get("leveling");
        // Get leveling roles
        Document levelingRoles = (Document) levelingDocument.get("roles");
        // Return role
        return (Document) levelingRoles.get(role);
    }

    // Add leveling role
    public void addLevelingRole(int level, String roleToAdd, String roleToRemove) {
        // Get guild document
        Document guildDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        // Get leveling document
        Document levelingDocument = (Document) guildDocument.get("leveling");
        // Get roles document
        Document levelingRoles = (Document) levelingDocument.get("roles");
        // Create new document
        Document role = new Document()
                .append("level", level)
                .append("role", roleToAdd)
                .append("remove", roleToRemove);
        // Add role to leveling roles document
        levelingRoles.append(roleToAdd, role);
        // Update database
        mongoDb.getCollection("guilds").findOneAndReplace(mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(), guildDocument);
    }

    // Remove leveling role
    public void removeLevelingRole(String role) {
        // Get guild document
        Document guildDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        // Get leveling document
        Document levelingDocument = (Document) guildDocument.get("leveling");
        // Get roles document
        Document levelingRoles = (Document) levelingDocument.get("roles");
        // search for leveling role
        levelingRoles.remove(role);
        // Update database
        mongoDb.getCollection("guilds").findOneAndReplace(mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(), guildDocument);
    }

    public void checkForNewOnesOwO(int level, Member member, Guild guild) {
        List<LevelingRolesDocument> roles = getLevelingRoles();
        // check for every role
        for (LevelingRolesDocument role : roles) {
            // If ur mini poopie level is to small :c
            if (level < role.getLevel()) continue;
            // Add role :3
            guild.addRoleToMember(member, guild.getRoleById(role.getRole()));
            // Check for role to remove
            if (role.getRemove().equals("not set")) continue;
            // Remove role ._.
            guild.removeRoleFromMember(member, guild.getRoleById(role.getRole()));
        }
    }
}
