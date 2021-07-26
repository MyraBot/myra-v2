package com.github.m5rian.myra.commands.member.general;

import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.Utilities;
import net.dv8tion.jda.api.entities.User;

public class Avatar implements CommandHandler {

    @CommandEvent(
            name = "avatar",
            aliases = {"av", "profile picture", "pp", "profile image"},
            args = "(user)",
            emoji = "\uD83D\uDDBC",
            description = "description.general.avatar"
    )
    public void execute(CommandContext ctx) throws Exception {
        // Get user
        User user = ctx.getAuthor();
        if (ctx.getArguments().length != 0) {
            user = Utilities.getUser(ctx.getEvent(), ctx.getArguments()[0], "avatar", "\uD83D\uDDBC");
            if (user == null) return;
        }

        // Avatar
        final Success avatar = new Success(ctx.getEvent())
                .setCommand("avatar")
                .setEmoji("\uD83D\uDDBC")
                .setHyperLink(user.getEffectiveAvatarUrl())
                .setImage(user.getEffectiveAvatarUrl());
        if (ctx.getGuild().isMember(user)) avatar.setColour(ctx.getGuild().getMember(user).getColor().getRGB());
        avatar.send();
    }
}