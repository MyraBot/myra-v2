package com.myra.dev.marian.commands.general.information;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.utilities.Format;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

@CommandSubscribe(
        name = "information user",
        aliases = {"info user"}
)
public class InformationUser implements Command {

    @Override
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
        final String badges = getBadges(user, utilities); // Get badges
        if (!badges.equals("")) userInfo.addField("\uD83C\uDFC5 │ badges", badges, false);

        userInfo.addField("\uD83D\uDCC5 │ Account created", Format.asDate(user.getTimeCreated().toEpochSecond()), false);

        ctx.getChannel().sendMessage(userInfo.build()).queue();
    }


    private String getBadges(User user, Utilities utilities) {
        final Utilities utils = Utilities.getUtils();
        final String flags = user.getFlags().toString();

        String badges = "";
        //bug hunter
        if (flags.contains("BUG_HUNTER_LEVEL_1")) badges += utils.getEmote("bugHunter").getAsMention() + " ";
        //bug hunter level 2
        if (flags.contains("BUG_HUNTER_LEVEL_2")) badges += utils.getEmote("bugHunterLvl2").getAsMention() + " ";
        if (flags.contains("EARLY_SUPPORTER")) {
        }
        if (flags.contains("HYPESQUAD")) {
        }
        //hypeSquad balance
        if (flags.contains("HYPESQUAD_BALANCE")) badges += utils.getEmote("balance").getAsMention() + " ";
        //hypeSquad bravery
        if (flags.contains("HYPESQUAD_BRAVERY")) badges += utils.getEmote("bravery").getAsMention() + " ";
        //hypeSquad brilliance
        if (flags.contains("HYPESQUAD_BRILLIANCE")) badges += utils.getEmote("brilliance").getAsMention() + " ";
        if (flags.contains("PARTNER")) badges += utils.getEmote("partner").getAsMention() + " ";
        if (flags.contains("STAFF")) badges += utils.getEmote("staff").getAsMention() + " ";
        if (flags.contains("SYSTEM")) {
        }
        if (flags.contains("UNKNOWN")) {
        }
        if (flags.contains("VERIFIED_BOT")) {
        }
        if (flags.contains("VERIFIED_DEVELOPER")) badges += utils.getEmote("verifiedDeveloper").getAsMention() + " ";

        return badges;
    }
}