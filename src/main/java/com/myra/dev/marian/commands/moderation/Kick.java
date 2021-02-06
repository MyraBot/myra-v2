package com.myra.dev.marian.commands.moderation;


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
        name = "kick",
        aliases = {"kek"},
        requires = Moderator.class
)
public class Kick implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        Utilities utilities = Utilities.getUtils(); // Get utilities
        // Command usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("kick", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "kick <user> <reason>`", "\uD83D\uDCE4 │ kick a specific member", false);
            ctx.getChannel().sendMessage(embed.build()).queue();
            return;
        }
// Kick member
        final Member member = utilities.getModifiedMember(ctx.getEvent(), ctx.getArguments()[0], "kick", "\uD83D\uDCE4"); // Get member
        if (member == null) return;

        final String reason = ctx.getArgumentsRaw().split("\\s+", 2)[1]; // Get reason

        final User user = member.getUser(); // Get member as user
        // Guild message
        EmbedBuilder guildMessage = new EmbedBuilder()
                .setAuthor(user.getAsTag() + " got kicked", null, user.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().red)
                .setDescription(user.getAsMention() + " got kicked from " + ctx.getGuild().getName())
                .setFooter("requested by " + ctx.getAuthor().getAsTag(), ctx.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        // Direct message
        EmbedBuilder directMessage = new EmbedBuilder()
                .setAuthor("You got kicked from " + ctx.getGuild().getName(), null, ctx.getGuild().getIconUrl())
                .setColor(Utilities.getUtils().red)
                .setDescription("You got kicked from " + ctx.getGuild().getName())
                .setFooter("requested by " + ctx.getAuthor().getAsTag(), ctx.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        // No reason is given
        if (ctx.getArguments().length == 1) {
            guildMessage.addField("\uD83D\uDCC4 │ no reason", "there was no reason given", false); // Set reason to none
            directMessage.addField("\uD83D\uDCC4 │ no reason", "there was no reason given", false); // Set reason to none
        }
        // Reason is given
        else {
            guildMessage.addField("\uD83D\uDCC4 │ reason:", reason, false);
            directMessage.addField("\uD83D\uDCC4 │ reason:", reason, false);
        }

        // Send messages
        ctx.getChannel().sendMessage(guildMessage.build()).queue(); // Send guild message
        user.openPrivateChannel().queue((channel) -> { // Send direct message
            channel.sendMessage(directMessage.build()).queue();
        });

        // Kick without reason
        if (ctx.getArguments().length == 1) member.kick().queue();
            // Kick with reason
        else member.kick(reason).queue();
    }
}
