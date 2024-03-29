package com.github.m5rian.myra.commands.administrator.leveling;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.guild.member.GuildMember;
import com.github.m5rian.myra.listeners.leveling.Leveling;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import net.dv8tion.jda.api.entities.Member;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class LevelingSet implements CommandHandler {

    @CommandEvent(
            name = "leveling set",
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 2) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("leveling set")
                    .addUsages(new Usage()
                            .setUsage("leveling set <user> <level>")
                            .setEmoji("\uD83C\uDFC6")
                            .setDescription(lang(ctx).get("description.leveling.set")))
                    .send();
            return;
        }


        // Get provided member
        final Member member = Utilities.getMember(ctx.getEvent(), ctx.getArguments()[0], "leveling set", "\uD83C\uDFC6");
        if (member == null) return;

        // Provided member is a bot
        if (member.getUser().isBot()) {
            new Error(ctx.getEvent())
                    .setCommand("leveling set")
                    .setEmoji("\uD83C\uDFC6")
                    .setMessage(lang(ctx).get("command.leveling.set.info.noBots"))
                    .send();
            return;
        }

        // Input is not a number
        if (!ctx.getArguments()[1].matches("\\d+")) {
            new Error(ctx.getEvent())
                    .setCommand("leveling set")
                    .setEmoji("\uD83C\uDFC6")
                    .setMessage(lang(ctx).get("error.invalid"))
                    .send();
            return;
        }


        try {
            Integer.parseInt(ctx.getArguments()[1]); // Try parsing the string to integer
        }
        // Input isn't an Integer
        catch (NumberFormatException e) {
            new Error(ctx.getEvent())
                    .setCommand("leveling set")
                    .setEmoji("\uD83C\uDFC6")
                    .setMessage(lang(ctx).get("command.leveling.set.info.numberTooBig"))
                    .send();
            return;
        }

        final Integer level = Integer.parseInt(ctx.getArguments()[1]); // Get provided level
        // Update member
        final GuildMember dbMember = GuildMember.get(ctx.getMember());
        dbMember.setLevel(level); // Update level
        dbMember.setXp(Leveling.getXpFromLevel(level)); // Update xp

        //send success message
        new Success(ctx.getEvent())
                .setCommand("leveling set")
                .setEmoji("\uD83C\uDFC6")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setMessage(lang(ctx).get("command.leveling.set.info.success")
                        .replace("{$member}", member.getAsMention()) // Member
                        .replace("{$level}", String.valueOf(level))) // New level
                .send();
        // Check for leveling roles
        Leveling.updateLevelingRoles(ctx.getGuild(), member, dbMember);
    }
}
