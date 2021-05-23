package com.myra.dev.marian.commands.music;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.natanbc.lavadsp.karaoke.KaraokePcmAudioFilter;
import com.github.natanbc.lavadsp.rotation.RotationPcmAudioFilter;
import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.GuildMusicManager;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * {@link EqualizerFactory} explanation
 * it has 15 bands (0-14) that can be adjusted:
 * 25 Hz, 40 Hz, 63 Hz, 100 Hz, 160 Hz, 250 Hz, 400 Hz, 630 Hz, 1k Hz, 1.6k Hz, 2.5k Hz, 4k Hz, 6.3k Hz, 10k Hz, 16k Hz
 * <p>
 * the meaningful range for the values is -0.25 to 0.5, -0.25 means you're completely wiping out that frequency, 0.25 means you're doubling it
 */
public class MusicFilters implements CommandHandler {
    final String[] emojis = {
            "\uD83D\uDEAB", // Off
            "\uD83C\uDF11", // Nightcore
            "\uD83C\uDFA7", // 8D
            "\uD83C\uDFA4", // Karaoke
            "\uD83D\uDD0A", // Bass boost
            "\uD83D\uDCA5" // Ear cancer
    };

    @CommandEvent(
            name = "filters",
            aliases = {"filter"},
            channel = Channel.GUILD
    )
    public void onFilterCommand(CommandContext ctx) {
        final Success selection = new Success(ctx.getEvent())
                .setCommand("filters")
                .setEmoji("\uD83C\uDFB7")
                .setMessage("**Select one filter**" +
                        "\n• " + emojis[0] + "Off" +
                        "\n• " + emojis[1] + "Nightcore" +
                        "\n• " + emojis[2] + "8D" +
                        "\n• " + emojis[3] + "Karaoke" +
                        "\n• " + emojis[4] + "Bass boost" +
                        "\n• " + emojis[5] + "Ear cancer");
        ctx.getChannel().sendMessage(selection.getEmbed().build()).queue(message -> {
            message.addReaction(emojis[0]).queue(); // Off
            message.addReaction(emojis[1]).queue(); // Nightcore
            message.addReaction(emojis[2]).queue(); // 8D
            message.addReaction(emojis[3]).queue(); // Karaoke
            message.addReaction(emojis[4]).queue(); // Bass boost
            message.addReaction(emojis[5]).queue(); // Ear cancer

            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e -> !e.getUser().isBot()
                            && e.getUserIdLong() == ctx.getAuthor().getIdLong()
                            && e.getMessageIdLong() == message.getIdLong()
                            && e.getReactionEmote().isEmoji() // Only accept emojis
                            && Arrays.asList(emojis).contains(e.getReactionEmote().getEmoji()))
                    .setAction(e -> {
                        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild()); // Get guild specific music manager
                        final String emoji = e.getReactionEmote().getEmoji();

                        // Prepare success message
                        final Success filter = new Success(ctx.getEvent())
                                .setCommand("filters")
                                .setEmoji("\uD83C\uDFB7");
                        // Off
                        if (emoji.equals(emojis[0])) {
                            musicManager.audioPlayer.setFilterFactory(null); // Remove filters
                            filter.setMessage("disabled filters");
                        }
                        // Nightcore
                        else if (emoji.equals(emojis[1])) {
                            musicManager.audioPlayer.setFilterFactory(this::nightcore);
                            filter.setMessage("Added Nightcore filter");
                        }
                        // 8D
                        else if (emoji.equals(emojis[2])) {
                            musicManager.audioPlayer.setFilterFactory(this::dimensional);
                            filter.setMessage("Applied 8D effect");
                        }
                        // 8D
                        else if (emoji.equals(emojis[3])) {
                            musicManager.audioPlayer.setFilterFactory(this::karaoke);
                            filter.setMessage("Time to sing to the song");
                        }
                        // Bass boost
                        else if (emoji.equals(emojis[4])) {
                            musicManager.audioPlayer.setFilterFactory(bassBoostExtreme());
                            filter.setMessage("Added bass boost");
                        }
                        // Ear cancer
                        else if (emoji.equals(emojis[5])) {
                            musicManager.audioPlayer.setFilterFactory(bassBoostFuck());
                            filter.setMessage("u h");
                        }

                        message.editMessage(filter.getEmbed().build()).queue(); // Edit message
                        message.clearReactions().queue();
                    })
                    .setTimeout(30, TimeUnit.SECONDS)
                    .setTimeoutAction(() -> message.clearReactions().queue())
                    .load();
        });
    }

    private List nightcore(AudioTrack track, AudioDataFormat format, UniversalPcmAudioFilter output) {
        final TimescalePcmAudioFilter timescale = new TimescalePcmAudioFilter(output, format.channelCount, format.sampleRate);
        timescale.setPitch(1.25);
        timescale.setSpeed(1.15);

        return Collections.singletonList(timescale);
    }

    private List dimensional(AudioTrack track, AudioDataFormat format, UniversalPcmAudioFilter output) {
        final RotationPcmAudioFilter rotation = new RotationPcmAudioFilter(output, format.sampleRate)
                .setRotationSpeed(0.1d);

        return Collections.singletonList(rotation);
    }

    private List karaoke(AudioTrack track, AudioDataFormat format, UniversalPcmAudioFilter output) {
        final KaraokePcmAudioFilter karaoke = new KaraokePcmAudioFilter(output, format.channelCount, format.sampleRate)
                .setFilterBand(500f)
                .setFilterWidth(100f);

        return Collections.singletonList(karaoke);
    }

    private EqualizerFactory bassBoostExtreme() {
        final EqualizerFactory eqFactory = new EqualizerFactory();
        eqFactory.setGain(1, 0.15f);
        eqFactory.setGain(2, 0.5f);
        eqFactory.setGain(3, 0.15f);
        eqFactory.setGain(4, 0.05f);

        return eqFactory;
    }

    private EqualizerFactory bassBoostFuck() {
        final EqualizerFactory eqFactory = new EqualizerFactory();
        eqFactory.setGain(0, 0.75f);
        eqFactory.setGain(1, 1.5f);
        eqFactory.setGain(2, 1.25f);
        eqFactory.setGain(3, 0.75f);
        eqFactory.setGain(4, 0.5f);
        return eqFactory;
    }

}
