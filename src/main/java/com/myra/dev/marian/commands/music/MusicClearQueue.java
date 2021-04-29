package com.myra.dev.marian.commands.music;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.TrackScheduler;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class MusicClearQueue implements CommandHandler {

@CommandEvent(
        name = "clear queue",
        aliases = {"queue clear"},
        channel = Channel.GUILD
)
    public void execute(CommandContext ctx) throws Exception {

        if (ctx.getArguments().length != 0) return; // Check for no arguments

        final TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(ctx.getGuild()).scheduler; // Get track scheduler

        // Bot isn't connected to a voice channel
        if (!ctx.getGuild().getAudioManager().isConnected()) {
            new Error(ctx.getEvent())
                    .setCommand("clear queue")
                    .setEmoji("\uD83D\uDDD1")
                    .setMessage("I'm not connected to a voice channel")
                    .send();
            return;
        }
        // No audio track is playing
        if (PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack() == null) {
            new Error(ctx.getEvent())
                    .setCommand("clear queue")
                    .setEmoji("\uD83D\uDDD1")
                    .setMessage("The player isn't playing any song")
                    .send();
            return;
        }

        final int size = (int) ctx.getMember().getVoiceState().getChannel().getMembers().stream().filter(member -> !member.getUser().isBot()).count();

        // Skip song
        if (size <= 4 || !new MongoGuild(ctx.getGuild()).getBoolean("musicVoting")) {
            clearQueue(scheduler, ctx.getChannel(), ctx.getAuthor());
        }

        // Only start voting if more than 4 members are in the voice call and music voting is enabled
        else new MusicVoteListener().onMusicCommand(ctx.getEvent().getMessage()); // Start voting
    }

    public void clearQueue(TrackScheduler scheduler, MessageChannel channel, User author) {
        scheduler.getQueue().clear(); // Clear queue
        // Success message
        new Success(null)
                .setCommand("clear queue")
                .setEmoji("\uD83D\uDDD1")
                .setAvatar(author.getEffectiveAvatarUrl())
                .setMessage("All songs have been removed from the queue")
                .setChannel(channel)
                .send();
    }
}
