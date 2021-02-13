package com.myra.dev.marian.commands.leveling.administrator.levelingRoles;


import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.permissions.Administrator;

@CommandSubscribe(
        name = "leveling roles",
        aliases = {"leveling role"},
        requires = Administrator.class
)
public class LevelingRolesHelp implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return; // Check for no arguments
        // Command usage
        new CommandUsage(ctx.getEvent())
                .setCommand("leveling roles")
                .addUsages(
                        new Usage()
                                .setUsage("leveling roles add <level> <role>")
                                .setEmoji("\uD83D\uDD17")
                                .setDescription("Link a role to a level"),
                        new Usage()
                                .setUsage("leveling roles remove <role>")
                                .setEmoji("\uD83D\uDDD1")
                                .setDescription("Delete the linking between a level and a role"),
                        new Usage()
                                .setUsage("leveling roles list")
                                .setEmoji("\uD83D\uDCC3")
                                .setDescription("Shows you all linked up roles"),
                        new Usage()
                                .setUsage("leveling roles unique")
                                .setEmoji("\uD83E\uDD84")
                                .setDescription("Toggle if leveling roles can be stacked on each other")
                ).send();
    }
}
