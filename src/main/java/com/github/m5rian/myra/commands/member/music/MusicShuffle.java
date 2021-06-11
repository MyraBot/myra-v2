package com.github.m5rian.myra.commands.member.music;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.APIs.LavaPlayer.PlayerManager;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.language.Lang;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class MusicShuffle implements CommandHandler {

    @CommandEvent(
            name = "shuffle",
            aliases = {"random", "randomize"},
            emoji = "\uD83C\uDFB2",
            description = "description.music.shuffle",
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
                    .setMessage(Lang.lang(ctx).get("command.music.error.notPlaying"))
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
                .setMessage(Lang.lang(ctx).get("command.music.shuffle.done"))
                .send();
    }
}
