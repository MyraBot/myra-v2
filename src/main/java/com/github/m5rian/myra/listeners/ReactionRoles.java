package com.github.m5rian.myra.listeners;

import com.github.m5rian.myra.database.MongoDb;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class ReactionRoles {

    public void reactionRoleAssign(GuildMessageReactionAddEvent event) {
        final Document guildDocument = MongoDb.getInstance().getCollection("guilds").find(eq("guildId", event.getGuild().getId())).first(); // Get guild document
        final List<Document> rr = guildDocument.getList("reactionRoles", Document.class); // Get reaction roles list

        if (rr.stream().noneMatch(document -> document.getString("message").equals(event.getMessageId())))
            return; // Message isn't a reaction role
        if (rr.stream().noneMatch(document -> document.getString("emoji").equals(event.getReactionEmote().getEmoji())))
            return; // Emoji isn't a reaction role emoji

        final List<Document> boundReactionRoles = new ArrayList<>(); // Create list for all reaction roles which are bound to the message
        Document reactionRole = null; // Create variable to store reaction role document
        for (Document doc : rr) {
            if (!doc.getString("message").equals(event.getMessageId()))
                continue; // Reaction role isn't bound to message
            boundReactionRoles.add(doc); // Add reaction role

            // Search for reaction role
            if (doc.getString("emoji").equals(event.getReactionEmote().getEmoji())) { // Reacted emoji is used in this reaction role
                reactionRole = doc; // Save reaction role
            }
        }
        if (reactionRole == null) return; // No reaction role found

        // Get role
        if (event.getGuild().getRoleById(reactionRole.getString("role")) == null) { // Role doesn't exist anymore
            rr.remove(reactionRole); // Remove reaction role
            return;
        }

        final Role role = event.getGuild().getRoleById(reactionRole.getString("role")); // Get role
        final Member member = event.getMember(); // Get member
        final Guild guild = event.getGuild(); // Get guild

        switch (reactionRole.getString("type")) {
            // Normal reaction
            case "normal":
                // Verify reaction
            case "verify":
                guild.addRoleToMember(member, role).queue(); // Add role to member
                break;

            // Unique reaction
            case "unique":
                event.retrieveMessage().queue(message -> message.getReactions().forEach(reaction -> { // For each reaction
                    if (boundReactionRoles.stream().anyMatch(boundRr -> boundRr.getString("emoji").equals(reaction.getReactionEmote().getEmoji()))) { // Reaction is a reaction role
                        // Retrieve reacted users
                        reaction.retrieveUsers().queue(users -> {
                            if (users.stream().anyMatch(user -> user.equals(event.getUser()))) { // User reacted to the reaction
                                boundReactionRoles.forEach(document -> { // Search for right reaction role
                                    // Wrong reaction role
                                    if (document.getString("message").equals(message.getId()) && !document.getString("emoji").equals(event.getReactionEmote().getEmoji())) {
                                        message.removeReaction(document.getString("emoji"), event.getUser()).queue(); // Remove reaction of user
                                        final Role rrRemove = event.getGuild().getRoleById(document.getString("role")); // Get reaction role
                                        event.getGuild().removeRoleFromMember(member, rrRemove).queue(); // Remove role from member
                                    }
                                    // Right reaction role
                                    if (document.getString("message").equals(message.getId()) && document.getString("emoji").equals(event.getReactionEmote().getEmoji())) {
                                        final Role rrAdd = event.getGuild().getRoleById(document.getString("role")); // Get reaction role
                                        event.getGuild().addRoleToMember(member, rrAdd).queue(); // Remove role from member
                                    }
                                });
                            }
                        });
                    }
                }));
                break;
        }

    }

    public void reactionRoleRemove(GuildMessageReactionRemoveEvent event) {
        final Document guildDocument = MongoDb.getInstance().getCollection("guilds").find(eq("guildId", event.getGuild().getId())).first(); // Get guild document
        final List<Document> rr = guildDocument.getList("reactionRoles", Document.class); // Get reaction roles list

        if (rr.stream().noneMatch(document -> document.getString("message").equals(event.getMessageId())))
            return; // Message isn't a reaction role
        if (rr.stream().noneMatch(document -> document.getString("emoji").equals(event.getReactionEmote().getEmoji())))
            return; // Emoji isn't a reaction role emoji

        Document reactionRole = null; // Create variable to store reaction role document
        for (Document doc : rr) {
            // Search for reaction role
            if (doc.getString("emoji").equals(event.getReactionEmote().getEmoji())) { // Reacted emoji is used in this reaction role
                reactionRole = doc; // Save reaction role
            }
        }
        if (reactionRole == null) return; // No reaction role found

        // Get role
        if (event.getGuild().getRoleById(reactionRole.getString("role")) == null) { // Role doesn't exist anymore
            rr.remove(reactionRole); // Remove reaction role
            return;
        }

        final Role role = event.getGuild().getRoleById(reactionRole.getString("role")); // Get role
        final Member member = event.getMember(); // Get member
        if (member == null) return;
        final Guild guild = event.getGuild(); // Get guild

        // Remove role if type is not verify
        if (!reactionRole.getString("type").equals("verify")) {
            guild.removeRoleFromMember(member, role).queue(); // Remove role from member
        }

    }
}