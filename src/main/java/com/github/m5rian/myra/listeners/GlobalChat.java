package com.github.m5rian.myra.listeners;

import com.github.m5rian.myra.database.MongoDb;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.Webhook;
import com.github.m5rian.myra.database.guild.MongoGuild;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import okhttp3.Request;
import okhttp3.Response;
import org.bson.Document;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.not;

public class GlobalChat {

    private final static List<Message> messages = new ArrayList<>();

    public void onMessage(MessageReceivedEvent event) throws Exception {
        if (!event.isFromGuild()) return;

        final String guildWebhookUrl = new MongoGuild(event.getGuild()).getString("globalChat"); // Get global chat webhook url
        if (guildWebhookUrl == null) return;

        final WebhookInformation guildWebhookInformation = new WebhookInformation(guildWebhookUrl); // Get webhook information
        // Invalid webhook
        if (!guildWebhookInformation.isValid) {
            new MongoGuild(event.getGuild()).setString("globalChat", null); // Remove global chat
            return;
        }
        if (!guildWebhookInformation.getChannelId().equals(event.getChannel().getId())) return; // Wrong channel

        if (messages.size() >= 15) messages.remove(0); // Remove first stored message
        messages.add(event.getMessage()); // Add new message

        for (Document document : MongoDb.getInstance().getCollection("guilds").find(not(eq("globalChat", null)))) {
            final String url = document.getString("globalChat"); // Get global chat webhook url
            if (url == null) continue; // No global chat set

            final Guild guild = event.getJDA().getGuildById(document.getString("guildId")); // Get guild
            if (event.getGuild() == guild) continue; // Same guild as author guild

            final User user = event.getAuthor(); // Get author
            // Create webhook message
            Webhook message = new Webhook(url);
            message.setUsername(user.getName());
            message.setAvatarUrl(user.getEffectiveAvatarUrl());

            // Message is a reply
            if (event.getMessage().getReferencedMessage() != null) {
                final Message reply = event.getMessage().getReferencedMessage(); // Get message to reply
                message.setContent("> " + reply.getContentRaw() + "\n"); // In JSON \n is \\n
            }
            message.appendContent(event.getMessage().getContentRaw()); // Add message
            // Message has an attachments
            if (!event.getMessage().getAttachments().isEmpty()) {
                message.addAttachment(event.getMessage().getAttachments().get(0));
            }
            message.send(); // Send message
        }
    }

    public void messageEdited(GuildMessageUpdateEvent event) throws IOException {
        final String guildWebhookUrl = new MongoGuild(event.getGuild()).getString("globalChat"); // Get global chat webhook url
        if (guildWebhookUrl == null) return;

        final WebhookInformation guildWebhookInformation = new WebhookInformation(guildWebhookUrl); // Get webhook information
        if (!guildWebhookInformation.getChannelId().equals(event.getChannel().getId())) return; // Wrong channel

        // Message is still in range to edit
        if (messages.contains(event.getMessage())) {
            for (Document document : MongoDb.getInstance().getCollection("guilds").find(not(eq("globalChat", null)))) {
                final String url = document.getString("globalChat"); // Get global chat webhook url
                if (url == null) continue; // No global chat set

                final Guild guild = event.getJDA().getGuildById(document.getString("guildId")); // Get guild
                if (event.getGuild() == guild) continue; // Same guild as author guild

                final WebhookInformation webhookInformation = new WebhookInformation(url); // Get webhook information
                final TextChannel channel = guild.getTextChannelById(webhookInformation.getChannelId()); // Get global chat

                channel.getHistory().retrievePast(15).queue(history -> { // Retrieve last 15 messages
                    history.forEach(historyMessage -> { // Check every message
                        messages.forEach(originalMessage -> {
                            if (historyMessage.getContentRaw().equals(originalMessage.getContentRaw()) && historyMessage.getAuthor().getName().equals(originalMessage.getAuthor().getName())) {
                                Webhook webhook = new Webhook(url);
                                webhook.setContent(event.getMessage().getContentRaw());
                                try {
                                    webhook.edit(historyMessage.getId());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    });
                });
            }
        }
    }

    private static class WebhookInformation {
        private final String url;
        private final JSONObject response;
        private boolean isValid = true;

        private WebhookInformation(String url) throws IOException {
            this.url = url;

            final Request webhookRequest = new Request.Builder()
                    .url(url) // Get information about the webhook
                    .build();
            try (Response response = Utilities.HTTP_CLIENT.newCall(webhookRequest).execute()) {
                final JSONObject json = new JSONObject(response.body().string()); // Create json object

                // Webhook isn't valid
                if (json.has("message")) {
                    if (json.getString("message").equals("Unknown Webhook")) this.isValid = false;
                }
                this.response = json; // Get information as a json object
            }
        }

        private boolean isValid() {
            return this.isValid;
        }

        private String getUrl() {
            return url;
        }

        private String getGuildId() {
            return response.getString("guild_id");
        }

        private String getChannelId() {
            return response.getString("channel_id");
        }
    }
}
