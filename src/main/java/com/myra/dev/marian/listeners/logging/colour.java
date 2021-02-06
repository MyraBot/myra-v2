package com.myra.dev.marian.listeners.logging;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.role.update.RoleUpdateColorEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class colour extends ListenerAdapter {
    public void onRoleUpdateColor(RoleUpdateColorEvent event) {
        EmbedBuilder log = new EmbedBuilder();
        log.setAuthor("role updated", null, event.getGuild().getIconUrl());
        log.addField("role: " + event.getRole().getName() + "\nid: " + event.getRole().getId(), "", false);
        log.addField("**colour updated**", "```old value:" + Integer.toHexString(event.getOldColorRaw()) + "\nnew value " +  Integer.toHexString(event.getNewColorRaw()) + "```", false);


        event.getGuild().getDefaultChannel().sendMessage(log.build()).queue();
    }
}
