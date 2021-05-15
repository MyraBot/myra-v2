package com.myra.dev.marian.commands.music;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.GuildMusicManager;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.Equalizer;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Collections;
import java.util.List;

public class MusicFilters implements CommandHandler {
    @CommandEvent(
            name = "filters",
            aliases = {"filter"}
    )
    public void onFilterCommand(CommandContext ctx) {
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild()); // Get guild specific music manager

        musicManager.audioPlayer.setFilterFactory((this::nightcore));

        new Success(ctx.getEvent())
                .setCommand("filters")
                .setEmoji("\uD83C\uDFB7")
                .setMessage("Activated filter!")
                .send();
    }

    private List nightcore(AudioTrack track, AudioDataFormat format, UniversalPcmAudioFilter output) {
        final TimescalePcmAudioFilter timescale = new TimescalePcmAudioFilter(output, format.channelCount, format.sampleRate);
        timescale.setPitch(1.25);
        timescale.setSpeed(1.25);

        return Collections.singletonList(timescale);
    }

}
