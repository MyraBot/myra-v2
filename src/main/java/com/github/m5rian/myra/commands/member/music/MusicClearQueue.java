package com.github.m5rian.myra.commands.member.music;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.APIs.LavaPlayer.PlayerManager;
import com.github.m5rian.myra.utilities.APIs.LavaPlayer.TrackScheduler;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.language.Lang;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

public class MusicClearQueue implements CommandHandler {

    @CommandEvent(
            name = "clear queue",
            aliases = {"queue clear"},
            emoji = "\uD83D\uDDD1",
            description = "description.music.clearQueue",
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return; // Check for no arguments

        final TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(ctx.getGuild()).scheduler; // Get track scheduler
        if (Utilities.hasMusicError(ctx)) return; // Check for errors
        // No audio track is playing
        if (PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack() == null) {
            new Error(ctx.getEvent())
                    .setCommand("clear queue")
                    .setEmoji("\uD83D\uDDD1")
                    .setMessage(Lang.lang(ctx).get("command.music.error.notPlaying"))
                    .send();
            return;
        }

        final long inVc = ctx.getMember().getVoiceState().getChannel().getMembers().stream().filter(member -> !member.getUser().isBot()).count(); // Get people in voice call without bots
        // Music voting is active
        if (inVc > 4 && new MongoGuild(ctx.getGuild()).getBoolean("musicVoting")) {
            new MusicVoteListener().onMusicCommand(ctx.getEvent().getMessage()); // Start voting
        }
        // Directly clear queue
        else clearQueue(scheduler, ctx.getChannel(), ctx.getMember()); // Clear queue
    }

    public void clearQueue(TrackScheduler scheduler, MessageChannel channel, Member author) {
        scheduler.getQueue().clear(); // Clear queue
        // Success message
        new Success(null)
                .setCommand("clear queue")
                .setEmoji("\uD83D\uDDD1")
                .setAvatar(author.getUser().getEffectiveAvatarUrl())
                .setMessage(Lang.lang(author).get("command.music.clearQueue.success"))
                .setChannel(channel)
                .send();
    }
}
