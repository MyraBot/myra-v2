package com.github.m5rian.myra.utilities.APIs.LavaPlayer;

import com.github.m5rian.myra.utilities.APIs.spotify.Playlist;
import com.github.m5rian.myra.utilities.APIs.spotify.Song;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.LoadingBar;
import com.github.m5rian.myra.utilities.Utilities;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
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
import java.util.concurrent.atomic.AtomicLong;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class PlayerManager {
    private static PlayerManager INSTANCE;

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        this.audioPlayerManager.getConfiguration().setFilterHotSwapEnabled(true); // Enable instant filter changes
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

    /**
     * @param player A {@link AudioPlayer} of an unknown guild.
     * @return Returns the matching guild id of the provided {@link AudioPlayer}.
     */
    public static Long getGuildIdFromPlayer(AudioPlayer player) {
        final AtomicLong guildId = new AtomicLong();
        PlayerManager.getInstance().musicManagers.forEach((key, value) -> {
            if (value.audioPlayer == player) guildId.set(key);
        });
        return guildId.get();
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
                        .setMessage("Adding to queue: " + Utilities.hyperlink("`" + track.getInfo().title + "`", track.getInfo().uri))
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
                        success.setMessage("Adding playlist to queue: " + Utilities.hyperlink("`" + playlist.getName() + "`", query));
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
        final LoadingBar loadingBarPreset = new LoadingBar("□", "■", 10L, (long) playlist.getSongs().size()); // Create preset for loading bar
        // Create decoding embed
        final EmbedBuilder decoding = new Success(null)
                .setCommand("play")
                .setAvatar(message.getAuthor().getEffectiveAvatarUrl())
                .setHyperLink(playlist.getUrl())
                .setMessage(lang(message).get("command.music.play.spotify.playlist.info.decoding")
                        .replace("{$playlist.name}", playlist.getName())
                        .replace("{$playlist.url}", playlist.getUrl()))
                .setFooter(loadingBarPreset.render(0L))
                .getEmbed();

        message.getChannel().sendMessage(decoding.build()).queue(response -> {
            final SpotifyPlaylistLoader playlistLoader = new SpotifyPlaylistLoader(response, playlist, musicManager); // Create a spotify playlist loader

            // Song loading
            final Thread songLoading = new Thread(() -> {
                for (Song song : playlist.getSongs()) {
                    final StringBuilder artists = new StringBuilder(); // Create string for all artists
                    song.getArtists().forEach(artist -> artists.append(artist.getName()).append(" ")); // Add all artists
                    final String songName = String.format("ytsearch:%s - %s", song.getName(), artists); // Create string to search in youtube

                    audioPlayerManager.loadItemOrdered(musicManager, songName, playlistLoader); // Load song
                }
            });
            songLoading.setName("Spotify playlist loader");

            // Embed updating
            final Thread messageUpdating = new Thread(() -> {
                // As long as songs are loaded into music manager
                while (playlist.getSongs().size() > playlistLoader.getPassedTracks()) {
                    try {
                        playlistLoader.updateEmbed(loadingBarPreset); // Update loading bar
                        Thread.sleep(5000); // Wait 5 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Last update of loading message to display that the playlist was loaded successfully
                playlistLoader.updateEmbed(loadingBarPreset);
            });
            messageUpdating.setName("Spotify playlist message updater");

            songLoading.start();
            messageUpdating.start();
        });
    }


    // Return PlayerManager class
    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }
}
