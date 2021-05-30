package com.github.m5rian.myra.commands.member.general.information;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.CustomEmoji;
import com.github.m5rian.myra.utilities.Format;
import com.github.m5rian.myra.utilities.Utilities;
import static com.github.m5rian.myra.utilities.language.Lang.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class InformationMember implements CommandHandler {

    @CommandEvent(
            name = "information member",
            aliases = {"info member"},
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        Member member = ctx.getMember(); // Get author as member
        // Another member is given
        if (ctx.getArguments().length > 0) {
            member = Utilities.getMember(ctx.getEvent(), ctx.getArgumentsRaw(), "information member", "\uD83D\uDC6A");
            if (member == null) return;
        }

        final String baseUrl = "https://discord.com/users/"; // Get base url for user
        EmbedBuilder memberInfo = new EmbedBuilder()
                .setAuthor(member.getEffectiveName(), baseUrl + member.getId(), member.getUser().getEffectiveAvatarUrl())
                .setColor(member.getColorRaw())
                .setThumbnail(member.getUser().getEffectiveAvatarUrl());

        // Nickname
        if (member.getNickname() != null) {
            memberInfo.addField("\uD83D\uDD75 │ Nickname", member.getNickname(), true);
        }

        // Online status
        final CustomEmoji emoji = CustomEmoji.search(member.getOnlineStatus().toString());
        String onlineStatus;
        switch (member.getOnlineStatus().toString()) {
            case "OFFLINE" -> onlineStatus = lang(ctx).get("command.general.info.member.offline");
            case "IDLE" -> onlineStatus = lang(ctx).get("command.general.info.member.idle");
            case "DO_NOT_DISTURB" -> onlineStatus = lang(ctx).get("command.general.info.member.doNotDisturb");
            case "ONLINE" -> onlineStatus = lang(ctx).get("command.general.info.member.online");
            default -> onlineStatus = "Unknown";
        }
        memberInfo.addField(emoji + " │ " + lang(ctx).get("command.general.info.member.onlineStatus"), onlineStatus, true);

        // Activity
        if (!member.getActivities().isEmpty()) {
            memberInfo.addField("\uD83C\uDFB2 │ " + lang(ctx).get("command.general.info.member.activity"), member.getActivities().get(0).getName(), true);
        }

        // Permissions
        final String permissions = lang(ctx).get("command.general.info.member.permissions");
        // Administrator permissions
        if (member.hasPermission(Permission.MANAGE_SERVER)) {
            memberInfo.addField("\uD83D\uDD11 │ " + permissions,
                    lang(ctx).get("command.general.info.member.permission.admin"), true);
        }
        // Moderator permissions
        else if (member.hasPermission(Permission.VIEW_AUDIT_LOGS)) {
            memberInfo.addField("\uD83D\uDD11 │ " + permissions,
                    lang(ctx).get("command.general.info.member.permission.mod"), true);
        }
        // Member permissions
        else {
            memberInfo.addField("\uD83D\uDD11 │ " + permissions,
                    lang(ctx).get("command.general.info.member.permission.member"), true);
        }

        // Time joined server
        final String joinedAt = Format.asDate(member.getTimeJoined().toEpochSecond());
        memberInfo.addField("\uD83D\uDCC5 │ " + lang(ctx).get("command.general.info.member.joinedAt"), joinedAt, true);

        if (ctx.getGuild().getBoosters().contains(member)) {
            final String boostingSince = member.getTimeBoosted().atZoneSameInstant(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm"));
            memberInfo.addField(CustomEmoji.NITRO.getAsEmoji() + " │ " + lang(ctx).get("command.general.info.member.isBoosting"),
                    lang(ctx).get("command.general.info.member.since") + ": " + boostingSince, true);
        }

        // Roles
        StringBuilder roles = new StringBuilder(); // Create variable for roles
        if (!member.getRoles().isEmpty()) {
            // Add all roles
            member.getRoles().forEach(role -> roles.append(role.getAsMention()).append(" "));
        } else roles.append(lang(ctx).get("command.general.info.member.noRoles"));
        memberInfo.addField("\uD83D\uDCC3 │ " + lang(ctx).get("command.general.info.member.Roles"), roles.toString(), false);

        ctx.getChannel().sendMessage(memberInfo.build()).queue();
    }
}
