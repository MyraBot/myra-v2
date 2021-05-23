package com.myra.dev.marian.commands.economy;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Utilities;
import static com.myra.dev.marian.utilities.language.Lang.*;
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
            member = Utilities.getMember(ctx.getEvent(), ctx.getArguments()[0], "streak", "\uD83D\uDCCA");
            if (member == null) return;
        }

        final Integer streak = new MongoGuild(ctx.getGuild()).getMembers().getMember(member).getDailyStreak(); // Get streak
        new Success(ctx.getEvent())
                .setCommand("streak")
                .setEmoji("\uD83D\uDCCA")
                .setMessage(lang(ctx).get("command.economy.streak.message.success")
                        .replace("{$streak}", String.valueOf(streak))) // Daily streak
                .send();
    }
}
