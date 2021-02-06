package com.myra.dev.marian.commands.leveling.administrator.levelingRoles;

import com.myra.dev.marian.database.allMethods.Database;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

@CommandSubscribe(
        name = "leveling roles add",
        aliases = {"leveling role add"},
        requires = Administrator.class
)
public class LevelingRolesAdd implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        // Usage
        if (ctx.getArguments().length == 0 || ctx.getArguments().length > 3) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("leveling roles add", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "leveling roles add <level> <role> [remove]`", "\uD83D\uDD17 │ Link a role to a level", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
        /**
         * Add new role
         */
        // If level is not a digit
        if (!ctx.getArguments()[0].matches("\\d+")) {
            new Error(ctx.getEvent())
                    .setCommand("leveling roles add")
                    .setEmoji("\uD83C\uDFC5")
                    .setMessage("Invalid level")
                    .send();
            return;
        }
        // Get role to add
        Role roleToAdd = utilities.getRole(ctx.getEvent(), ctx.getArguments()[1], "leveling roles add", "\uD83C\uDFC5");
        if (roleToAdd == null) return;
        // If role to remove is given
        Role roleToRemove = null;
        if (ctx.getArguments().length == 3) {
            // Get role
            roleToRemove = utilities.getRole(ctx.getEvent(), ctx.getArguments()[2], "leveling roles add", "\uD83C\uDFC5");
            if (roleToRemove == null) return;
        }
        // Get database
        Database db = new Database(ctx.getGuild());
        // If no role to remove is set
        if (roleToRemove == null) {
            // Update database
            db.getLeveling().addLevelingRole(Integer.parseInt(ctx.getArguments()[0]), roleToAdd.getId(), "not set");
        } else {
            // Update database
            db.getLeveling().addLevelingRole(Integer.parseInt(ctx.getArguments()[0]), roleToAdd.getId(), roleToRemove.getId());
        }
        // Update every member
        for (Member member : ctx.getGuild().getMembers()) {
            // Leave bots out
            if (member.getUser().isBot()) continue;
            // If members level is at least the level of the leveling roles
            if (db.getMembers().getMember(member).getLevel() >= Integer.parseInt(ctx.getArguments()[0])) {
                // Add role
                ctx.getGuild().addRoleToMember(member, roleToAdd).queue();
                // Check if role to remove is not null
                if (roleToRemove != null) {
                    // Remove role from member
                    ctx.getGuild().removeRoleFromMember(member, roleToRemove).queue();
                }
            }
        }
        // Success message
        Success success = new Success(ctx.getEvent())
                .setCommand("leveling roles add")
                .setEmoji("\uD83C\uDFC5")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl());
        // If role to remove is given
        if (ctx.getArguments().length == 3)
            success.setMessage(roleToAdd.getAsMention() + " is now linked up tp level `" + ctx.getArguments()[0] + "` and I will remove " + roleToRemove.getAsMention()).send();
            // If role to remove isn't give
        else
            success.setMessage(roleToAdd.getAsMention() + " is now linked up to level `" + ctx.getArguments()[0] + "`").send();
    }
}
