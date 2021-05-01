package com.myra.dev.marian.listeners;

import com.myra.dev.marian.database.guild.MongoGuild;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;

import java.util.List;

public class AutoroleAssign {

    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        final Guild guild = event.getGuild(); // Get guild

        final MongoGuild db = new MongoGuild(guild); // Get database
        List<String> autoRoles = db.getList("autoRole", String.class);
        // Add all roles to member
        autoRoles.forEach(roleId -> {
            // Invalid role
            if (guild.getRoleById(roleId) == null) {
                autoRoles.remove(roleId); // Remove role
                db.setList("autoRole", autoRoles); // Update database
            }
            // Role is valid
            else {
                try {
                    final Role role = guild.getRoleById(roleId); // Get role
                    guild.addRoleToMember(event.getMember(), role).queue(); // Add role to member
                }catch (HierarchyException e) {
                    // Don't print out hierarchy exceptions
                }

            }
        });
    }
}
