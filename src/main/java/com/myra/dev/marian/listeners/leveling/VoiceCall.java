package com.myra.dev.marian.listeners.leveling;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.database.allMethods.GetMember;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class VoiceCall {
    //              Guild         User   start time
    private static HashMap<String, HashMap<String, Long>> activeCalls = new HashMap<>(); // Create hashmap for tracking calls

    public void startXpGain(Member member) {
        final Guild guild = member.getGuild(); // Get guild
        final GuildVoiceState voiceState = member.getVoiceState(); // Get members voice state
        if (voiceState.isMuted()) return; // Member is muted
        if (voiceState.getChannel() == null) return; // Member isn't in a voice call anymore

        int size = (int) voiceState.getChannel().getMembers().stream().filter(vcMember -> !vcMember.getUser().isBot()).count(); // Get amount of members in the voice call (without bots)
        if (size <= 1) return; // Member is alone in the voice call

        // No voice call has been registered yet
        if (!activeCalls.containsKey(guild.getId())) {
            activeCalls.put(guild.getId(), new HashMap<>()); // Add guild to active voice calls
        }
        if (activeCalls.get(guild.getId()).containsKey(member.getId())) return; // Member is already earning xp

        // Add user
        activeCalls.get(guild.getId()).put(member.getId(), System.currentTimeMillis()); // Save voice call start time
    }

    public void updateXpGain(GuildVoiceMuteEvent event) {
        final GuildVoiceState state = event.getMember().getVoiceState(); // Members voice sate

        // Member was muted
        if (state.isDeafened() || state.isMuted()) { // Will return a value if you are guild deafened/muted or self deafened/muted
            stopXpGain(event.getMember());
        }
        // Member was unmuted
        else startXpGain(event.getMember());
    }

    public void stopXpGain(Member member) {
        if (!activeCalls.containsKey(member.getGuild().getId())) return;
        if (!activeCalls.get(member.getGuild().getId()).containsKey(member.getId())) return;

        final GetMember dbMember = new Database(member.getGuild()).getMembers().getMember(member); // Get member in database

        final Long currentSpokenTime = dbMember.getLong("voiceCallTime"); // Get voice call time until now
        final Long timeSpoken = System.currentTimeMillis() - activeCalls.get(member.getGuild().getId()).get(member.getId()); // Get voice call time from the active voice call
        dbMember.setLong("voiceCallTime", currentSpokenTime + timeSpoken); // Update voice call time

        final Integer xp = dbMember.getInteger("xp"); // Get current xp
        dbMember.setInteger("xp", xp + getXp(timeSpoken)); // Update xp

        new Leveling().levelUp(member, null, dbMember, xp); // Check for new level

        activeCalls.get(member.getGuild().getId()).remove(member.getId()); // Remove user from active calls
    }


    public void updateXpGain(VoiceChannel vc) {
        if (vc.getMembers().isEmpty()) return; // Voice call is empty :c

        int size = (int) vc.getMembers().stream().filter(member -> !member.getUser().isBot()).count(); // Get amount of members in the voice call (without bots)
        // At lest 2 members are in the voice call
        if (size > 1) {
            vc.getMembers().forEach(this::startXpGain); // Start xp gain of every member
        }
        // Member is alone in the voice call
        else {
            vc.getMembers().forEach(this::stopXpGain); // Stop xp gain for every member
        }
    }

    private int getXp(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);

        return (int) (minutes / 5) * 2;
    }
}
