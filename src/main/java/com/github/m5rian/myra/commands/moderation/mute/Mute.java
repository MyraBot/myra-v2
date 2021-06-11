package com.github.m5rian.myra.commands.moderation.mute;


import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
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

public class Mute implements CommandHandler {

    @CommandEvent(
            name = "mute",
            args = {"<user>", "(reason)"},
            emoji = "\uD83D\uDCDD",
            description = "description.mod.mute",
            requires = Moderator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length > 0 && ctx.getArguments()[0].equalsIgnoreCase("role")) return; // Mute role command was used

        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("tempban")
                    .addUsages(new Usage()
                            .setUsage("mute <user> (reason)")
                            .setEmoji("\uD83D\uDD07")
                            .setDescription(lang(ctx).get("description.mod.mute")))
                    .send();
            return;
        }

        // Get provided member
        final Member member = Utilities.getModifiedMember(ctx.getEvent(), ctx.getArguments()[0], "mute", "\uD83D\uDD07"); // Get member
        if (member == null) return;

        final String muteRoleId = new MongoGuild(ctx.getGuild()).getString("muteRole"); // Get mute role id
        // No mute role set
        if (muteRoleId.equals("not set")) {
            new Error(ctx.getEvent())
                    .setCommand("mute")
                    .setEmoji("\uD83D\uDD07")
                    .setMessage(lang(ctx).get("command.mod.mute.error.muteRole")
                            .replace("{$prefix}", ctx.getPrefix()))
                    .send();
            return;
        }
        // User is already muted
        if (member.getRoles().contains(ctx.getGuild().getRoleById(muteRoleId))) {
            new Error(ctx.getEvent())
                    .setCommand("mute")
                    .setEmoji("\uD83D\uDD07")
                    .setMessage(lang(ctx).get("command.mod.mute.error.muted"))
                    .send();
            return;
        }

        final String reason = ctx.getArguments().length == 1 ? "none" : ctx.getArgumentsRaw().split("\\s+", 2)[1]; // Get reason
        // Prepare message
        final Success success = new Success(ctx.getEvent())
                .setCommand("ban")
                .setFooter(lang(ctx).get("command.mod.info.requestBy").replace("{$member}", ctx.getAuthor().getAsTag()), ctx.getAuthor().getEffectiveAvatarUrl())
                .addField("\uD83D\uDCC4 │ " + lang(ctx).get("word.reason"), reason) // Add reason
                .addTimestamp();

        // Guild message
        success.setMessage(lang(ctx).get("command.mod.mute.info.guild")
                .replace("{$member}", member.getAsMention()))
                .send();

        member.getUser().openPrivateChannel().queue(channel -> {
            success.setMessage(lang(ctx).get("command.mod.mute.info.dm")
                    .replace("{$guild}", ctx.getGuild().getName())) // Guild name
                    .setChannel(channel)
                    .send();
        });

        // Mute member
        ctx.getGuild().addRoleToMember(member, ctx.getGuild().getRoleById(muteRoleId)).queue();
    }
}
