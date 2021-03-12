package com.myra.dev.marian.listeners;

import com.myra.dev.marian.utilities.APIs.LavaPlayer.GuildMusicManager;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MusicTimeout {
    private final static Map<Long, MusicTimeout> setter = new HashMap<>();

    public static MusicTimeout getGuildManager(Guild guild) {
        // No MusicTimeout saved for this guild
        if (!setter.containsKey(guild.getIdLong())) {
            final MusicTimeout guildTimeOut = new MusicTimeout(); // Create new MusicTimeout object
            setter.put(guild.getIdLong(), guildTimeOut); // Add new MusicTimeout to setter
            return guildTimeOut; // Return guild specific MusicTimeout object
        }

        return setter.get(guild.getIdLong());
    }


    private AtomicBoolean timeout = new AtomicBoolean(false);

    public void timeout(Guild guild, VoiceChannel call) {
        if (!guild.getAudioManager().isConnected()) return; // Bot isn't in a voice call

        // Get amount of members in a voice call without bots
        final long members = call.getMembers().stream().filter(member -> !member.getUser().isBot()).count();

        // No member is in the voice call
        if (members == 0) {
            this.timeout.set(true);

            Utilities.TIMER.schedule(() -> {
                if (this.timeout.get()) {
                    GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(guild);

                    manager.scheduler.getQueue().clear(); // Clear queue
                    manager.audioPlayer.stopTrack(); // Stop track
                    manager.audioPlayer.destroy(); // Destroy audio player

                    guild.getAudioManager().closeAudioConnection(); // Leave voice call
                }
            }, 10, TimeUnit.SECONDS);

        }
    }

    public void interrupt() {
        this.timeout.set(false);
    }
}
