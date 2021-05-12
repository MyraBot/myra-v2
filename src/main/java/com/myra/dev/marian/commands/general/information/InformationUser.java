package com.myra.dev.marian.commands.general.information;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.MongoUser;
import com.myra.dev.marian.utilities.CustomEmoji;
import com.myra.dev.marian.utilities.Format;
import com.myra.dev.marian.utilities.UserBadge;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public class InformationUser implements CommandHandler {

    @CommandEvent(
            name = "information user",
            aliases = {"info user"}
    )
    public void execute(CommandContext ctx) throws Exception {
        final Utilities utilities = Utilities.getUtils(); // Get utilities
        User user = ctx.getAuthor(); // Get author as member

        // Another member is given
        if (ctx.getArguments().length > 0) {
            final User providedUser = Utilities.getUtils().getUser(ctx.getEvent(), ctx.getArgumentsRaw(), "information member", "\uD83D\uDC6A");
            if (providedUser == null) return;
            user = providedUser;
        }


        final String baseUrl = "https://discord.com/users/"; // Get base url for user
        EmbedBuilder userInfo = new EmbedBuilder()
                .setAuthor(user.getAsTag(), baseUrl + user.getId(), user.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setThumbnail(user.getEffectiveAvatarUrl())
                .addField("\uD83C\uDF9F │ User id", user.getId(), true);

        // Badges
        final StringBuilder badges = new StringBuilder(); // Create string for all badges
        final List<UserBadge> badgesList = new MongoUser(user).getBadges(); // Get badges of user
        badgesList.forEach(badge -> badges.append(CustomEmoji.search(badge.getName()).getAsEmoji())); // Add all badges to string
        if (!badges.equals("")) userInfo.addField("\uD83C\uDFC5 │ badges", badges.toString(), false);

        userInfo.addField("\uD83D\uDCC5 │ Account created", Format.asDate(user.getTimeCreated().toEpochSecond()), false);

        ctx.getChannel().sendMessage(userInfo.build()).queue();
    }

}