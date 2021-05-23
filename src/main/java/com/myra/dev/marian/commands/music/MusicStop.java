package com.myra.dev.marian.commands.music;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.GuildMusicManager;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Utilities;

import static com.myra.dev.marian.utilities.language.Lang.lang;

public class MusicStop implements CommandHandler {

    @CommandEvent(
            name = "stop",
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return; // Check for no arguments

        if (Utilities.hasMusicError(ctx)) return; // Check for errors
        // No audio track is playing
        if (PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack() == null) {
            new Error(ctx.getEvent())
                    .setCommand("stop")
                    .setEmoji("\u23F9")
                    .setMessage(lang(ctx).get("command.music.error.notPlaying"))
                    .send();
            return;
        }

        // Stop player
        final GuildMusicManager player = PlayerManager.getInstance().getMusicManager(ctx.getGuild()); // Get player manager
        player.scheduler.getQueue().clear(); // Clear queue
        player.audioPlayer.stopTrack(); // Stop track
        player.audioPlayer.destroy(); // Destroy audio player

        new Success(ctx.getEvent())
                .setCommand("stop")
                .setEmoji("\u23F9")
                .setMessage(lang(ctx).get("command.music.stop.done"))
                .send();
    }
}
