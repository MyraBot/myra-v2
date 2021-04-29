package com.myra.dev.marian.commands.moderation.mute;


import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Moderator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.time.Instant;

public class Mute implements CommandHandler {

@CommandEvent(
        name = "mute",
        requires = Moderator.class,
        channel = Channel.GUILD
)
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("mute", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "mute <user> <reason>`", "\uD83D\uDD07 │ mute a specific user", false)
                    .setFooter("you don't have to give a reason.");
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }

        if (ctx.getArguments()[0].equalsIgnoreCase("role")) return; // Mute role command was used

        final Member member = Utilities.getUtils().getModifiedMember(ctx.getEvent(), ctx.getArguments()[0], "mute", "\uD83D\uDD07"); // Get member
        if (member == null) return;

        final String muteRoleId = new MongoGuild(ctx.getGuild()).getString("muteRole"); // Get mute role id
        // No mute role set
        if (muteRoleId.equals("not set")) {
            new Error(ctx.getEvent())
                    .setCommand("mute")
                    .setEmoji("\uD83D\uDD07")
                    .setMessage("You didn't specify a mute role")
                    .send();
            return;
        }
        // User is already muted
        if (member.getRoles().contains(ctx.getGuild().getRoleById(muteRoleId))) {
            new Error(ctx.getEvent())
                    .setCommand("mute")
                    .setEmoji("\uD83D\uDD07")
                    .setMessage("This user is already muted")
                    .send();
            return;
        }

        final User user = member.getUser(); // Get member as user
        // Guild message
        EmbedBuilder guildMessage = new EmbedBuilder()
                .setAuthor(user.getAsTag() + " got muted", null, user.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().red)
                .setDescription("\uD83D\uDD07 │ " + user.getAsMention() + " got muted on " + ctx.getGuild().getName())
                .setFooter("requested by " + ctx.getAuthor().getAsTag(), ctx.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        // Direct message
        EmbedBuilder directMessage = new EmbedBuilder()
                .setAuthor("You got muted", null, user.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().red)
                .setDescription("\uD83D\uDD07 │ You got muted on " + ctx.getGuild().getName())
                .setFooter("requested by " + ctx.getAuthor().getAsTag(), ctx.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        // No reason given
        if (ctx.getArguments().length == 1) {
            guildMessage.addField("\uD83D\uDCC4 │ no reason", "there was no reason given", false); // Set reason to none
            directMessage.addField("\uD83D\uDCC4 │ no reason", "there was no reason given", false); // Set reason to none
        }
        //mute with reason
        else {
            final String reason = ctx.getArgumentsRaw().split("\\s+", 2)[1]; // Get arguments
            guildMessage.addField("\uD83D\uDCC4 │ reason:", reason, false); // Add reason
            directMessage.addField("\uD83D\uDCC4 │ reason:", reason, false); // Add reason
        }

        // Send messages
        ctx.getChannel().sendMessage(guildMessage.build()).queue(); // Send message in guild
        user.openPrivateChannel().queue((channel) -> { // Send direct message
            channel.sendMessage(directMessage.build()).queue();
        });
        // Mute member
        ctx.getGuild().addRoleToMember(ctx.getGuild().getMember(user), ctx.getGuild().getRoleById(muteRoleId)).queue();
    }
}
