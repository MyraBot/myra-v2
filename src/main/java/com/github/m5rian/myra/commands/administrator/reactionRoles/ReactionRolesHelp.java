package com.github.m5rian.myra.commands.administrator.reactionRoles;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import static com.github.m5rian.myra.utilities.language.Lang.*;
import com.github.m5rian.myra.utilities.permissions.Administrator;

public class ReactionRolesHelp implements CommandHandler {

    @CommandEvent(
            name = "reaction roles",
            aliases = {"reaction role", "rr"},
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            // Command usage
            new CommandUsage(ctx.getEvent())
                    .setCommand("reaction roles")
                    .addUsages(new Usage()
                                    .setUsage("reaction roles add <role>")
                                    .setEmoji("\uD83D\uDD17")
                                    .setDescription(lang(ctx).get("description.reactionRoles.add")),
                            new Usage()
                                    .setUsage("reaction roles remove")
                                    .setEmoji("\uD83D\uDDD1")
                                    .setDescription(lang(ctx).get("description.reactionRoles.remove")))
                    .send();
        }
    }
}
