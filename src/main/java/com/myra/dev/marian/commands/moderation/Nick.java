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
        name = "nick",
        aliases = {"nickname", "change nickname"},
        requires = Moderator.class
)
public class Nick implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        Utilities utilities = Utilities.getUtils(); // Get utilities
        //command usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("nick", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "nick @user <nickname>`", "\uD83D\uDD75 │ Change a users nickname", true);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
// Change nickname
        final Member member = utilities.getModifiedMember(ctx.getEvent(), ctx.getArguments()[0], "nick", "\uD83D\uDD75"); // Get member
        if (member == null) return;

        final String nickname = ctx.getArgumentsRaw().split("\\s+", 2)[1]; // Get new nickname

        final User user = member.getUser(); // Get member as user
        // Success
        EmbedBuilder success = new EmbedBuilder()
                .setAuthor("nickname changed", null, user.getEffectiveAvatarUrl())
                .setColor(utilities.green)
                .addField("\uD83D\uDCC4 │ nickname changed of " + user.getName(), "`" + ctx.getGuild().getMember(user).getEffectiveName() + "` **→** `" + nickname + "`", true)
                .setFooter("requested by " + ctx.getAuthor().getAsTag(), ctx.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        ctx.getChannel().sendMessage(success.build()).queue(); // Send success message
        member.modifyNickname(nickname).queue(); // Change nickname
    }
}
