package com.myra.dev.marian.commands.music;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Format;
import com.myra.dev.marian.utilities.LoadingBar;
import com.myra.dev.marian.utilities.Utilities;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import static com.myra.dev.marian.utilities.language.Lang.lang;

public class MusicInformation implements CommandHandler {

    @CommandEvent(
            name = "track information",
            aliases = {"track info", "music information", "music info", "track", "song", "song information", "song info", "current", "current playing"},
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return; // Check for no arguments

        final AudioPlayer player = PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer; // Get audio player
        if (Utilities.hasMusicError(ctx)) return; // Check for errors
        // Bot isn't playing any songs
        if (PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack() == null) {
            new Error(ctx.getEvent())
                    .setCommand("track information")
                    .setEmoji("\uD83D\uDDD2")
                    .setMessage(lang(ctx).get("command.music.error.notPlaying"))
                    .send();
            return;
        }

        final String position = new LoadingBar("\u25AC", "\uD83D\uDD18", 15L, player.getPlayingTrack().getDuration()).asPointer().render(player.getPlayingTrack().getPosition());
        new Success(ctx.getEvent())
                .setCommand("track information")
                .setMessage(player.isPaused() ? "\u23F8\uFE0F " : "\u23F8\uFE0F " + Format.toTimeExact(player.getPlayingTrack().getPosition()) + " - " + Format.toTimeExact(player.getPlayingTrack().getDuration()))
                .setFooter(position)
                .send();
    }
}