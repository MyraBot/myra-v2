package com.myra.dev.marian.commands.music;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Utilities;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static com.myra.dev.marian.utilities.language.Lang.lang;

public class MusicShuffle implements CommandHandler {

    @CommandEvent(
            name = "shuffle",
            aliases = {"random", "randomize"},
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return; // Check for no arguments

        if (Utilities.hasMusicError(ctx)) return; // Check for errors
        // No audio track is playing
        if (PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack() == null) {
            new Error(ctx.getEvent())
                    .setCommand("shuffle")
                    .setEmoji("\uD83D\uDCE4")
                    .setMessage(lang(ctx).get("command.music.error.notPlaying"))
                    .send();
            return;
        }

        final BlockingQueue<AudioTrack> queue = PlayerManager.getInstance().getMusicManager(ctx.getGuild()).scheduler.getQueue(); // Get queue
        final List<AudioTrack> tracks = new ArrayList<>(queue); // Get queue as list
        Collections.shuffle(tracks); // Shuffle queue

        PlayerManager.getInstance().getMusicManager(ctx.getGuild()).scheduler.getQueue().clear(); // Clear current queue
        queue.addAll(tracks); // Add shuffled queue

        // Success message
        new Success(ctx.getEvent())
                .setCommand("shuffle")
                .setEmoji("\uD83D\uDCE4")
                .setMessage(lang(ctx).get("command.music.shuffle.done"))
                .send();
    }
}
