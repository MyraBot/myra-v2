package com.github.m5rian.myra.commands.administrator.leveling.levelingRoles;


import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import static com.github.m5rian.myra.utilities.language.Lang.*;
import com.github.m5rian.myra.utilities.permissions.Administrator;

public class LevelingRolesHelp implements CommandHandler {

    @CommandEvent(
            name = "leveling roles",
            aliases = {"leveling role"},
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return; // Check for no arguments

        // Command usage
        new CommandUsage(ctx.getEvent())
                .setCommand("leveling roles")
                .addUsages(
                        new Usage()
                                .setUsage("leveling roles add <level> <role>")
                                .setEmoji("\uD83D\uDD17")
                                .setDescription(lang(ctx).get("description.leveling.roles.Add")),
                        new Usage()
                                .setUsage("leveling roles remove <role>")
                                .setEmoji("\uD83D\uDDD1")
                                .setDescription(lang(ctx).get("description.leveling.roles.Remove")),
                        new Usage()
                                .setUsage("leveling roles list")
                                .setEmoji("\uD83D\uDCC3")
                                .setDescription(lang(ctx).get("description.leveling.roles.List")),
                        new Usage()
                                .setUsage("leveling roles unique")
                                .setEmoji("\uD83E\uDD84")
                                .setDescription(lang(ctx).get("description.leveling.roles.Unique")))
                .send();
    }
}
