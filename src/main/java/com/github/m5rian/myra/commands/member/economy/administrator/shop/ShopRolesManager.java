package com.github.m5rian.myra.commands.member.economy.administrator.shop;

import com.github.m5rian.myra.database.MongoDb;
import com.github.m5rian.myra.database.guild.ShopRolesDocument;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class ShopRolesManager {
    private final static ShopRolesManager INSTANCE = new ShopRolesManager(); // Instance

    public static ShopRolesManager getInstance() {
        return INSTANCE; // Return instance
    }

    private final MongoDb db = MongoDb.getInstance(); // Get database

    public void addRole(Guild guild, String roleId, Integer price) {
        final Document guildDocument = db.getCollection("guilds").find(eq("guildId", guild.getId())).first(); // Get guild document
        final Document economy = (Document) guildDocument.get("economy"); // Get economy document
        final Document shop = (Document) economy.get("shop"); // Get shop document

        // Create a new role document
        Document role = new Document();
        role.put("id", roleId);
        role.put("price", price);

        // Add role to shop
        shop.put(roleId, role); // Add role to the shop document
        db.getCollection("guilds").findOneAndReplace(eq("guildId", guild.getId()), guildDocument); // Get guild document
    }

    public void removeRole(Guild guild, String roleId) {
        final Document guildDocument = db.getCollection("guilds").find(eq("guildId", guild.getId())).first(); // Get guild document
        final Document economy = (Document) guildDocument.get("economy"); // Get economy document
        final Document shop = (Document) economy.get("shop"); // Get shop document

        shop.remove(roleId); // Remove role from shop

        db.getCollection("guilds").findOneAndReplace(eq("guildId", guild.getId()), guildDocument); // Update guild document
    }

    public List<ShopRolesDocument> getRoles(Guild guild) {
        final Document guildDocument = db.getCollection("guilds").find(eq("guildId", guild.getId())).first(); // Get guild document
        final Document economy = (Document) guildDocument.get("economy"); // Get economy document
        final Document shop = (Document) economy.get("shop"); // Get shop document

        List<ShopRolesDocument> roles = new ArrayList<>(); // Create a list

        // Add every role to the list
        for (String key : shop.keySet()) {
            final Document role = (Document) shop.get(key); // Get role document
            roles.add(new ShopRolesDocument(role)); // Add Role Object to list
        }
        Collections.sort(roles, Comparator.comparing(ShopRolesDocument::getPrice).reversed()); // Sort list
        return roles; // Return ordered list
    }
}