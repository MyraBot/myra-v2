package com.github.m5rian.myra.commands.member.music;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.language.Lang;
import net.dv8tion.jda.api.Permission;

import java.util.concurrent.TimeUnit;

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

        // Bot is already in a voice call
        if (ctx.getGuild().getAudioManager().isConnected()) {
            ctx.getGuild().getAudioManager().getConnectedChannel().createInvite().timeout(15, TimeUnit.MINUTES).queue(invite -> {
                new Error(ctx.getEvent())
                        .setCommand("join")
                        .setEmoji("\uD83D\uDCE5")
                        .setMessage(Lang.lang(ctx).get("command.music.error.alreadyConnected")
                                .replace("{$channel}", invite.getChannel().getName()) // Channel name
                                .replace("{$invite}", invite.getUrl())) // Channel url
                        .send();
            });
            return;
        }
        // Missing permissions to connect
        if (!ctx.getGuild().getSelfMember().hasPermission(ctx.getMember().getVoiceState().getChannel(), Permission.VOICE_CONNECT)) {
            new Error(ctx.getEvent())
                    .setCommand("join")
                    .setEmoji("\uD83D\uDCE5")
                    .setMessage(Lang.lang(ctx).get("command.music.join.error.missingPermission"))
                    .send();
            return;
        }


        ctx.getGuild().getAudioManager().openAudioConnection(ctx.getMember().getVoiceState().getChannel()); // Open audio connection
        // Send success message
        ctx.getMember().getVoiceState().getChannel().createInvite().timeout(15, TimeUnit.MINUTES).queue(invite -> {
            new Success(ctx.getEvent())
                    .setCommand("join")
                    .setEmoji("\uD83D\uDCE5")
                    .setHyperLink(invite.getUrl())
                    .setMessage(Lang.lang(ctx).get("command.music.join.info.done")
                            .replace("{$channel}", invite.getChannel().getName()) // Channel name
                            .replace("{$invite}", invite.getUrl())) // Invite url
                    .send();
        });
    }
}
