package com.github.m5rian.myra.commands.moderation;


import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.permissions.Moderator;
import net.dv8tion.jda.api.entities.Member;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Kick implements CommandHandler {
    @CommandEvent(
            name = "kick",
            aliases = {"kek"},
            args = {"<member>"},
            emoji = "\uD83D\uDCE4",
            description = "description.mod.kick",
            requires = Moderator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("kick")
                    .addUsages(new Usage()
                            .setUsage("kick <user> (reason)")
                            .setEmoji("\uD83D\uDCE4")
                            .setDescription(lang(ctx).get("description.mod.kick")))
                    .send();
            return;
        }

        // Get provided member
        final Member member = Utilities.getModifiedMember(ctx.getEvent(), ctx.getArguments()[0], "kick", "\uD83D\uDCE4"); // Get member
        if (member == null) return;

        final String reason = ctx.getArguments().length == 1 ? "none" : ctx.getArgumentsRaw().split("\\s+", 2)[1]; // Get reason
        // Prepare success message
        final Success success = new Success(ctx.getEvent())
                .setCommand("ban")
                // Member who executed the ban
                .setFooter(lang(ctx).get("command.mod.info.requestBy")
                                .replace("{$member}", ctx.getAuthor().getAsTag()),
                        ctx.getAuthor().getEffectiveAvatarUrl())
                // Add reason
                .addField("\uD83D\uDCC4 │ " + lang(ctx).get("word.reason"), reason)
                .addTimestamp();

        // Guild message
        success.setMessage(lang(ctx).get("command.mod.kick.info.guild")
                .replace("{$member}", member.getAsMention()))
                .send();

        member.getUser().openPrivateChannel().queue(channel -> {
            success.setMessage(lang(ctx).get("command.mod.kick.info.dm")
                    .replace("{$member}", member.getAsMention()))
                    .setAvatar(ctx.getGuild().getIconUrl())
                    .setChannel(channel)
                    .send();
        });

        member.kick(reason).queue(); // Kick member
    }
}
