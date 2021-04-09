package com.myra.dev.marian.database.guild;

import com.myra.dev.marian.database.MongoDb;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class GuildLeveling {
    // Variables
    private MongoDb mongoDb;
    private Guild guild;

    /**
     * @param mongoDb A {@link MongoDb} instance.
     * @param guild   The guild of the current document.
     */
    public GuildLeveling(MongoDb mongoDb, Guild guild) {
        this.mongoDb = mongoDb;
        this.guild = guild;
    }

    /**
     * The returned list is unordered. Roles which doesn't exist anymore,
     * won't be removed and still remain in the returned list.
     *
     * @return Returns a list with all leveling roles from a guild.
     */
    public List<LevelingRole> getLevelingRoles() {
        final Document guildDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(); // Get guild document
        final Document levelingDocument = guildDocument.get("leveling", Document.class); // Get leveling document
        final Document levelingRoles = levelingDocument.get("roles", Document.class); // Get leveling roles

        List<LevelingRole> roles = new ArrayList<>(); // Create list
        // Add every role to List
        for (String key : levelingRoles.keySet()) {
            final Document roleDocument = (Document) levelingRoles.get(key); // Get leveling role
            roles.add(new LevelingRole(roleDocument)); // Add leveling role to List
        }
        return roles; // Return list
    }

    /**
     * @param role The role to search for a leveling role.
     * @return Returns a {@link Document} based on the provided  role.
     */
    public Document getLevelingRole(String role) {
        final Document guildDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(); // Get guild document
        final Document levelingDocument = guildDocument.get("leveling", Document.class); // Get leveling document
        final Document levelingRoles = levelingDocument.get("roles", Document.class); // Get leveling roles
        return levelingRoles.get(role, Document.class); // Return role
    }

    /**
     * Add a leveling role
     *
     * @param level The level when the role should get added.
     * @param role  The role which is assigned, once a member reaches {@param level}.
     */
    public void addLevelingRole(int level, Role role) {
        final Document guildDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(); // Get guild document
        final Document levelingDocument = guildDocument.get("leveling", Document.class); // Get leveling document
        final Document levelingRoles = levelingDocument.get("roles", Document.class); // Get roles document
        // Create new document
        final Document roleDocument = new Document()
                .append("level", level) // Add level
                .append("role", role.getId()); // Add role id
        levelingRoles.append(role.getId(), roleDocument); // Add role to leveling roles document

        mongoDb.getCollection("guilds").findOneAndReplace(eq("guildId", guild.getId()), guildDocument); // Update database
    }

    /**
     * Remove a leveling role.
     *
     * @param role The role to remove.
     */
    public void removeLevelingRole(Role role) {
        final Document guildDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(); // Get guild document
        final Document levelingDocument = guildDocument.get("leveling", Document.class); // Get leveling document
        final Document levelingRoles = levelingDocument.get("roles", Document.class); // Get roles document
        levelingRoles.remove(role.getId()); // search for leveling role
        mongoDb.getCollection("guilds").findOneAndReplace(eq("guildId", guild.getId()), guildDocument); // Update database
    }
}
