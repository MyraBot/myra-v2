package com.github.m5rian.myra.listeners.leveling;

import com.github.m5rian.myra.database.MongoUser;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.database.guild.member.GuildMember;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Duration;
import java.util.HashMap;
import java.util.Random;

public class LevelingListener {
    private static HashMap<Guild, HashMap<Member, Message>> cooldown = new HashMap<>();
    private final Leveling LEVELING = new Leveling();

    public void onMessage(MessageReceivedEvent event) throws Exception {
        if (!event.isFromGuild()) return; // Ignore direct messages

        final MongoGuild dbGuild = MongoGuild.get(event.getGuild()); // Get guild from database
        final MongoUser dbUser = new MongoUser(event.getAuthor()); // Get User from database
        final GuildMember dbMember = dbGuild.getMembers().getMember(event.getMember()); // Get member from database

        //System.out.println("message count " + dbMember.getMessages());
        dbMember.addMessage(); // Update message count
        dbUser.addMessage(); // Update global messages count

        final int xpFromMessage = getXpFromMessage(event.getMessage()); // Get xp from message
        dbUser.addXp(xpFromMessage); // Update global xp count

        if (!dbGuild.getListenerManager().check("leveling")) return; // Check if leveling is enabled
        if (event.getMessage().getContentRaw().startsWith(dbGuild.getString("prefix"))) return; // Message is a command
        if (!cooldown(event)) return; // Cooldown

        LEVELING.levelUp(event.getMember(), event.getChannel(), dbMember, xpFromMessage); // Check for new level
        dbMember.addXp(xpFromMessage); // Update xp
    }

    private boolean cooldown(MessageReceivedEvent event) {
        boolean returnedValue = true;
        //check if guild isn't in the HashMap yet
        if (cooldown.get(event.getGuild()) == null) {
            //crate new Map for member
            HashMap<Member, Message> memberMap = new HashMap<>();
            memberMap.put(event.getMessage().getMember(), event.getMessage());
            //put guild in the 'cooldown' Map
            cooldown.put(event.getGuild(), memberMap);
        }
        //check if user isn't in the HashMap yet
        else if (cooldown.get(event.getGuild()).get(event.getMessage().getMember()) == null) {
            cooldown.get(event.getGuild()).put(event.getMessage().getMember(), event.getMessage());
        }
        //check if 1 minutes passed
        else {
            if (Duration.between(event.getMessage().getTimeCreated(), cooldown.get(event.getGuild()).get(event.getMessage().getMember()).getTimeCreated()).toSeconds() < 5) {
                cooldown.get(event.getGuild()).replace(event.getMessage().getMember(), event.getMessage());
                returnedValue = false;
            }
        }
        return returnedValue;
    }

    //return xp
    public int getXpFromMessage(Message rawMessage) {
        String message = rawMessage.getContentDisplay(); // Get message
        final int oneOrTwo = new Random().nextInt(4) + 1; // Get random number between 1 and 5

        String[] eachWord = rawMessage.getContentRaw().split("\\s+"); // Split message in words
        for (String word : eachWord) { // Go through each word
            // Word is a link
            if (word.startsWith("http") || word.startsWith("www")) {
                message = message.replace(word, ""); // Remove link
            }
        }

        char[] msg = message.toCharArray(); // Convert message to character array
        return msg.length / 10 + oneOrTwo; // Calculate xp
    }
}
