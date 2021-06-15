package com.github.m5rian.myra.commands.member.economy;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.Utilities;
import net.dv8tion.jda.api.entities.Member;

public class Streak implements CommandHandler {

    @CommandEvent(
            name = "streak",
            args = {"(member)"},
            emoji = "\uD83D\uDCCA",
            description = "description.economy.streak",
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length > 1) return; // Check for no arguments

        // Get member
        Member member = ctx.getMember(); // Get self member
        if (ctx.getArguments().length == 1) { // Another member is given
            member = Utilities.getMember(ctx.getEvent(), ctx.getArguments()[0], "streak", "\uD83D\uDCCA");
            if (member == null) return;
        }

        final Integer streak = MongoGuild.get(ctx.getGuild()).getMembers().getMember(member).getDailyStreak(); // Get streak
        new Success(ctx.getEvent())
                .setCommand("streak")
                .setEmoji("\uD83D\uDCCA")
                .setMessage(Lang.lang(ctx).get("command.economy.streak.message.success")
                        .replace("{$streak}", String.valueOf(streak))) // Daily streak
                .send();
    }
}
