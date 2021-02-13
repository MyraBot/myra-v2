package com.myra.dev.marian.commands.leveling.administrator.levelingRoles;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Administrator;
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
        // Command usage
        if (ctx.getArguments().length == 0 || ctx.getArguments().length > 2) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("leveling roles add")
                    .addUsages(new Usage()
                            .setUsage("leveling roles add <level> <role>")
                            .setEmoji("\uD83D\uDD17")
                            .setDescription("Link a role to a level")
                    ).send();
            return;
        }

        // If level is not a digit
        if (!ctx.getArguments()[0].matches("\\d+")) {
            new Error(ctx.getEvent())
                    .setCommand("leveling roles add")
                    .setEmoji("\uD83C\uDFC5")
                    .setMessage("Invalid level")
                    .send();
            return;
        }

        final Utilities utilities = Utilities.getUtils(); // Get utilities
        // Get role
        Role roleToAdd = utilities.getRole(ctx.getEvent(), ctx.getArguments()[1], "leveling roles add", "\uD83C\uDFC5");
        if (roleToAdd == null) return;

        // Update database
        Database db = new Database(ctx.getGuild()); // Get database
        db.getLeveling().addLevelingRole(Integer.parseInt(ctx.getArguments()[0]), roleToAdd.getId());

        // Update every member
        for (Member member : ctx.getGuild().getMembers()) {
            if (member.getUser().isBot()) continue; // Ignore bots
            // If members level is at least the level of the leveling roles
            if (db.getMembers().getMember(member).getLevel() >= Integer.parseInt(ctx.getArguments()[0])) {
                ctx.getGuild().addRoleToMember(member, roleToAdd).queue(); // Add role
            }
        }

        // Success message
        new Success(ctx.getEvent())
                .setCommand("leveling roles add")
                .setEmoji("\uD83C\uDFC5")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setMessage(roleToAdd.getAsMention() + " is now linked up to level `" + ctx.getArguments()[0] + "`")
                .send();
    }
}
