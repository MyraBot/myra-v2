package com.myra.dev.marian.commands.leveling.administrator.levelingRoles;

import com.myra.dev.marian.database.allMethods.Database;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.bson.Document;

@CommandSubscribe(
        name = "leveling roles remove",
        aliases = {"leveling role remove"},
        requires = Administrator.class
)
public class LevelingRolesRemove implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        // Usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("leveling roles remove", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "leveling roles remove <role>`", "\uD83D\uDDD1 │ Delete the linking between a level and a role", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
        /**
         * Remove leveling role
         */
        // Get role
        Role role = utilities.getRole(ctx.getEvent(), ctx.getArguments()[0], "leveling roles remove", "\uD83C\uDFC5");
        if (role == null) return;
        // Get database
        Database db = new Database(ctx.getGuild());
        // Get role document
        Document roleDocument = db.getLeveling().getLevelingRoles(role.getId());
        // Remove role from database
        db.getLeveling().removeLevelingRole(role.getId());
        // Get role
        Role levelingRole = ctx.getGuild().getRoleById(roleDocument.getString("role"));
        // Update every member
        for (Member member : ctx.getGuild().getMembers()) {
            // Leave bots out
            if (member.getUser().isBot()) continue;
            // If members level is at least the level of the leveling roles
            if (db.getMembers().getMember(member).getLevel() >= roleDocument.getInteger("level")) {
                // Remove leveling role
                ctx.getGuild().removeRoleFromMember(member, levelingRole).queue();
            }
        }
        // Success message
        Success success = new Success(ctx.getEvent())
                .setCommand("leveling roles remove")
                .setEmoji("\uD83C\uDFC5")
                .setMessage(role.getAsMention() + " is no longer linked up with level " + roleDocument.getInteger("level"));
        success.send();
    }
}
