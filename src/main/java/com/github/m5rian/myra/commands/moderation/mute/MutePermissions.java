package com.github.m5rian.myra.commands.moderation.mute;

import com.github.m5rian.myra.database.guild.MongoGuild;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;

import java.util.EnumSet;

public class MutePermissions {

    public void textChannelCreateEvent(TextChannelCreateEvent event) {
        // Bot doesn't have MANAGE CHANNEL permission
        if (event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MANAGE_CHANNEL)) return;

        final String roleId = new MongoGuild(event.getGuild()).getString("muteRole");
        if (roleId.equals("not set")) return;
        final Role muteRole = event.getGuild().getRoleById(roleId);
        if (muteRole == null) return;

        event.getChannel().getManager().putPermissionOverride(muteRole, null, EnumSet.of(Permission.MESSAGE_WRITE)).queue();
    }
}
