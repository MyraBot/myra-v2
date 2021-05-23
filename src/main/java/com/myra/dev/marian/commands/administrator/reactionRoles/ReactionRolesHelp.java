package com.myra.dev.marian.commands.administrator.reactionRoles;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import static com.myra.dev.marian.utilities.language.Lang.*;
import com.myra.dev.marian.utilities.permissions.Administrator;

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
