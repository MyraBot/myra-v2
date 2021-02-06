package com.myra.dev.marian.commands.music;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@CommandSubscribe(
        name = "queue",
        aliases = {"songs", "tracks"},
        channel = Channel.GUILD
)
public class MusicQueue implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Get utilities
        Utilities utilities = Utilities.getUtils();
// Errors
        // Bot isn't connected to a voice channel
        if (!ctx.getGuild().getAudioManager().isConnected()) {
            new Error(ctx.getEvent())
                    .setCommand("shuffle queue")
                    .setEmoji("\uD83D\uDCE4")
                    .setMessage("I'm not connected to a voice channel")
                    .send();
            return;
        }
        // No audio track is playing
        if (PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack() == null) {
            new Error(ctx.getEvent())
                    .setCommand("shuffle queue")
                    .setEmoji("\uD83D\uDCE4")
                    .setMessage("The player isn't playing any song")
                    .send();
            return;
        }
// Send Queue
        // Get queue
        BlockingQueue<AudioTrack> queue = PlayerManager.getInstance().getMusicManager(ctx.getGuild()).scheduler.getQueue();
        // Get the first 15 audio tracks
        int trackCount = Math.min(queue.size(), 15);
        List<AudioTrack> tracks = new ArrayList<>(queue);
        String songs = "";
        for (int i = 0; i < trackCount; i++) {
            songs += ("\n• " + tracks.get(i).getInfo().title);
        }
        // If there are no songs queued
        if (songs.equals("")) {
            songs = "none \uD83D\uDE14";
        }
        // Get audio player
        AudioPlayer audioPlayer = PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer;
        // Get current playing Song
        String currentPlaying = utilities.hyperlink(audioPlayer.getPlayingTrack().getInfo().title, audioPlayer.getPlayingTrack().getInfo().uri);

        EmbedBuilder queuedSongs = new EmbedBuilder()
                .setAuthor("queue", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .addField("\uD83D\uDCC3 │ queued songs", songs, false)
                .addField("\uD83D\uDCDA │ total songs", Integer.toString(queue.size()), false)
                .addField("\uD83D\uDCBF │ current playing", currentPlaying, false);
        ctx.getChannel().sendMessage(queuedSongs.build()).queue();
    }
}