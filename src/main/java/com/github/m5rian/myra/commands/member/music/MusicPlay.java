package com.github.m5rian.myra.commands.member.music;


import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.APIs.LavaPlayer.PlayerManager;
import com.github.m5rian.myra.utilities.APIs.spotify.Playlist;
import com.github.m5rian.myra.utilities.APIs.spotify.Spotify;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.language.Lang;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.concurrent.TimeUnit;

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
            new CommandUsage(ctx.getEvent())
                    .setCommand("play")
                    .addUsages(new Usage()
                            .setUsage("play <song>")
                            .setEmoji("\uD83D\uDCBF")
                            .setDescription(Lang.lang(ctx).get("description.music.play")))
                    .addInformation(Lang.lang(ctx).get("command.music.info.platforms"))
                    .send();
            return;
        }

        final AudioManager audioManager = ctx.getGuild().getAudioManager();
        // Bot hasn't joined voice channel yet
        if (!audioManager.isConnected()) {
            // Missing permissions to connect
            if (!ctx.getGuild().getSelfMember().hasPermission(ctx.getMember().getVoiceState().getChannel(), Permission.VOICE_CONNECT)) {
                new Error(ctx.getEvent())
                        .setCommand("play")
                        .setEmoji("\uD83D\uDCBF")
                        .setMessage(Lang.lang(ctx).get("command.music.join.error.missingPermission"))
                        .send();
                return;
            }
            audioManager.openAudioConnection(ctx.getMember().getVoiceState().getChannel()); // Connect to voice channel
        }
        // Author isn't in a voice channel yet
        if (!ctx.getEvent().getMember().getVoiceState().inVoiceChannel()) {
            new Error(ctx.getEvent())
                    .setCommand("leave")
                    .setEmoji("\uD83D\uDCE4")
                    .setMessage(Lang.lang(ctx).get("command.music.error.memberNotInVoiceChannel")).send();
            return;
        }
        // Author isn't in the same voice channel as bot
        if (audioManager.isConnected() && !audioManager.getConnectedChannel().getMembers().contains(ctx.getEvent().getMember())) {
            audioManager.getConnectedChannel().createInvite().timeout(15, TimeUnit.MINUTES).queue(invite -> {
                new Error(ctx.getEvent())
                        .setCommand("leave")
                        .setEmoji("\uD83D\uDCE4")
                        .setMessage(Lang.lang(ctx).get("command.music.error.alreadyConnected")
                                .replace("{$channel}", invite.getChannel().getName()) // Channel name
                                .replace("{$invite}", invite.getUrl())) // Invite url
                        .send();
            });
            return;
        }

        // Get song
        String song = ctx.getArgumentsRaw();
        // If song is url
        if (Utilities.isValidURL(song)) {
            // Link is spotify playlist
            if (song.startsWith("https://open.spotify.com/playlist/")) {
                final String playlistId = song.split("/")[4].split("\\?")[0]; // Get playlist id
                final Playlist playlist = Spotify.getApi().getPlaylist(playlistId); // Get playlist
                PlayerManager.getInstance().loadAndPlay(ctx.getEvent().getMessage(), null, true, playlist); // Add playlist to queue
            }
            // Link is from other audio source
            else {
                PlayerManager.getInstance().loadAndPlay(ctx.getEvent().getMessage(), song, true, null); // Play song
            }
        }
        // If song is given by name
        else {
            PlayerManager.getInstance().loadAndPlay(ctx.getEvent().getMessage(), String.format("ytsearch:%s", song), false, null); // Play song}
        }

        ctx.getEvent().getMessage().delete().queue(null, new ErrorHandler() // Delete message
                .ignore(InsufficientPermissionException.class));  // Ignore missing permissions exceptions
    }
}