package com.github.m5rian.myra.commands.moderation.ban;


import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.permissions.Moderator;
import net.dv8tion.jda.api.entities.Member;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Ban implements CommandHandler {

    @CommandEvent(
            name = "ban",
            aliases = {"bean"},
            args = {"<member>", "(reason)"},
            emoji = "\uD83D\uDD12",
            description = "description.mod.ban",
            requires = Moderator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("ban")
                    .addUsages(new Usage()
                            .setUsage("ban <user> (reason)")
                            .setEmoji("\uD83D\uDD12")
                            .setDescription(lang(ctx).get("description.mod.ban")))
                    .send();
            return;
        }

        // Get provided member
        final Member member = Utilities.getModifiedMember(ctx.getEvent(), ctx.getArguments()[0], "ban", "\uD83D\uDD12"); // Get member
        if (member == null) return;

        final String reason = ctx.getArguments().length == 1 ? "none" : ctx.getArgumentsRaw().split("\\s+", 2)[1]; // Get reason
        // Prepare message
        final Success success = new Success(ctx.getEvent())
                .setCommand("ban")
                // Member who executed the ban
                .setFooter(lang(ctx).get("command.mod.info.requestBy")
                                .replace("{$member}", ctx.getAuthor().getAsTag()),
                        ctx.getAuthor().getEffectiveAvatarUrl())
                // Add reason
                .addField("\uD83D\uDCC4 │ " + lang(ctx).get("word.reason"), reason)
                .addTimestamp();

        // Send guild message
        success.setMessage(lang(ctx).get("command.mod.ban.success.guild")
                .replace("{$member}", member.getAsMention())) // Member who got banned
                .send(); // Send in current channel
        // Send direct message
        member.getUser().openPrivateChannel().queue(channel -> {
            success.setMessage(lang(ctx).get("command.mod.ban.success.dm")
                    .replace("{$guild}", ctx.getGuild().getName())) // Member who got banned
                    .setChannel(channel) // Set channel to direct message
                    .send();
        });

        member.ban(7, reason).queue(); // Ban member
    }
}