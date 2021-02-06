package com.myra.dev.marian.listeners.logging;

import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.text.update.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Channels extends ListenerAdapter {
    //channel created
    public void onTextChannelCreate(TextChannelCreateEvent event) {
        //NSFW
        String isNSFW = "false";
        if (event.getChannel().isNSFW()) {
            isNSFW = "true";
        }
        //category
        String category = "no category";
        if (!(event.getChannel().getParent() == null)) {
            category = event.getChannel().getParent().getName();
        }

        EmbedBuilder log = new EmbedBuilder();
        log.setAuthor("│ channel created", null, event.getGuild().getIconUrl());
        log.setColor(Utilities.getUtils().blue);
        log.addField("\uD83C\uDFF7 │ name", event.getChannel().getName(), false);
        log.addField("\uD83C\uDF9F │ id", event.getChannel().getId(), false);
        if (!(event.getChannel().getTopic() == null)) {
            log.addField("topic", event.getChannel().getTopic(), false);
        }
        log.addField("\uD83D\uDD1E │ Is NSFW", isNSFW, false);
        log.addField("\uD83D\uDCC1 │ category", category, false);
        log.addField("\uD83D\uDCAC │ type", "text channel", false);

        log.setFooter(event.getChannel().getTimeCreated().getDayOfMonth() + "." + event.getChannel().getTimeCreated().getMonthValue() + "." + event.getChannel().getTimeCreated().getYear() + ", " + event.getChannel().getTimeCreated().getHour() + ":" + event.getChannel().getTimeCreated().getMinute() + ":" + event.getChannel().getTimeCreated().getSecond());


        event.getGuild().getDefaultChannel().sendMessage(log.build()).queue();
    }
    //channel deleted
    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        //NSFW
        String isNSFW = "false";
        if (event.getChannel().isNSFW()) {
            isNSFW = "true";
        }
        //category
        String category = "no category";
        if (!(event.getChannel().getParent() == null)) {
            category = event.getChannel().getParent().getName();
        }

        EmbedBuilder log = new EmbedBuilder();
        log.setAuthor("│ channel deleted", null, event.getGuild().getIconUrl());
        log.setColor(Utilities.getUtils().red);
        log.addField("\uD83C\uDFF7 │ name", event.getChannel().getName(), false);
        log.addField("\uD83C\uDF9F │ id", event.getChannel().getId(), false);
        if (!(event.getChannel().getTopic() == null)) {
            log.addField("topic", event.getChannel().getTopic(), false);
        }
        log.addField("\uD83D\uDD1E │ Is NSFW", isNSFW, false);
        log.addField("\uD83D\uDCC1 │ category", category, false);
        log.addField("\uD83D\uDCAC │ type", "text channel", false);

        log.setFooter(event.getChannel().getTimeCreated().getDayOfMonth() + "." + event.getChannel().getTimeCreated().getMonthValue() + "." + event.getChannel().getTimeCreated().getYear() + ", " + event.getChannel().getTimeCreated().getHour() + ":" + event.getChannel().getTimeCreated().getMinute() + ":" + event.getChannel().getTimeCreated().getSecond());


        event.getGuild().getDefaultChannel().sendMessage(log.build()).queue();
    }
    //name changed
    public void onTextChannelUpdateName(TextChannelUpdateNameEvent event) {
        EmbedBuilder log = new EmbedBuilder();
        log.setAuthor("│ channel renamed", null, event.getGuild().getIconUrl());
        log.setDescription("\uD83C\uDF9F │ channel id\n" + event.getChannel().getId());
        log.setColor(Utilities.getUtils().blue);
        log.addField("\uD83D\uDDD1 │ old name", event.getOldName(), false);
        log.addField("\uD83C\uDFF7 │ new name", event.getNewName(), false);
        log.setFooter(event.getChannel().getTimeCreated().getDayOfMonth() + "." + event.getChannel().getTimeCreated().getMonthValue() + "." + event.getChannel().getTimeCreated().getYear() + ", " + event.getChannel().getTimeCreated().getHour() + ":" + event.getChannel().getTimeCreated().getMinute() + ":" + event.getChannel().getTimeCreated().getSecond());

        event.getGuild().getDefaultChannel().sendMessage(log.build()).queue();
    }
    //NSFW changed
    public void onTextChannelUpdateNSFW(TextChannelUpdateNSFWEvent event) {
        String isOldNSFW = "false";
        String isNewNSFW = "false";
        if (event.getOldValue()) {
            isOldNSFW = "true";
        }
        if (event.getNewValue()) {
            isNewNSFW = "true";
        }
        EmbedBuilder log = new EmbedBuilder();
        log.setAuthor("│ NSFW changed", null, event.getGuild().getIconUrl());
        log.setDescription("\uD83C\uDF9F │ channel id\n" + event.getChannel().getId());
        log.setColor(Utilities.getUtils().blue);
        log.addField("\uD83D\uDDD1 │ old NSFW", isOldNSFW, false);
        log.addField("\uD83D\uDD1E │ new NSFW", isNewNSFW, false);
        log.setFooter(event.getChannel().getTimeCreated().getDayOfMonth() + "." + event.getChannel().getTimeCreated().getMonthValue() + "." + event.getChannel().getTimeCreated().getYear() + ", " + event.getChannel().getTimeCreated().getHour() + ":" + event.getChannel().getTimeCreated().getMinute() + ":" + event.getChannel().getTimeCreated().getSecond());

        event.getGuild().getDefaultChannel().sendMessage(log.build()).queue();
    }
    //parent changed
    public void onTextChannelUpdateParent(TextChannelUpdateParentEvent event) {
        String oldParent = "*no category*";
        if (event.getOldParent() != null) {
            oldParent = event.getOldParent().getName();
        }
        String newParent = "*no category*";
        if (event.getNewParent() != null) {
            oldParent = event.getNewParent().getName();
        }

        EmbedBuilder log = new EmbedBuilder();
        log.setAuthor("│ category changed", null, event.getGuild().getIconUrl());
        log.setDescription("\uD83C\uDF9F │ channel id\n" + event.getChannel().getId());
        log.setColor(Utilities.getUtils().blue);
        log.addField("\uD83D\uDDD1 │ old category", oldParent, false);
        log.addField("\uD83D\uDCC1 │ new category", newParent, false);
        log.setFooter(event.getChannel().getTimeCreated().getDayOfMonth() + "." + event.getChannel().getTimeCreated().getMonthValue() + "." + event.getChannel().getTimeCreated().getYear() + ", " + event.getChannel().getTimeCreated().getHour() + ":" + event.getChannel().getTimeCreated().getMinute() + ":" + event.getChannel().getTimeCreated().getSecond());

        event.getGuild().getDefaultChannel().sendMessage(log.build()).queue();
    }
    //position changed
    public void onTextChannelUpdatePosition(TextChannelUpdatePositionEvent event) {
    }
    //slowdown changed
    public void onTextChannelUpdateSlowmode(TextChannelUpdateSlowmodeEvent event) {
        EmbedBuilder log = new EmbedBuilder();
        log.setAuthor("│ category changed", null, event.getGuild().getIconUrl());
        log.setDescription("\uD83C\uDF9F │ channel id\n" + event.getChannel().getId());
        log.setColor(Utilities.getUtils().blue);
        log.addField("\uD83D\uDDD1 │ old category", event.getOldSlowmode() + "seconds", false);
        log.addField("\uD83D\uDCC1 │ new category", event.getNewSlowmode() + "seconds", false);
        log.setFooter(event.getChannel().getTimeCreated().getDayOfMonth() + "." + event.getChannel().getTimeCreated().getMonthValue() + "." + event.getChannel().getTimeCreated().getYear() + ", " + event.getChannel().getTimeCreated().getHour() + ":" + event.getChannel().getTimeCreated().getMinute() + ":" + event.getChannel().getTimeCreated().getSecond());

        event.getGuild().getDefaultChannel().sendMessage(log.build()).queue();
    }
    //topic changed
    public void onTextChannelUpdateTopic(TextChannelUpdateTopicEvent event) {
    }
}
