package com.myra.dev.marian.listeners.autorole;

import com.myra.dev.marian.database.allMethods.Database;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

public class AutoroleAssign  {

    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        try {
            Database db = new Database(event.getGuild());
            //get role
            String autoRole = db.getString("autoRole");
            // Check if no role is set
            if (autoRole.equals("not set")) return;
            //assign role
            event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(autoRole)).queue();
        } catch (Exception e) {
        }
    }
}
