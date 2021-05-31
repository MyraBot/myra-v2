package com.github.m5rian.myra.commands.member.music;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.Utilities;

import java.util.concurrent.TimeUnit;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class MusicLeave implements CommandHandler {
    @SuppressWarnings("ConstantConditions")

    @CommandEvent(
            name = "leave",
            aliases = {"disconnect"},
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return; // Check for no arguments

        if (Utilities.hasMusicError(ctx)) return; // Check for errors
        ctx.getGuild().getAudioManager().closeAudioConnection(); // Leave from current channel
        // Success message
        ctx.getGuild().getAudioManager().getConnectedChannel().createInvite().timeout(15, TimeUnit.MINUTES).queue(invite -> {
            new Success(ctx.getEvent())
                    .setCommand("leave")
                    .setEmoji("\uD83D\uDCE4")
                    .setHyperLink(invite.getUrl())
                    .setMessage(Lang.lang(ctx).get("command.music.leave.info.done")
                            .replace("{$channel}", invite.getChannel().getName()) // Channel name
                            .replace("{$invite}", invite.getUrl())) // Invite url
                    .send();
        });
    }
}