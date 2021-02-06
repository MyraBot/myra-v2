package com.myra.dev.marian.commands.leveling.administrator.levelingRoles;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.database.documents.LevelingRolesDocument;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@CommandSubscribe(
        name = "leveling roles list",
        aliases = {"leveling role list"},
        requires = Administrator.class
)
public class LevelingRolesList implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        // Get leveling roles
        List<LevelingRolesDocument> rolesList = new Database(ctx.getGuild()).getLeveling().getLevelingRoles();
        // Sort roles
        Collections.sort(rolesList, Comparator.comparing(LevelingRolesDocument::getLevel).reversed());
        // Add all roles to String
        StringBuilder roles = new StringBuilder();

        // If list is empty
        if (rolesList.isEmpty()) {
            roles = new StringBuilder("none");
        }
        // Else add all leveling roles to the String
        else {
            for (LevelingRolesDocument role : rolesList) {
                // When there is a role to remove
                if (!role.getRemove().equals("not set")) {
                    roles.append("• level: `").append(role.getLevel()).append("` add: ").append(ctx.getGuild().getRoleById(role.getRole()).getAsMention()).append(" remove:").append(ctx.getGuild().getRoleById(role.getRemove()).getAsMention() + "\n");
                    continue;
                }
                // When there is only a role to add
                roles.append("• level: `").append(role.getLevel()).append("` add: ").append(ctx.getGuild().getRoleById(role.getRole()).getAsMention() + "\n");
            }
        }
        // Create embed
        EmbedBuilder levelingRoles = new EmbedBuilder()
                .setAuthor("leveling roles list", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(utilities.blue)
                .setDescription(roles.toString());
        ctx.getChannel().sendMessage(levelingRoles.build()).queue();
    }
}
