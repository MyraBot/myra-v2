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
        List<LevelingRolesDocument> rolesList = new Database(ctx.getGuild()).getLeveling().getLevelingRoles(); // Get leveling roles
        rolesList.sort(Comparator.comparing(LevelingRolesDocument::getLevel).reversed()); // Sort roles
        StringBuilder roles = new StringBuilder(); // Add all roles to String

        // If list is empty
        if (rolesList.isEmpty()) roles = new StringBuilder("none");
        // Else add all leveling roles to the String
        else {
            for (LevelingRolesDocument role : rolesList) {
                roles.append("• level: `").append(role.getLevel()).append("` add: ").append(ctx.getGuild().getRoleById(role.getRole()).getAsMention() + "\n");
            }
        }

        // Create embed
        EmbedBuilder levelingRoles = new EmbedBuilder()
                .setAuthor("leveling roles list", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription(roles.toString());
        ctx.getChannel().sendMessage(levelingRoles.build()).queue();
    }
}
