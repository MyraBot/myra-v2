package com.github.m5rian.myra.commands.member.leveling;

import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.guild.member.GuildMember;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.Format;
import com.github.m5rian.myra.utilities.Utilities;
import net.dv8tion.jda.api.entities.Member;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Time implements CommandHandler {

    @CommandEvent(
            name = "time",
            args = "(member)",
            emoji = "\u231A",
            description = "description.leveling.time"
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length > 1) return; // Check for no arguments

        // Get member
        Member member = ctx.getMember(); // Get self member
        if (ctx.getArguments().length == 1) { // Another member is given
            member = Utilities.getMember(ctx.getEvent(), ctx.getArguments()[0], "streak", "\uD83D\uDCCA");
            if (member == null) return;
        }

        final String voiceCallTime = Format.toTimeExact(GuildMember.get(member).getVoiceTime());
        new Success(ctx.getEvent())
                .setCommand("time")
                .setMessage(lang(ctx).get("command.leveling.time.success")
                        .replace("{$time}", voiceCallTime))
                .send();
    }
}
