package com.github.m5rian.myra.commands.member.general.information;

import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.MongoUser;
import com.github.m5rian.myra.utilities.CustomEmoji;
import com.github.m5rian.myra.utilities.Format;
import com.github.m5rian.myra.utilities.UserBadge;
import com.github.m5rian.myra.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class InformationUser implements CommandHandler {

    @CommandEvent(
            name = "information user",
            aliases = {"info user"}
    )
    public void execute(CommandContext ctx) throws Exception {
        User user = ctx.getAuthor(); // Get author as member
        // Another member is given
        if (ctx.getArguments().length > 0) {
            user = Utilities.getUser(ctx.getEvent(), ctx.getArgumentsRaw(), "information member", "\uD83D\uDC6A");
            if (user == null) return;
        }

        final String baseUrl = "https://discord.com/users/"; // Get base url for user
        EmbedBuilder userInfo = new EmbedBuilder()
                .setAuthor(user.getAsTag(), baseUrl + user.getId(), user.getEffectiveAvatarUrl())
                .setColor(Utilities.blue)
                .setThumbnail(user.getEffectiveAvatarUrl())
                .addField("\uD83C\uDF9F │ " + lang(ctx).get("command.general.info.user.userId"), user.getId(), true);

        // Badges
        final StringBuilder badges = new StringBuilder(); // Create string for all badges
        final List<UserBadge> badgesList = MongoUser.get(user).getBadges(); // Get badges of user
        badgesList.forEach(badge -> badges.append(CustomEmoji.search(badge.getName()).getAsMention())); // Add all badges to string
        if (!badges.equals(""))
            userInfo.addField("\uD83C\uDFC5 │ " + lang(ctx).get("command.general.info.user.badges"), badges.toString(), false);
        // account creation
        userInfo.addField("\uD83D\uDCC5 │ " + lang(ctx).get("command.general.info.user.createdAt"), Format.asDate(user.getTimeCreated().toEpochSecond()), false);

        ctx.getChannel().sendMessage(userInfo.build()).queue();
    }

}