package com.myra.dev.marian.commands.moderation.ban;


import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Moderator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.time.Instant;

@CommandSubscribe(
        name = "ban",
        aliases = {"bean"},
        requires = Moderator.class
)
public class Ban implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        Utilities utilities = Utilities.getUtils(); // Get utilities
        // Command usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("ban", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "ban <user> <reason>`", "\uD83D\uDD12 │ Ban a specific member", false)
                    .setFooter("You don't have to give a reason.");
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
// Ban user
        final Member member = utilities.getModifiedMember(ctx.getEvent(), ctx.getArguments()[0], "ban", "\uD83D\uDD12"); // Get member
        if (member == null) return;

        final User user = member.getUser(); // Get member as user
        // Guild message
        EmbedBuilder guildMessageBan = new EmbedBuilder()
                .setAuthor(user.getAsTag() + " got banned", null, user.getEffectiveAvatarUrl())
                .setColor(utilities.red)
                .setDescription("\uD83D\uDD12 │ " + user.getAsMention() + " got banned on `" + ctx.getGuild().getName() + "`")
                .setFooter("requested by " + ctx.getAuthor().getAsTag(), ctx.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        // Direct message
        EmbedBuilder directMessageBan = new EmbedBuilder()
                .setAuthor("You got banned", null, ctx.getGuild().getIconUrl())
                .setColor(utilities.red)
                .setDescription("\uD83D\uDD12 │ You got banned from `" + ctx.getGuild().getName() + "`")
                .setFooter("requested by " + ctx.getAuthor().getAsTag(), ctx.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        
        // No reason is given
        if (ctx.getArguments().length == 1) {
            guildMessageBan.addField("\uD83D\uDCC4 │ no reason", "there was no reason given", false); // Set reason to none
            directMessageBan.addField("\uD83D\uDCC4 │ no reason", "there was no reason given", false); // Set reason to none
            // Send messages
            ctx.getChannel().sendMessage(guildMessageBan.build()).queue(); // Guild message
            user.openPrivateChannel().queue((channel) -> { // Direct message
                channel.sendMessage(directMessageBan.build()).queue();
            });
            // ban
            member.ban(7).queue(); // Without reason
        }
        //with reason
        else {
            final String reason = ctx.getArgumentsRaw().split("\\s+", 2)[1]; // Get reason

            guildMessageBan.addField("\uD83D\uDCC4 │ reason:", reason, false); // Add reason
            directMessageBan.addField("\uD83D\uDCC4 │ reason:", reason, false); // Add reason
            // Send messages
            ctx.getChannel().sendMessage(guildMessageBan.build()).queue(); // Guild message
            user.openPrivateChannel().queue((channel) -> { // Direct message
                channel.sendMessage(directMessageBan.build()).queue();
            });
            // ban
            ctx.getGuild().getMember(user).ban(7, reason).queue(); // Add reason
        }
    }
}