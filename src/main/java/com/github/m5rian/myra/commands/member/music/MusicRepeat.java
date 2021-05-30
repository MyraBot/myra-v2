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
                    .setMessage(Lang.lang(ctx).get("command.music.error.notPlaying"))
                    .send();
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild()); // Get guild audio manager
        final boolean repeating = !musicManager.scheduler.repeating; // Get new repeating value
        musicManager.scheduler.repeating = repeating; // Update repeat value

        new Success(ctx.getEvent())
                .setCommand("repeat")
                .setEmoji("\uD83D\uDD01")
                .setMessage(repeating ? Lang.lang(ctx).get("command.music.repeat.true") : Lang.lang(ctx).get("command.music.repeat.false"))
                .send();
    }
}
