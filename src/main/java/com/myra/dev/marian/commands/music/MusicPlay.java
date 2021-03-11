package com.myra.dev.marian.commands.music;


import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.APIs.spotify.Playlist;
import com.myra.dev.marian.utilities.APIs.spotify.Spotify;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("ConstantConditions") // Requires '.enableCache(CacheFlag.VOICE_STATE)' to be not null
@CommandSubscribe(
        name = "play",
        channel = Channel.GUILD
)
public class MusicPlay implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        //command usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("play", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "play <song>`", "\uD83D\uDCBF │ add a song to the queue*", false)
                    .setFooter("supported platforms: YoutTube, SoundCloud, Bandcamp, Vimeo, Twitch streams");
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
// Add a audio track to the queue
        // Member isn't in a voice call
        if (!ctx.getEvent().getMember().getVoiceState().inVoiceChannel()) {
            new Error(ctx.getEvent())
                    .setCommand("play")
                    .setEmoji("\uD83D\uDCBF")
                    .setMessage("You need to join a voice channel first to use this command")
                    .send();
            return;
        }
        // Member isn't in the same voice call as bot
        if (ctx.getGuild().getAudioManager().isConnected() && !ctx.getEvent().getMember().getVoiceState().getChannel().equals(ctx.getGuild().getAudioManager().getConnectedChannel())) {
            new Error(ctx.getEvent())
                    .setCommand("play")
                    .setEmoji("\uD83D\uDCBF")
                    .setMessage("You need to join the same voice channel as me")
                    .send();
            return;
        }

        if (!ctx.getGuild().getAudioManager().isConnected())
            ctx.getGuild().getAudioManager().openAudioConnection(ctx.getMember().getVoiceState().getChannel());
        // Get song
        String song = ctx.getArgumentsRaw();

        // If song is url
        if (isValidURL(song)) {
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

        ctx.getEvent().getMessage().delete().queue(); // Delete message
    }

    private boolean isValidURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}