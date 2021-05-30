package com.github.m5rian.myra.commands.member.music;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.APIs.LavaPlayer.GuildMusicManager;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.APIs.LavaPlayer.PlayerManager;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.Utilities;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

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
                    .setMessage(Lang.lang(ctx).get("command.music.error.notPlaying"))
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
                .setMessage(Lang.lang(ctx).get("command.music.stop.done"))
                .send();
    }
}
