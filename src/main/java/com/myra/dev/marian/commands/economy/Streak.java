package com.myra.dev.marian.commands.economy;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

public class Streak implements CommandHandler {

@CommandEvent(
        name = "streak"
)
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length > 1) return; // Check for no arguments

        // Get member
        Member member = ctx.getMember(); // Get self member
        if (ctx.getArguments().length == 1) { // Another member is given
            member = Utilities.getUtils().getMember(ctx.getEvent(), ctx.getArguments()[0], "streak", "\uD83D\uDCCA");
            if (member == null) return;
        }

        EmbedBuilder streak = new EmbedBuilder()
                .setAuthor("streak", null, member.getUser().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("Your current streak is **" + new MongoGuild(ctx.getGuild()).getMembers().getMember(member).getDailyStreak() + "**");
        ctx.getChannel().sendMessage(streak.build()).queue();
    }
}
