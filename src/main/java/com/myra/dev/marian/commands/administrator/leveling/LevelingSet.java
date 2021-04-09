package com.myra.dev.marian.commands.administrator.leveling;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.listeners.leveling.Leveling;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

@CommandSubscribe(
        name = "leveling set",
        requires = Administrator.class
)
public class LevelingSet implements Command {
    private final Leveling leveling = new Leveling();

    @Override
    public void execute(CommandContext ctx) throws Exception {
        Utilities utilities = Utilities.getUtils(); // Get utilities
        // Usage
        if (ctx.getArguments().length != 2) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("leveling set", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "leveling set <user> <level>`", "\uD83C\uDFC6 │ Change the level of a user", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
        // Get database
        MongoGuild db = new MongoGuild(ctx.getGuild());
        //get provided member
        Member member = utilities.getMember(ctx.getEvent(), ctx.getArguments()[0], "leveling set", "\uD83C\uDFC6");
        if (member == null) return;
        // When user is a bot
        if (member.getUser().isBot()) {
            new Error(ctx.getEvent())
                    .setCommand("leveling set")
                    .setEmoji("\uD83C\uDFC6")
                    .setMessage("Bots aren't allowed to participate in the ranking competition")
                    .send();
            return;
        }

        // Update database
        db.getMembers().getMember(member).setLevel(Integer.parseInt(ctx.getArguments()[1])); // Update level
        db.getMembers().getMember(member).setXp(leveling.xpFromLevel(Integer.parseInt(ctx.getArguments()[1]))); // Update xp

        //send success message
        new Success(ctx.getEvent())
                .setCommand("leveling set")
                .setEmoji("\uD83C\uDFC6")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setMessage(member.getAsMention() + " is now level `" + ctx.getArguments()[1] + "`")
                .send();
        // Check for leveling roles
        leveling.updateLevelingRoles(ctx.getGuild(), member, new MongoGuild(ctx.getGuild()).getMembers().getMember(member));
    }
}
