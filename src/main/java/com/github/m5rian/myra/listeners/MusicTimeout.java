package com.github.m5rian.myra.listeners;

import com.github.m5rian.myra.utilities.APIs.LavaPlayer.GuildMusicManager;
import com.github.m5rian.myra.utilities.APIs.LavaPlayer.PlayerManager;
import com.github.m5rian.myra.utilities.Utilities;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MusicTimeout {

    private final static List<Long> timeoutQueue = new ArrayList<>();

    public static void timeout(VoiceChannel call) {
        if (!call.getGuild().getAudioManager().isConnected()) return; // Bot isn't in a voice call

        // Get amount of members in a voice call without bots
        final long members = call.getMembers().stream().filter(member -> !member.getUser().isBot()).count();
        final Guild guild = call.getGuild(); // Get guild

        // In the voice call are still members
        if (members != 0) {
            System.out.println("stopping timeout");
            timeoutQueue.remove(guild.getIdLong()); // Remove guild from the timeout queue
        }
        // No member is in the voice call
        if (members == 0) {
            System.out.println("started timeout");
            timeoutQueue.add(guild.getIdLong()); // Add guild to timeout queue

            Utilities.TIMER.schedule(() -> {
                System.out.println("Checking!!!");
                // The timeout wasn't canceled
                if (timeoutQueue.contains(guild.getIdLong())) {
                    final GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(guild);

                    manager.scheduler.getQueue().clear(); // Clear queue
                    manager.audioPlayer.stopTrack(); // Stop track
                    manager.audioPlayer.destroy(); // Destroy audio player

                    guild.getAudioManager().closeAudioConnection(); // Leave voice call

                    timeoutQueue.remove(guild.getIdLong()); // Remove guild from timeout queue
                }
            }, 10, TimeUnit.SECONDS);

        }
    }

}
