package com.github.m5rian.myra.utilities.APIs.LavaPlayer;

import com.github.m5rian.myra.utilities.APIs.spotify.Playlist;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.LoadingBar;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class SpotifyPlaylistLoader implements AudioLoadResultHandler {
    private boolean finished = false;
    private int passed = 0;
    private int loaded = 0;
    private int failed = 0;

    private final Message message;
    private final Playlist playlist;
    private final GuildMusicManager musicManager;

    /**
     * @param message      The success message.
     * @param playlist     The Spotify {@link Playlist} with all songs to decode.
     * @param musicManager The {@link GuildMusicManager}.
     */
    public SpotifyPlaylistLoader(Message message, Playlist playlist, GuildMusicManager musicManager) {
        this.message = message;
        this.playlist = playlist;
        this.musicManager = musicManager;
    }

    public int getPassedTracks() {
        return this.passed;
    }

    public int getLoadedTracks() {
        return this.loaded;
    }

    public int getFailedTracks() {
        return this.failed;
    }

    public void updateEmbed(LoadingBar loadingBarPreset) {
        // Finished loading
        if (this.playlist.getSongs().size() == this.passed) {
            final Success success = new Success(null)
                    .setCommand("play")
                    .setAvatar(this.message.getAuthor().getEffectiveAvatarUrl())
                    .setHyperLink(playlist.getUrl())
                    .setThumbnail(playlist.getThumbnail())
                    .setMessage(lang(this.message).get("command.music.play.spotify.playlist.info.loaded")
                            .replace("{$playlist.size}", String.valueOf(this.loaded))
                            .replace("{$playlist.name}", playlist.getName())
                            .replace("{$playlist.url}", playlist.getUrl()))
                    .setFooter(lang(this.message).get("command.music.play.spotify.playlist.info.author")
                            .replace("{$playlist.owner}", playlist.getOwner().getName()));
            if (failed > 0) success.appendMessage("\n\u26A0 │ " + lang(this.message).get("command.music.play.spotify.playlist.info.failedSongs")
                    .replace("{$failed}", String.valueOf(this.failed)));

            this.message.editMessage(success.getEmbed().build()).queue(); // Edit message
            return;
        }

        final String currentBar = this.message.getEmbeds().get(0).getFooter().getText(); // Get current loading bar
        final String updatedBar = loadingBarPreset.render(this.passed); // Get updated loading bar
        // Bar changed
        if (!currentBar.equals(updatedBar)) {
            // Create decoding embed
            final EmbedBuilder decoding = new Success(null)
                    .setCommand("play")
                    .setAvatar(this.message.getAuthor().getEffectiveAvatarUrl())
                    .setHyperLink(playlist.getUrl())
                    .setMessage(lang(this.message).get("command.music.play.spotify.playlist.info.decoding")
                            .replace("{$playlist.name}", this.playlist.getName())
                            .replace("{$playlist.url}", this.playlist.getUrl()))
                    .setFooter(updatedBar)
                    .getEmbed();
            this.message.editMessage(decoding.build()).queue(); // Edit message
        }
    }

    /**
     * @param track The track which should load.
     *              This method will only run if a link was provided.
     *              Because lavaplayer can't handle spotify playlist we can ignore this method.
     */
    @Override
    public void trackLoaded(AudioTrack track) {

    }

    /**
     * @param result The search result.
     */
    @Override
    public void playlistLoaded(AudioPlaylist result) {
        // Search result is a playlist
        if (!result.isSearchResult()) {
            this.passed++; // Add one passed track
            this.failed++; // Add one failed track
        }
        // Search result is a single song
        else {
            final AudioTrack track = result.getTracks().get(0); // Get first song
            final RequestData requestData = new RequestData(this.message.getAuthor().getIdLong(), this.message.getTextChannel()); // Create new request data
            track.setUserData(requestData); // Add request data
            this.musicManager.scheduler.queue(track); // Add track to queue
            this.passed++; // Add one passed track
            this.loaded++; // Add one successfully loaded track
        }
    }

    /**
     * Called when there were no items found by the specified identifier.
     */
    @Override
    public void noMatches() {
        this.passed++; // Add one passed track
        this.failed++; // Add one failed track
    }

    /**
     * Runs when an exception is thrown. The song won't be added to the queue.
     *
     * @param exception The thrown exception.
     */
    @Override
    public void loadFailed(FriendlyException exception) {
        this.passed++; // Add one passed track
        this.failed++; // Add one failed track
    }
}
