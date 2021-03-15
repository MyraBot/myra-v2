package com.myra.dev.marian.utilities.APIs.LavaPlayer;

import com.myra.dev.marian.utilities.APIs.spotify.Playlist;
import com.myra.dev.marian.utilities.APIs.spotify.Song;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.LoadingBar;
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
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
        audioPlayerManager.setFrameBufferDuration(5000); // Add a 5 second buffer
        audioPlayerManager.setItemLoaderThreadPoolSize(10);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            // Tell JDA what to use to send the audio
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

    public void loadAndPlay(Message message, String query, boolean isUrl, Playlist playlist) {
        final GuildMusicManager musicManager = this.getMusicManager(message.getGuild()); // Get music manager for guild

        // Load songs from spotify playlist
        if (playlist != null) {
            loadSpotifyPlaylist(message, playlist, musicManager); // Load spotify playlist
        }
        // Load songs from other sources (handled by lavaplayer)
        else {
            loadAndPlayQuery(message, query, isUrl, musicManager);
        }
        musicManager.audioPlayer.setVolume(50); // Set volume
    }


    private void loadAndPlayQuery(Message message, String query, boolean isUrl, GuildMusicManager musicManager) {
        final User author = message.getAuthor();  // Get author

        this.audioPlayerManager.loadItemOrdered(musicManager, query, new AudioLoadResultHandler() {

            /**
             * Load a track into the queue
             * @param track The found audio track based on the query.
             */
            @Override
            public void trackLoaded(AudioTrack track) {
                final RequestData requestData = new RequestData(author.getIdLong(), message.getTextChannel()); // Create new request data
                track.setUserData(requestData); // Add request data
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

            /**
             * Load a playlist into the queue
             * @param playlist The found playlist based on the query.
             *                 if playlist#isSearchResult is true the playlist will
             *                 be a list of search results which match the query
             */
            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                // Query is a link to a playlist
                if (!playlist.isSearchResult()) {
                    Success success = new Success(null)
                            .setCommand("play")
                            .setAvatar(author.getEffectiveAvatarUrl())
                            .setHyperLink(query)
                            .setChannel(message.getChannel());
                    // Playlist is given by url
                    if (isUrl) { // Add in message url to playlist
                        success.setMessage("Adding playlist to queue: " + Utilities.getUtils().hyperlink("`" + playlist.getName() + "`", query));
                    } else {
                        success.setMessage(String.format("Adding playlist to queue: `%s`", playlist.getName()));
                    }
                    success.send(); // Send success message

                    // Add every audio of the playlist track to queue
                    playlist.getTracks().forEach(track -> {
                        final RequestData requestData = new RequestData(author.getIdLong(), message.getTextChannel()); // Create new request data
                        track.setUserData(requestData); // Add request data
                        musicManager.scheduler.queue(track); // Add audio track to queue
                    });
                }

                // Playlist are audio racks, which match query
                else {
                    final AudioTrack track = playlist.getTracks().get(0); // Get first track
                    final RequestData requestData = new RequestData(author.getIdLong(), message.getTextChannel()); // Create new request data
                    track.setUserData(requestData); // Add request data
                    trackLoaded(track); // Load track in queue
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
    }

    private void loadSpotifyPlaylist(Message message, Playlist playlist, GuildMusicManager musicManager) {
        final User author = message.getAuthor(); // Get requester

        final EmbedBuilder decoding = getDecodingEmbed(message.getAuthor(), playlist); // Get decoding embed
        message.getChannel().sendMessage(decoding.build()).queue(msg -> {

            final AtomicInteger overall = new AtomicInteger(); // Amount of passed tracks
            final AtomicInteger loaded = new AtomicInteger(); // Amount of successfully loaded tracks
            final AtomicInteger failed = new AtomicInteger(); // Amount of failed tracks

            for (Song song : playlist.getSongs()) {
                final StringBuilder artists = new StringBuilder();
                song.getArtists().forEach(artist -> artists.append(artist.getName()).append(" ")); // Add all artists

                final String songName = String.format("ytsearch:%s - %s", song.getName(), artists);

                audioPlayerManager.loadItemOrdered(musicManager, songName, new AudioLoadResultHandler() {
                    /**
                     * @param track The loaded track
                     *              This method will only run if a link was provided
                     *              We can ignore this method because spotify's links aren't valid
                     */
                    @Override
                    public void trackLoaded(AudioTrack track) {
                    }

                    /**
                     * @param result The loaded playlist / Search result of query
                     */
                    @Override
                    public void playlistLoaded(AudioPlaylist result) {
                        // Don't add playlists
                        if (!result.isSearchResult()) return;

                        final AudioTrack track = result.getTracks().get(0); // Get first song
                        final RequestData requestData = new RequestData(author.getIdLong(), message.getTextChannel()); // Create new request data
                        track.setUserData(requestData); // Add request data
                        musicManager.scheduler.queue(track); // Add track to queue
                        loaded.addAndGet(1); // Add one successfully loaded track
                        overall.addAndGet(1); // Add one passed track
                        updateDecodingStatus(msg, message.getAuthor(), playlist, overall.get(), loaded.get(), failed.get()); // Update decoding status
                    }

                    /**
                     * Called when there were no items found by the specified identifier.
                     */
                    @Override
                    public void noMatches() {
                        failed.getAndAdd(1); // Add one failed song
                        overall.addAndGet(1); // Add one passed track
                        updateDecodingStatus(msg, message.getAuthor(), playlist, overall.get(), loaded.get(), failed.get()); // Update decoding status
                    }

                    @Override
                    public void loadFailed(FriendlyException exception) {
                        failed.getAndAdd(1); // Add one failed song
                        overall.addAndGet(1); // Add one passed track
                        updateDecodingStatus(msg, message.getAuthor(), playlist, overall.get(), loaded.get(), failed.get()); // Update decoding status
                    }
                });
            }

        });
    }


    private void updateDecodingStatus(Message msg, User author, Playlist playlist, int passedInt, int loaded, int failed) {
        final String currentBar = msg.getEmbeds().get(0).getFooter().getText(); // Get current loading bar
        final long passed = Long.parseLong(String.valueOf(passedInt)); // Get passed tracks as long
        final String updatedBar = new LoadingBar("□", "■", 10L, (long) playlist.getSongs().size()).render(passed); // Get updated loading bar

        // Finished loading
        if (playlist.getSongs().size() == passedInt) {
            Success success = new Success(null)
                    .setCommand("play")
                    .setAvatar(author.getEffectiveAvatarUrl())
                    .setHyperLink(playlist.getUrl())
                    .setThumbnail(playlist.getThumbnail())
                    .setMessage(String.format("Added **%s** songs to the queue from the playlist %s", loaded, Utilities.getUtils().hyperlink("`" + playlist.getName() + "`", playlist.getUrl())))
                    .setFooter("by " + playlist.getOwner().getName());
            if (failed > 0) success.appendMessage(String.format("%n⚠ │ `%s` songs failed to load", failed));

            msg.editMessage(success.getEmbed().build()).queue(); // Edit message
            return;
        }

        // Bar didn't change
        if (currentBar.equals(updatedBar)) return;

            // Bar changed
        else {
            EmbedBuilder decoding = getDecodingEmbed(author, playlist);// Get decoding embed
            decoding.setFooter(updatedBar); // Update loading bar
            msg.editMessage(decoding.build()).queue(); // Edit message
        }

    }

    private EmbedBuilder getDecodingEmbed(User author, Playlist playlist) {
        return new Success(null)
                .setCommand("play")
                .setAvatar(author.getEffectiveAvatarUrl())
                .setHyperLink(playlist.getUrl())
                .setMessage("Decoding " + Utilities.getUtils().hyperlink("`" + playlist.getName() + "`", playlist.getUrl()))
                .setFooter("□□□□□□□□□□")
                .getEmbed();
    }

    // Return PlayerManager class
    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }
}
