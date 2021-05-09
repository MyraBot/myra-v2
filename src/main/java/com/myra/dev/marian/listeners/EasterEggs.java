package com.myra.dev.marian.listeners;

import com.myra.dev.marian.Config;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.Webhook;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EasterEggs {
    private final String jtxId = "266226790458982412"; // User id from jtx
    private final List<String> sysMembers = new ArrayList<>() {{
        add(Config.marian); // Marian
        add("317236712440856576"); // Astri
        add("697951389707665418"); // Pokesci
    }};

    public void onMessage(MessageReceivedEvent event) {
        final String rawMessage = event.getMessage().getContentRaw(); // Get message
        final String prefix = new MongoGuild(event.getGuild()).getString("prefix"); // Get prefix

        // Chocolate
        if (rawMessage.toLowerCase().contains("i like chocolate")) {
            event.getChannel().sendMessage(String.format("I like chocolate too" + "%n(\\\\__/)" + "%n( • - • )" + "%n/っ \uD83C\uDF6B")).queue();
        }

        // Guild events
        if (!event.isFromGuild()) return;
        final TextChannel channel = (TextChannel) event.getChannel(); // Get channel
        // Np
        if (rawMessage.equalsIgnoreCase(prefix + "np") && sysMembers.contains(event.getAuthor().getId())) {
            event.getJDA().retrieveUserById(jtxId).queue(jtx -> { // Retrieve jtx
                channel.createWebhook("jtx").queue(webhook -> { // Create webhook
                    try {
                        new Webhook(webhook.getUrl()) // Create new webhook message
                                .setUsername("jtx") // Set username
                                .setAvatarUrl(jtx.getEffectiveAvatarUrl()) // Set avatar url
                                .setContent("np").send(); // Send webhook
                        webhook.delete().queue(); // Delete webhook
                        event.getMessage().delete().queue(); // Delete message
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                });
            });
        }
    }
}
