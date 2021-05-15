package com.myra.dev.marian.listeners;

import com.myra.dev.marian.DiscordBot;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Random;

public class Someone {

    public void onMessage(MessageReceivedEvent event) throws Exception {
        if (!event.getMessage().getContentRaw().contains("@someone")) return;

        List<Permission> permissions = DiscordBot.COMMAND_SERVICE.getPermission(Administrator.class).getPermissions(); // Get administrator permissions
        if (!event.getMember().getPermissions().containsAll(permissions)) return;

        // Get random number
        int number = new Random().nextInt(event.getGuild().getMembers().size());
        // Get random member
        event.getGuild().loadMembers()
                .onSuccess(members -> {
                            String message = event.getMessage().getContentRaw().replace("@someone", members.get(number).getAsMention());
                            event.getChannel().deleteMessageById(event.getChannel().getLatestMessageIdLong()).queue();
                            event.getChannel().sendMessage(message).queue();
                        }
                );
    }
}
