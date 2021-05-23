package com.myra.dev.marian.commands.administrator.leveling.levelingRoles;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.LevelingRole;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.Utilities;
import static com.myra.dev.marian.utilities.language.Lang.*;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Comparator;
import java.util.List;

public class LevelingRolesList implements CommandHandler {

    @CommandEvent(
            name = "leveling roles list",
            aliases = {"leveling role list"},
            requires = Administrator.class,
            channel = Channel.GUILD
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
                final String roleMention = ctx.getGuild().getRoleById(role.getRole()).getAsMention(); // Get role as mention
                roles
                        .append("• ").append(lang(ctx).get("command.leveling.roles.list.info.level")).append(": `").append(role.getLevel()) // Required level
                        .append("` ").append(lang(ctx).get("command.leveling.roles.list.info.add")).append(": ").append(roleMention) // Role
                        .append("\n");
            }
        }

        // Create embed
        EmbedBuilder levelingRoles = new EmbedBuilder()
                .setAuthor("leveling roles list", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.blue)
                .setDescription(roles.toString());
        ctx.getChannel().sendMessage(levelingRoles.build()).queue();
    }
}
