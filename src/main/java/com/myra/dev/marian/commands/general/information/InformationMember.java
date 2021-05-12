package com.myra.dev.marian.commands.general.information;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.CustomEmoji;
import com.myra.dev.marian.utilities.Format;
import com.myra.dev.marian.utilities.Utilities;
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
        final Utilities utilities = Utilities.getUtils(); // Get utilities
        Member member = ctx.getMember(); // Get author as member

        // Another member is given
        if (ctx.getArguments().length > 0) {
            final Member providedMember = Utilities.getUtils().getMember(ctx.getEvent(), ctx.getArgumentsRaw(), "information member", "\uD83D\uDC6A");
            if (providedMember == null) return;
            member = providedMember;
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
        CustomEmoji emoji = CustomEmoji.search(member.getOnlineStatus().toString());
        String onlineStatus;
        switch (member.getOnlineStatus().toString()) {
            case "OFFLINE" -> onlineStatus = "Offline";
            case "IDLE" -> onlineStatus = "Idle";
            case "DO_NOT_DISTURB" -> onlineStatus = "Do not disturb";
            case "ONLINE" -> onlineStatus = "Online";
            default -> onlineStatus = "Unknown";
        }
        memberInfo.addField(emoji + " │ status", onlineStatus, true);

        // Activity
        if (!member.getActivities().isEmpty())
            memberInfo.addField("\uD83C\uDFB2 │ activity", member.getActivities().get(0).getName(), true);

        // Permissions
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            memberInfo.addField("\uD83D\uDD11 │ Permissions", "Administrator", true);
        } else if (member.hasPermission(Permission.VIEW_AUDIT_LOGS)) {
            memberInfo.addField("\uD83D\uDD11 │ Permissions", "Moderator", true);
        } else {
            memberInfo.addField("\uD83D\uDD11 │ Permissions", "Member", true);
        }

        // Time joined server
        final String joinedAt = Format.asDate(member.getTimeJoined().toEpochSecond());
        memberInfo.addField("\uD83D\uDCC5 │ joined server", joinedAt, true);

        if (ctx.getGuild().getBoosters().contains(member)) {
            final String boostingSince = member.getTimeBoosted().atZoneSameInstant(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm"));
            memberInfo.addField(CustomEmoji.NITRO.getAsEmoji() + " │ Is boosting", "since: " + boostingSince, true);
        }

        // Roles
        StringBuilder roles = new StringBuilder(); // Create variable for roles
        if (!member.getRoles().isEmpty()) {
            // Add all roles
            member.getRoles().forEach(role -> roles.append(role.getAsMention()).append(" "));
        } else roles.append("*no roles*");
        memberInfo.addField("\uD83D\uDCC3 │ Roles", roles.toString(), false);

        ctx.getChannel().sendMessage(memberInfo.build()).queue();
    }
}
