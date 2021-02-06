package com.myra.dev.marian.commands.leveling.administrator.levelingRoles;


import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "leveling roles",
        aliases = {"leveling role"},
        requires = Administrator.class
)
public class LevelingRolesHelp implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Usage
        EmbedBuilder usage = new EmbedBuilder()
                .setAuthor("leveling roles", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().gray)
                .addField("`" + ctx.getPrefix() + "leveling roles add <level> <role> [remove]`", "\uD83D\uDD17 │ Link a role to a level", false)
                .addField("`" + ctx.getPrefix() + "leveling roles remove <role>`", "\uD83D\uDDD1 │ Delete the linking between a level and a role", false)
                .addField("`" + ctx.getPrefix() + "leveling roles list`", "\uD83D\uDCC3 │ Shows you all linked up roles", false);
        ctx.getChannel().sendMessage(usage.build()).queue();
    }
}
