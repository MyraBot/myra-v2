package com.myra.dev.marian.commands.leveling;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Format;
import com.myra.dev.marian.utilities.Utilities;
import static com.myra.dev.marian.utilities.language.Lang.*;
import net.dv8tion.jda.api.entities.Member;

public class Time implements CommandHandler {

    @CommandEvent(
            name = "time"
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length > 1) return; // Check for no arguments

        // Get member
        Member member = ctx.getMember(); // Get self member
        if (ctx.getArguments().length == 1) { // Another member is given
            member = Utilities.getMember(ctx.getEvent(), ctx.getArguments()[0], "streak", "\uD83D\uDCCA");
            if (member == null) return;
        }

        final String voiceCallTime = Format.toTimeExact(new MongoGuild(ctx.getGuild()).getMembers().getMember(member).getVoiceTime());
        new Success(ctx.getEvent())
                .setCommand("time")
                .setMessage(lang(ctx).get("command.leveling.time.success")
                        .replace("{$time}", voiceCallTime))
                .send();
    }
}
