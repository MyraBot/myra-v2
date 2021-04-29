package com.myra.dev.marian.commands.music;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class MusicSkip implements CommandHandler {

@CommandEvent(
        name = "skip",
        aliases = {"next"},
        channel = Channel.GUILD
)
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return; // Check for no arguments

        // Errors
        if (!ctx.getGuild().getAudioManager().isConnected()) { // Bot isn't connected to a voice channel
            new Error(ctx.getEvent())
                    .setCommand("skip")
                    .setEmoji("\u23ED\uFE0F")
                    .setMessage("I'm not connected to a voice channel")
                    .send();
            return;
        }
        if (PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack() == null) { // No audio track is playing
            new Error(ctx.getEvent())
                    .setCommand("skip")
                    .setEmoji("\u23ED\uFE0F")
                    .setMessage("The player isn't playing any song")
                    .send();
            return;
        }


        final int size = (int) ctx.getMember().getVoiceState().getChannel().getMembers().stream().filter(member -> !member.getUser().isBot()).count();

        // Skip song
        if (size <= 4 || !new MongoGuild(ctx.getGuild()).getBoolean("musicVoting")) {
            skip(ctx.getGuild(), ctx.getChannel(), ctx.getAuthor());
        }

        // Only start voting if more than 4 members are in the voice call and music voting is enabled
        else new MusicVoteListener().onMusicCommand(ctx.getEvent().getMessage()); // Start voting
    }

    public void skip(Guild guild, MessageChannel channel, User author) {
        final AudioTrack track = PlayerManager.getInstance().getMusicManager(guild).audioPlayer.getPlayingTrack(); // Get audio player
        // Send success message
        EmbedBuilder success = new EmbedBuilder()
                .setAuthor("skip", track.getInfo().uri, author.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("Skipped track: " + Utilities.getUtils().hyperlink(track.getInfo().title, track.getInfo().uri));
        channel.sendMessage(success.build()).queue();
        // Skip track
        PlayerManager.getInstance().getMusicManager(guild).scheduler.nextTrack();
    }
}
