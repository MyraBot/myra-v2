package com.github.m5rian.myra.commands.moderation.mute;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.permissions.Moderator;
import net.dv8tion.jda.api.entities.Member;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Unmute implements CommandHandler {

    @CommandEvent(
            name = "unmute",
            args = {"<member>"},
            emoji = "\uD83D\uDD08",
            description = "description.mod.unmute",
            requires = Moderator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("unmute")
                    .addUsages(new Usage()
                            .setUsage("unmute <member>")
                            .setEmoji("\uD83D\uDD08")
                            .setDescription(lang(ctx).get("description.mod.unmute")))
                    .send();
            return;
        }

        // Get provided member
        final Member member = Utilities.getModifiedMember(ctx.getEvent(), ctx.getArguments()[0], "Unmute", "\uD83D\uDD08"); // Get member
        if (member == null) return;

        final String muteRoleId = MongoGuild.get(ctx.getGuild()).getString("muteRole"); // Get mute role id
        // No mute role set
        if (muteRoleId.equals("not set")) {
            new Error(ctx.getEvent())
                    .setCommand("unmute")
                    .setEmoji("\uD83D\uDD08")
                    .setMessage(lang(ctx).get("command.mod.mute.error.muteRole"))
                    .send();
            return;
        }
        // User is already muted
        if (!member.getRoles().contains(ctx.getGuild().getRoleById(muteRoleId))) {
            new Error(ctx.getEvent())
                    .setCommand("unmute")
                    .setEmoji("\uD83D\uDD08")
                    .setMessage(lang(ctx).get("command.mod.unmute.error.notMuted"))
                    .send();
            return;
        }

        // Prepare success message
        final Success success = new Success(null)
                .setCommand("unmute")
                .setEmoji("\uD83D\uDD08")
                .addTimestamp();

        // Guild message
        success.setAvatar(member.getUser().getEffectiveAvatarUrl())
                .setMessage(lang(ctx).get("command.mod.unmute.info.guild")
                        .replace("{$member.mention}", member.getAsMention()))
                .setChannel(ctx.getChannel())
                .send();
        // Direct message
        member.getUser().openPrivateChannel().queue(channel -> {
            success.setMessage(lang(ctx).get("command.mod.unmute.info.dm")
                    .replace("{$guild}", ctx.getGuild().getName()))
                    .setAvatar(ctx.getGuild().getIconUrl())
                    .setChannel(channel)
                    .send();
        });

        ctx.getGuild().removeRoleFromMember(member, ctx.getGuild().getRoleById(muteRoleId)).queue(); // Unmute
    }
}