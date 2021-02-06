package com.myra.dev.marian.commands.moderation.ban;


import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Moderator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.time.Instant;

@CommandSubscribe(
        name = "unban",
        aliases = {"unbean"},
        requires = Moderator.class
)
public class Unban implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        Utilities utilities = Utilities.getUtils(); // Get utilities
        // Command usage
        if (ctx.getArguments().length == 1) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("│ unban", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "unban <user>`", "\uD83D\uDD13 │ unban a specific member", false);
            ctx.getChannel().sendMessage(embed.build()).queue();
            return;
        }
// Unban
        User user = utilities.getUser(ctx.getEvent(), ctx.getArguments()[0], "unban", "\uD83D\uDD13"); // Get member
        if (user == null) return;


        ctx.getGuild().retrieveBanList().queue(bans -> {
            // User isn't banned
            if (!bans.stream().anyMatch(ban -> ban.getUser().equals(user))) {
                new Error(ctx.getEvent())
                        .setCommand("unban")
                        .setEmoji("\uD83D\uDD13")
                        .setMessage("This user isn't banned")
                        .send();
                return;
            }


            // Guild message
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor(user.getAsTag() + " got unbanned", null, user.getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().blue)
                    .setDescription("\uD83D\uDD13 │ " + user.getAsMention() + " got unbanned from " + ctx.getGuild().getName())
                    .setFooter("requested by " + ctx.getAuthor().getAsTag(), ctx.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now());
            // Direct message
            EmbedBuilder directMessage = new EmbedBuilder()
                    .setAuthor("You got unbanned", null, ctx.getGuild().getIconUrl())
                    .setColor(Utilities.getUtils().blue)
                    .setDescription("\uD83D\uDD13 │ You got unbanned from `" + ctx.getGuild().getName() + "`")
                    .setFooter("requested by " + ctx.getAuthor().getAsTag(), ctx.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now());

            // Send messages
            ctx.getChannel().sendMessage(embed.build()).queue(); // Guild message
            user.openPrivateChannel().queue((channel) -> { // Direct message
                channel.sendMessage(directMessage.build()).queue();
            });

            ctx.getGuild().unban(user).queue(); // unban user
        });
    }
}