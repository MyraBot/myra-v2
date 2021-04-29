package com.myra.dev.marian.commands.administrator.leveling.levelingRoles;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.bson.Document;

public class LevelingRolesRemove implements CommandHandler {

@CommandEvent(
        name = "leveling roles remove",
        aliases = {"leveling role remove"},
        requires = Administrator.class
)
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("leveling roles remove")
                    .addUsages(new Usage()
                            .setUsage("leveling roles remove <role>")
                            .setEmoji("\uD83D\uDDD1")
                            .setDescription("Delete the linking between a level and a role")
                    ).send();
            return;
        }

        // Get role
        Role role = Utilities.getUtils().getRole(ctx.getEvent(), ctx.getArguments()[0], "leveling roles remove", "\uD83C\uDFC5");
        if (role == null) return;

        MongoGuild db = new MongoGuild(ctx.getGuild()); // Get database
        Document roleDocument = db.getLeveling().getLevelingRole(role.getId()); // Get role document
        db.getLeveling().removeLevelingRole(role); // Remove role from database
        Role levelingRole = ctx.getGuild().getRoleById(roleDocument.getString("role")); // Get role

        // Update every member
        for (Member member : ctx.getGuild().getMembers()) {
            if (member.getUser().isBot()) continue; // Ignore bots
            // If members level is at least the level of the leveling roles
            if (db.getMembers().getMember(member).getLevel() >= roleDocument.getInteger("level")) {
                ctx.getGuild().removeRoleFromMember(member, levelingRole).queue(); // Remove leveling role
            }
        }
        // Success message
        new Success(ctx.getEvent())
                .setCommand("leveling roles remove")
                .setEmoji("\uD83C\uDFC5")
                .setMessage(role.getAsMention() + " is no longer linked up with level " + roleDocument.getInteger("level"))
                .send();
    }
}
