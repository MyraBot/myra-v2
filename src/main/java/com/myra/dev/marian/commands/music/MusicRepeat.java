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

public class MusicRepeat implements CommandHandler {

    @CommandEvent(
            name = "repeat",
            aliases = {"repeat queue", "queue repeat", "loop", "loop queue", "queue loop"},
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return; // Check for no arguments

        if (Utilities.hasMusicError(ctx)) return; // Check for errors
        // No audio track is playing
        if (PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack() == null) {
            new Error(ctx.getEvent())
                    .setCommand("repeat")
                    .setEmoji("\uD83D\uDD01")
                    .setMessage(lang(ctx).get("command.music.error.notPlaying"))
                    .send();
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild()); // Get guild audio manager
        final boolean repeating = !musicManager.scheduler.repeating; // Get new repeating value
        musicManager.scheduler.repeating = repeating; // Update repeat value

        new Success(ctx.getEvent())
                .setCommand("repeat")
                .setEmoji("\uD83D\uDD01")
                .setMessage(repeating ? lang(ctx).get("command.music.repeat.true") : lang(ctx).get("command.music.repeat.false"))
                .send();
    }
}
