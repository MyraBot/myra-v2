package com.myra.dev.marian.commands.moderation.ban;


import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Moderator;
import net.dv8tion.jda.api.entities.User;

import static com.myra.dev.marian.utilities.language.Lang.lang;

public class Unban implements CommandHandler {
    @CommandEvent(
            name = "unban",
            aliases = {"unbean"},
            requires = Moderator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("unban")
                    .addUsages(new Usage()
                            .setUsage("unban <user>")
                            .setEmoji("\uD83D\uDD13")
                            .setDescription(lang(ctx).get("description.mod.unban")))
                    .send();
            return;
        }

        final User user = Utilities.getUser(ctx.getEvent(), ctx.getArguments()[0], "unban", "\uD83D\uDD13"); // Get member
        if (user == null) return;


        ctx.getGuild().retrieveBanList().queue(bans -> {
            // User isn't banned
            if (bans.stream().noneMatch(ban -> ban.getUser().equals(user))) {
                new Error(ctx.getEvent())
                        .setCommand("unban")
                        .setEmoji("\uD83D\uDD13")
                        .setMessage(lang(ctx).get("command.mod.unban.error.notBanned"))
                        .send();
                return;
            }

            // Prepare message
            final Success success = new Success(ctx.getEvent())
                    .setCommand("unban")
                    .setFooter(lang(ctx).get("command.mod.info.requestBy")
                                    .replace("{$member}", ctx.getAuthor().getAsTag()), // Member who executed the unban
                            ctx.getAuthor().getEffectiveAvatarUrl())
                    .addTimestamp();

            // Guild message
            success.setMessage(lang(ctx).get("command.mod.unban.info.guild")
                    .replace("{$user}", user.getAsTag())) // Member to unban
                    .send();
            // Direct message
            user.openPrivateChannel().queue(channel -> {
                success.setMessage(lang(ctx).get("command.mod.unban.info.guild")
                        .replace("{$guild}", ctx.getGuild().getName())) // Guild name
                        .setChannel(channel)
                        .send();
            });

            ctx.getGuild().unban(user).queue(); // unban user
        });
    }
}