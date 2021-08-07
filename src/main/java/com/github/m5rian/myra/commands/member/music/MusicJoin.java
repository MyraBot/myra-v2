package com.github.m5rian.myra.commands.member.music;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.CommandUtils;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.myra.utilities.Utilities;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.VoiceChannel;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class MusicJoin implements CommandHandler {

    @CommandEvent(
            aliases = {"connect"},
            name = "join",
            emoji = "\uD83D\uDCE5",
            description = "description.music.join",
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return; // Check for no arguments
        if (!Utilities.hasPerms(ctx, Permission.VOICE_CONNECT)) return; // Check for required permissions

        // Bot is already in a voice call
        if (ctx.getGuild().getAudioManager().isConnected()) {
            final VoiceChannel voiceChannel = ctx.getGuild().getAudioManager().getConnectedChannel(); // Get voice channel the bot is in
            CommandUtils.errorFactory.invoke(ctx).setDescription(lang(ctx).get("command.music.error.alreadyConnected")
                    .replace("{$channel.mention}", Utilities.mentionChannel(voiceChannel.getId()))) // Channel mention
                    .send();
            return;
        }

        final VoiceChannel voiceChannel = ctx.getMember().getVoiceState().getChannel(); // Get voice channel to connect
        ctx.getGuild().getAudioManager().openAudioConnection(voiceChannel); // Open audio connection
        info(ctx).setDescription(lang(ctx).get("command.music.join.info.done")
                .replace("{$channel.mention}", Utilities.mentionChannel(voiceChannel.getId()))) // Voice channel mention
                .send();
    }
}
