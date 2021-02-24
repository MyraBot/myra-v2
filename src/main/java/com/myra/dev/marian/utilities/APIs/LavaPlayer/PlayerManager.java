package com.myra.dev.marian.utilities.APIs.LavaPlayer;

import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Utilities;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

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

    public void loadAndPlay(Message message, String query, boolean isUrl) {
        final GuildMusicManager musicManager = this.getMusicManager(message.getGuild()); // Get music manager for guild
        final User author = message.getAuthor();  // Get author
        // All methods
        this.audioPlayerManager.loadItemOrdered(musicManager, query, new AudioLoadResultHandler() {

            // Load a track into the queue
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.scheduler.queue(track); // Add audio track to queue
                // Success message
                new Success(null)
                        .setCommand("play")
                        .setAvatar(author.getEffectiveAvatarUrl())
                        .setHyperLink(track.getInfo().title)
                        .setMessage("Adding to queue: " + Utilities.getUtils().hyperlink("`" + track.getInfo().title + "`", track.getInfo().uri))
                        .setChannel(message.getChannel())
                        .send();
            }

            // Load a playlist into the queue
            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                // Run if query is a link to a playlist
                if (isUrl) {
                    // Success message
                    new Success(null)
                            .setCommand("play")
                            .setAvatar(author.getEffectiveAvatarUrl())
                            .setHyperLink(query)
                            .setMessage("Adding playlist to queue: " + Utilities.getUtils().hyperlink("`" + playlist.getName() + "`", query))
                            .setChannel(message.getChannel())
                            .send();
                    playlist.getTracks().forEach(musicManager.scheduler::queue); // Add every audio of the playlist track to queue
                }

                // Playlist are results, which match query
                else if (playlist.isSearchResult()) {
                    final AudioTrack track = playlist.getTracks().get(0); // Get first track
                    trackLoaded(track); // Load track in queue
                }
                // Playlist was found
                else {
                    // Success message
                    new Success(null)
                            .setCommand("play")
                            .setAvatar(author.getEffectiveAvatarUrl())
                            .setHyperLink(query)
                            .setMessage(String.format("Adding playlist to queue: `%s`", playlist.getName()))
                            .setChannel(message.getChannel())
                            .send();
                    playlist.getTracks().forEach(musicManager.scheduler::queue); // Add every audio of the playlist track to queue
                }
            }

            @Override
            public void noMatches() {
                new Error(null)
                        .setCommand("play")
                        .setEmoji("\uD83D\uDCBF")
                        .setAvatar(author.getEffectiveAvatarUrl())
                        .setMessage(String.format("Nothing found by `%s`", query))
                        .setChannel(message.getChannel())
                        .send();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                new Error(null)
                        .setCommand("play")
                        .setEmoji("\uD83D\uDCBF")
                        .setAvatar(author.getEffectiveAvatarUrl())
                        .setMessage("Could not play the track")
                        .send();
            }
        });

        musicManager.audioPlayer.setVolume(50); // Set volume
    }

    // Return PlayerManager class
    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }
}
