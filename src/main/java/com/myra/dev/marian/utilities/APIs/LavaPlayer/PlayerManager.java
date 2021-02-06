package com.myra.dev.marian.utilities.APIs.LavaPlayer;

import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    private static PlayerManager INSTANCE;

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        // Register sources
        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        // Set buffer
        audioPlayerManager.setFrameBufferDuration(1000);
        audioPlayerManager.setItemLoaderThreadPoolSize(500);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            // Tell JDA what to use to send the audio
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

    public void loadAndPlay(Guild guild,MessageChannel channel, String trackUrl, String authorAvatar, String thumbnailUrl) {
        // Get Utilities
        Utilities utilities = Utilities.getUtils();
        // Get music manager for guild
        final GuildMusicManager musicManager = this.getMusicManager(guild);
        // All methods
        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            // Load a track into the queue
            @Override
            public void trackLoaded(AudioTrack track) {
                // Add audio track to queue
                musicManager.scheduler.queue(track);
                // Success message
                EmbedBuilder success = new EmbedBuilder()
                        .setAuthor("play", trackUrl, authorAvatar)
                        .setColor(utilities.blue)
                        .setDescription("Adding to queue: " + utilities.hyperlink(track.getInfo().title, trackUrl))
                        .setImage(thumbnailUrl);
                channel.sendMessage(success.build()).queue();
            }

            // Load a playlist into the queue
            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                // Add every audio of the playlist track to queue
                playlist.getTracks().forEach(musicManager.scheduler::queue);
                // Success message
                EmbedBuilder success = new EmbedBuilder()
                        .setAuthor("play", trackUrl, authorAvatar)
                        .setColor(utilities.blue)
                        .setDescription("Adding playlist to queue: " + utilities.hyperlink(playlist.getName(), trackUrl))
                        .setImage(thumbnailUrl);
                channel.sendMessage(success.build()).queue();
            }

            @Override
            public void noMatches() {
                new Error(null)
                        .setCommand("play")
                        .setEmoji("\uD83D\uDCBF")
                        .setAvatar(authorAvatar)
                        .setMessage("Nothing found by " + trackUrl)
                        .send();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                new Error(null)
                        .setCommand("play")
                        .setEmoji("\uD83D\uDCBF")
                        .setAvatar(authorAvatar)
                        .setMessage("Could not play the track")
                        .send();
            }
        });
        // Set volume
        musicManager.audioPlayer.setVolume(50);
    }

    // Return PlayerManager class
    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }
}
