package com.github.m5rian.myra.listeners;

import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.utilities.Webhook;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.database.guild.MongoGuild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class EasterEggs {
    private final String jtxId = "266226790458982412"; // User id from jtx
    private final List<String> sysMembers = new ArrayList<>() {{
        add(Config.MARIAN_ID); // Marian
        add("317236712440856576"); // Astri
        add("697951389707665418"); // Pokesci
    }};

    public void onMessage(MessageReceivedEvent event) {
        final String rawMessage = event.getMessage().getContentRaw(); // Get message
        final String prefix = new MongoGuild(event.getGuild()).getString("prefix"); // Get prefix

        // Chocolate
        if (rawMessage.toLowerCase().contains("i like chocolate")) {
            event.getChannel().sendMessage(Lang.lang(event).get("listener.easterEgg.chocolate")).queue();
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        }
    }
}
