package com.github.m5rian.myra.commands.member.music;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.APIs.LavaPlayer.PlayerManager;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.Utilities;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class MusicSkip implements CommandHandler {

    @CommandEvent(
            name = "skip",
            aliases = {"next"},
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return; // Check for no arguments

        if (Utilities.hasMusicError(ctx)) return; // Check for errors
        // No audio track is playing
        if (!ctx.getGuild().getAudioManager().isConnected()) { // Bot isn't connected to a voice channel
            new Error(ctx.getEvent())
                    .setCommand("skip")
                    .setEmoji("\u23ED\uFE0F")
                    .setMessage(lang(ctx).get("command.music.error.notPlaying"))
                    .send();
            return;
        }

        final int inVc = (int) ctx.getMember().getVoiceState().getChannel().getMembers().stream().filter(member -> !member.getUser().isBot()).count(); // Get people in voice call without bots
        // Music voting is active
        if (inVc > 4 && MongoGuild.get(ctx.getGuild()).getBoolean("musicVoting")) {
            new MusicVoteListener().onMusicCommand(ctx.getEvent().getMessage()); // Start voting
        }
        // Skip current song
        else skip(ctx.getGuild(), ctx.getChannel(), ctx.getMember());
    }

    public void skip(Guild guild, MessageChannel channel, Member author) {
        final AudioTrack track = PlayerManager.getInstance().getMusicManager(guild).audioPlayer.getPlayingTrack(); // Get audio player
        // Send success message
        new Success(null)
                .setCommand("skip")
                .setEmoji("\u23ED\uFE0F")
                .setAvatar(author.getUser().getEffectiveAvatarUrl())
                .setMessage(lang(author).get("command.music.skip.done")
                        .replace("{$name}", track.getInfo().title) // Title of track
                        .replace("{$url}", track.getInfo().uri)) // Url of track
                .setChannel(channel)
                .send();

        PlayerManager.getInstance().getMusicManager(guild).scheduler.nextTrack(); // Skip track
    }
}
