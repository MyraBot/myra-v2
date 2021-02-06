package com.myra.dev.marian.commands.general.information;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@CommandSubscribe(
        name = "information user",
        aliases = {"info user", "information member", "info member"}
)
public class InformationUser implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        //get utilities
        Utilities utilities = Utilities.getUtils();
        Member user;
        String roleNames = "*this user has no roles*";
// Get user
        //yourself information
        if (ctx.getArguments().length == 0) {
            user = ctx.getEvent().getMember();
        }
        //get given member
        else {
            //if user isn't in the guild
            if (utilities.getMember(ctx.getEvent(), ctx.getArguments()[0], "information user", "\uD83D\uDC64") == null) {
                new Error(ctx.getEvent())
                        .setCommand("information user")
                        .setEmoji("\uD83D\uDC64")
                        .setMessage("No user found")
                        .send();
                return;
            }
            user = utilities.getMember(ctx.getEvent(), ctx.getArguments()[0], "information user", "\uD83D\uDC64");
        }

        List<Role> roles = user.getRoles();
        if (!roles.isEmpty()) {
            roleNames = "";
            //role names
            for (Role role : roles) {
                roleNames += role.getAsMention() + " ";
            }
        }
        //users status
        OnlineStatus status = user.getOnlineStatus();
        String StringStatus = status.toString()
                .replace("OFFLINE", utilities.getEmote("offline").getAsMention() + " │ offline")
                .replace("IDLE", utilities.getEmote("idle").getAsMention() + " │ idle")
                .replace("DO_NOT_DISTURB", utilities.getEmote("doNotDisturb").getAsMention() + " │ do not distrub")
                .replace("ONLINE", utilities.getEmote("online").getAsMention() + " │ online");
        //badges
        String badges = getBadges(user, utilities);
        /**
         * build embed
         */
        EmbedBuilder userInformation = new EmbedBuilder()
                .setAuthor(" │ " + user.getUser().getAsTag(), user.getUser().getEffectiveAvatarUrl(), user.getUser().getEffectiveAvatarUrl())
                .setThumbnail(user.getUser().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().getMemberRoleColour(user))
                .addField("\uD83C\uDF9F │ user id", user.getId(), true);
        //nickname
        if (user.getNickname() != null)
            userInformation.addField("\uD83D\uDD75 │ nickname", "\uD83C\uDFF7 │ " + user.getNickname(), true);
        //status
        userInformation.addField("\uD83D\uDCE1 │ status", StringStatus, false);
        //activity
        if (!user.getActivities().isEmpty())
            userInformation.addField("\uD83C\uDFB2 │ activity", user.getActivities().get(0).getName(), true);
        //badges
        if (!badges.equals("")) userInformation.addField("\uD83C\uDFC5 │ badges", badges, false);
        //join time
        userInformation.addField("\uD83D\uDCC5 │ joined server", user.getTimeJoined().atZoneSameInstant(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy , hh:mm")), true);
        //booster
        if (ctx.getGuild().getBoosters().contains(user))
            userInformation.addField(utilities.getEmote("nitroBoost") + " │ is boosting", "since: " + user.getTimeBoosted().atZoneSameInstant(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy , hh:mm")), true);
        //permissions
        if (user.hasPermission(Permission.ADMINISTRATOR)) {
            userInformation.addField("\uD83D\uDD11 │ Permissions", "Administrator", false);
        } else if (user.hasPermission(Permission.VIEW_AUDIT_LOGS)) {
            userInformation.addField("\uD83D\uDD11 │ Permissions", "Moderator", false);
        }
        userInformation.addField("\uD83D\uDCC3 │ roles", roleNames, false);

        ctx.getChannel().sendMessage(userInformation.build()).queue();
    }

    //return badges
    private String getBadges(Member user, Utilities utilities) {
        final Utilities utils = Utilities.getUtils();
        final String flags = user.getUser().getFlags().toString();

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