package com.myra.dev.marian.commands.music;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class MusicShuffle implements CommandHandler {

@CommandEvent(
        name = "shuffle",
        aliases = {"random", "randomize"},
        channel = Channel.GUILD
)
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Get utilities
        Utilities utilities = Utilities.getUtils();
// Errors
        // Bot isn't connected to a voice channel
        if (!ctx.getGuild().getAudioManager().isConnected()) {
            new Error(ctx.getEvent())
                    .setCommand("shuffle")
                    .setEmoji("\uD83D\uDCE4")
                    .setMessage("I'm not connected to a voice channel")
                    .send();
            return;
        }
        // No audio track is playing
        if (PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack() == null) {
            new Error(ctx.getEvent())
                    .setCommand("shuffle")
                    .setEmoji("\uD83D\uDCE4")
                    .setMessage("The player isn't playing any song")
                    .send();
            return;
        }
// Shuffle queue
        // Get queue
        BlockingQueue<AudioTrack> queue = PlayerManager.getInstance().getMusicManager(ctx.getGuild()).scheduler.getQueue();
        List<AudioTrack> tracks = new ArrayList<>(queue);
        // Shuffle queue
        Collections.shuffle(tracks);
        // Replace
        PlayerManager.getInstance().getMusicManager(ctx.getGuild()).scheduler.getQueue().clear();
        queue.addAll(tracks);
        // Success message
        EmbedBuilder success = new EmbedBuilder()
                .setAuthor("shuffle", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(utilities.blue)
                .setDescription("The current queue was jumbled");
        ctx.getChannel().sendMessage(success.build()).queue();
    }
}
