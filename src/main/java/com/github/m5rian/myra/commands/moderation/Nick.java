package com.github.m5rian.myra.commands.moderation;


import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.permissions.Moderator;
import net.dv8tion.jda.api.entities.Member;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Nick implements CommandHandler {

    @CommandEvent(
            name = "nick",
            aliases = {"nickname", "change nickname"},
            requires = Moderator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length < 2) {

            new CommandUsage(ctx.getEvent())
                    .setCommand("nick")
                    .addUsages(new Usage()
                            .setUsage("nick <member> <nickname>")
                            .setEmoji("\uD83D\uDD75")
                            .setDescription(lang(ctx).get("description.mod.nick")))
                    .send();
            return;
        }

        // Get provided member
        final Member member = Utilities.getModifiedMember(ctx.getEvent(), ctx.getArguments()[0], "nick", "\uD83D\uDD75"); // Get member
        if (member == null) return;

        final String nickname = ctx.getArgumentsRaw().split("\\s+", 2)[2]; // Get nickname
        // Send success message
        new Success(ctx.getEvent())
                .setCommand("ban")
                // Member who executed the ban
                .setFooter(lang(ctx).get("command.mod.info.requestBy")
                                .replace("{$member}", ctx.getAuthor().getAsTag()),
                        ctx.getAuthor().getEffectiveAvatarUrl())
                .setMessage(lang(ctx).get("command.mod.nick.info.done")
                        .replace("{$member}", member.getAsMention()) // Member whose nicknames changes
                        .replace("{$nickname}", nickname)) // New nickname
                .addTimestamp()
                .send();

        member.modifyNickname(nickname).queue(); // Change nickname
    }
}
