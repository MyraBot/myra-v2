package com.myra.dev.marian.commands.leveling;

import com.myra.dev.marian.database.allMethods.Database;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

@CommandSubscribe(
        name = "time"
)
public class Time implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length > 1) return; // Check for no arguments

        // Get member
        Member member = ctx.getMember(); // Get self member
        if (ctx.getArguments().length == 1) { // Another member is given
            member = Utilities.getUtils().getMember(ctx.getEvent(), ctx.getArguments()[0], "streak", "\uD83D\uDCCA");
            if (member == null) return;
        }

        final String voiceCallTime = Utilities.getUtils().formatTime(new Database(ctx.getGuild()).getMembers().getMember(member).getLong("voiceCallTime"));
        EmbedBuilder time = new EmbedBuilder()
                .setAuthor("time", null, member.getUser().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("Your voice call time is **" + voiceCallTime + "**");
        ctx.getChannel().sendMessage(time.build()).queue();
    }
}
