package com.github.m5rian.myra.commands.member.music;


import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.myra.utilities.APIs.LavaPlayer.PlayerManager;
import com.github.m5rian.myra.utilities.APIs.spotify.Playlist;
import com.github.m5rian.myra.utilities.APIs.spotify.Spotify;
import com.github.m5rian.myra.utilities.Utilities;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

@SuppressWarnings("ConstantConditions") // Requires '.enableCache(CacheFlag.VOICE_STATE)' to be not null
public class MusicPlay implements CommandHandler {
    @CommandEvent(
            name = "play",
            aliases = {"p"},
            args = {"<song>"},
            emoji = "\uD83D\uDCBF",
            description = "description.music.play",
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            usage(ctx).setFooter(lang(ctx).get("command.music.info.platforms")).send();
            return;
        }

        if (Utilities.hasMusicError(ctx)) return; // Check for any music commands specific errors

        String song = ctx.getArgumentsRaw(); // Get song
        // If song is url
        if (Utilities.isValidURL(song)) {
            // Link is spotify playlist
            if (song.startsWith("https://open.spotify.com/playlist/")) {
                final String playlistId = song.split("/")[4].split("\\?")[0]; // Get playlist id
                final Playlist playlist = Spotify.getApi().getPlaylist(playlistId); // Get playlist
                PlayerManager.getInstance().loadAndPlay(ctx, null, true, playlist); // Add playlist to queue
            }
            // Link is from other audio source
            else {
                PlayerManager.getInstance().loadAndPlay(ctx, song, true, null); // Play song
            }
        }
        // If song is given by name
        else {
            PlayerManager.getInstance().loadAndPlay(ctx, String.format("ytsearch:%s", song), false, null); // Play song}
        }
    }
}