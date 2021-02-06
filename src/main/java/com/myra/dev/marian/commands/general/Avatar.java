package com.myra.dev.marian.commands.general;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

@CommandSubscribe(
        name = "avatar",
        aliases = {"av", "profile picture", "pp", "profile image"}
)
public class Avatar implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        //get user
        User user = ctx.getAuthor();
        if (ctx.getArguments().length != 0) {
            user = utilities.getUser(ctx.getEvent(), ctx.getArguments()[0], "avatar", "\uD83D\uDDBC");
            if (user == null) return;
        }
        //avatar
        EmbedBuilder avatar = new EmbedBuilder()
                .setAuthor(user.getName() + "'s avatar:", user.getEffectiveAvatarUrl(), user.getEffectiveAvatarUrl());
        if (ctx.getGuild().getMember(user) != null) {
            avatar.setColor(utilities.getMemberRoleColour(ctx.getGuild().getMember(user)));
        }
        avatar.setImage(user.getEffectiveAvatarUrl());

        ctx.getChannel().sendMessage(avatar.build()).queue();
    }
}