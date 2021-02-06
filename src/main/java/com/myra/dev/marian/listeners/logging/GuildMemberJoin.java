package com.myra.dev.marian.listeners.logging;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildMemberJoin extends ListenerAdapter {

    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        EmbedBuilder log = new EmbedBuilder();
        log.setAuthor("│ " + event.getMember().getUser().getAsTag(), null, event.getUser().getEffectiveAvatarUrl());
        log.addField("user joined" + event.getGuild().getName(), event.getMember().getUser().getName() + "joined " + event.getGuild().getName(), false);
        log.setFooter(event.getMember().getTimeJoined().getDayOfMonth() + "." + event.getMember().getTimeJoined().getMonthValue() + "." + event.getMember().getTimeJoined().getYear() + ", " + event.getMember().getTimeJoined().getHour() + ":" + event.getMember().getTimeJoined().getMinute() + ":" + event.getMember().getTimeJoined().getSecond());
    }
}
