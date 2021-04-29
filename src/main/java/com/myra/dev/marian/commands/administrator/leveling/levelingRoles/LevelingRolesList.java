package com.myra.dev.marian.commands.administrator.leveling.levelingRoles;

import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.database.guild.LevelingRole;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Comparator;
import java.util.List;

public class LevelingRolesList implements CommandHandler {

@CommandEvent(
        name = "leveling roles list",
        aliases = {"leveling role list"},
        requires = Administrator.class
)
    public void execute(CommandContext ctx) throws Exception {
        List<LevelingRole> rolesList = new MongoGuild(ctx.getGuild()).getLeveling().getLevelingRoles(); // Get leveling roles
        rolesList.sort(Comparator.comparing(LevelingRole::getLevel).reversed()); // Sort roles
        StringBuilder roles = new StringBuilder(); // Add all roles to String

        // If list is empty
        if (rolesList.isEmpty()) roles = new StringBuilder("none");
        // Else add all leveling roles to the String
        else {
            for (LevelingRole role : rolesList) {
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
