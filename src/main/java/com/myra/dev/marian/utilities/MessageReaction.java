package com.myra.dev.marian.utilities;


import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MessageReaction {
    //                     Guild           Command      Message id   Information
    public static HashMap<String, HashMap<String, HashMap<String, Document>>> reactions = new HashMap();

    public static void add(Guild guild, String command, Message message, User author, boolean timeOut, String... emojis) {
        // Get variables
        final String guildId = guild.getId(); // Get guild id

        // Guild isn't in the hashmap yet
        if (!reactions.containsKey(guildId)) {
            reactions.put(guildId, new HashMap<>()); // Add guild to reactions hashmap
        }

        // Command isn't in the hashmap yet
        if (!reactions.get(guildId).containsKey(command)) {
            reactions.get(guildId).put(command, new HashMap<>()); // Add command to the hashmap of the guild
        }

        List<String> emojiList = new ArrayList<>(Arrays.asList(emojis));
        // Create new Document for command
        Document document = new Document()
                .append("messageId", message.getId())
                .append("userId", author.getId())
                .append("emojis", emojiList);
        reactions.get(guildId).get(command).put(message.getId(), document); // Add document to hashmap

        // If there should be a time out
        if (timeOut) {
            // Delay
            Utilities.TIMER.schedule(() -> {
                // Only run if message isn't removed yet
                if (reactions.get(guildId).get(command).get(message.getId()) != null) {
                    reactions.get(guildId).get(command).remove(message.getId()); // Remove command from the hashmap
                    if (message != null) message.clearReactions().queue(); // Clear all reaction emojis

                }
            }, 1, TimeUnit.MINUTES); // Time out will be after 1 minute
        }
    }

    public static boolean check(GuildMessageReactionAddEvent event, String command, boolean delete) {
        final String guildId = event.getGuild().getId();
        final String messageId = event.getMessageId();

        if (event.getUser().isBot()) return false; // Author is bot

        if (!reactions.containsKey(guildId)) return false; // Guild isn't in the hashmap
        if (!reactions.get(guildId).containsKey(command)) return false; // Command isn't in the hashmap
        if (!reactions.get(guildId).get(command).containsKey(messageId)) return false; // Message isn't in the hashmap


        final Document reaction = reactions.get(guildId).get(command).get(messageId); // Get reaction document
        if (!event.getUser().getId().equals(reaction.getString("userId"))) return false; // Wrong user reacted

        // Reaction is emoji
        if (event.getReactionEmote().isEmoji()) {
            if (!reaction.getList("emojis", String.class).contains(event.getReactionEmote().getEmoji()))
                return false; // Wrong emoji
        }
        // Reaction is custom emote
        if (event.getReactionEmote().isEmote()) {
            if (!reaction.getList("emojis", String.class).contains(event.getReactionEmote().getEmote().getId()))
                return false; // Wrong emoji
        }

        if (delete) reactions.get(guildId).get(command).remove(messageId); // Delete message from hashmap
        return true; // Return true
    }

    public static void remove(String command, Message message) {
        reactions.get(message.getGuild().getId()).get(command).remove(message.getId());
        message.clearReactions().queue();
    }
}
