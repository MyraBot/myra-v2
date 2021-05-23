package com.myra.dev.marian.commands.music;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Utilities;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static com.myra.dev.marian.utilities.language.Lang.lang;

public class MusicQueue implements CommandHandler {

    @CommandEvent(
            name = "queue",
            aliases = {"songs", "tracks"},
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return; // Check for no arguments

        if (Utilities.hasMusicError(ctx)) return; // Check for errors
        // No audio track is playing
        if (PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack() == null) {
            new Error(ctx.getEvent())
                    .setCommand("shuffle queue")
                    .setEmoji("\uD83D\uDCE4")
                    .setMessage(lang(ctx).get("command.music.error.notPlaying"))
                    .send();
            return;
        }

        final BlockingQueue<AudioTrack> queue = PlayerManager.getInstance().getMusicManager(ctx.getGuild()).scheduler.getQueue(); // Get current queue
        final StringBuilder tracks = new StringBuilder(); // All songs
        // No tracks are queued
        if (queue.isEmpty()) {
            tracks.append(lang(ctx).get("command.music.queue.noTracks"));
        }
        // Tracks are queued
        else {
            final int trackCount = Math.min(queue.size(), 10); // Get amount of tracks to display => max 15
            List<AudioTrack> queuedTracks = new ArrayList<>(queue); // Get queue as list
            for (int i = 0; i < trackCount; i++) {
                final AudioTrack track = queuedTracks.get(i); // Get current track
                tracks.append(lang(ctx).get("command.music.queue.track") // Add track to string
                        .replace("{$name}", track.getInfo().title)
                        .replace("{$url}", track.getInfo().uri));
            }
        }

        final AudioPlayer audioPlayer = PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer; // Get audio player
        final String currentPlaying = Utilities.hyperlink(audioPlayer.getPlayingTrack().getInfo().title, audioPlayer.getPlayingTrack().getInfo().uri); // Get current playing Song
        // Queue
        new Success(ctx.getEvent())
                .setCommand("queue")
                .setEmoji("\uD83D\uDCE4")
                .addField("\uD83D\uDCDA │ " + lang(ctx).get("command.music.queue.trackCount"), String.valueOf(queue.size())) // Track count
                .addField("\uD83D\uDCC3 │ " + lang(ctx).get("command.music.queue.queuedSongs"), tracks.toString()) // All tracks
                .addField("\uD83D\uDCBF │ " + lang(ctx).get("command.music.queue.current"), currentPlaying) // Current playing song
                .send();
    }
}