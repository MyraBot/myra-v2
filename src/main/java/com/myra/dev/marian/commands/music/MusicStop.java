package com.myra.dev.marian.commands.music;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.APIs.LavaPlayer.GuildMusicManager;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "stop",
        channel = Channel.GUILD
)
public class MusicStop implements Command {
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
                    .setCommand("stop")
                    .setEmoji("\u23F9")
                    .setMessage("I'm not connected to a voice channel")
                    .send();
            return;
        }
        // No audio track is playing
        if (PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack() == null) {
            new Error(ctx.getEvent())
                    .setCommand("stop")
                    .setEmoji("\u23F9")
                    .setMessage("The player isn't playing any song")
                    .send();
            return;
        }
// Skip current playing track
        // Send success message
        EmbedBuilder success = new EmbedBuilder()
                .setAuthor("stop", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(utilities.blue)
                .setDescription("Stopped music player");
        ctx.getChannel().sendMessage(success.build()).queue();
        // Stop player
        final GuildMusicManager player = PlayerManager.getInstance().getMusicManager(ctx.getGuild()); // Get player manager
        player.scheduler.getQueue().clear(); // Clear queue
        player.audioPlayer.stopTrack(); // Stop track
    }
}
