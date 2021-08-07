package com.github.m5rian.myra.commands.member.music;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.myra.utilities.Utilities;
import net.dv8tion.jda.api.entities.VoiceChannel;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class MusicLeave implements CommandHandler {
    @SuppressWarnings("ConstantConditions")

    @CommandEvent(
            name = "leave",
            aliases = {"disconnect"},
            emoji = "\uD83D\uDCE4",
            description = "description.music.disconnect",
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return; // Check for no arguments
        if (Utilities.hasMusicError(ctx)) return; // Check for any music commands specific errors

        final VoiceChannel voiceChannel = ctx.getGuild().getAudioManager().getConnectedChannel(); // Get connected channel
        ctx.getGuild().getAudioManager().closeAudioConnection(); // Leave from current channel
        info(ctx).setDescription(lang(ctx).get("command.music.leave.info.done")
                .replace("{$channel.mention}", Utilities.mentionChannel(voiceChannel.getId()))) // Voice channel mention
                .send();
    }
}
