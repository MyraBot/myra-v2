package com.myra.dev.marian.commands.general;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.entities.User;

public class Avatar implements CommandHandler {

    @CommandEvent(
            name = "avatar",
            aliases = {"av", "profile picture", "pp", "profile image"}
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