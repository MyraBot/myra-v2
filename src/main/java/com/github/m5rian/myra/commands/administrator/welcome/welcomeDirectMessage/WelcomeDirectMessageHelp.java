package com.github.m5rian.myra.commands.administrator.welcome.welcomeDirectMessage;


import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.permissions.Administrator;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class WelcomeDirectMessageHelp implements CommandHandler {

    @CommandEvent(
            name = "welcome direct message",
            aliases = {"welcome dm"},
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) {
            // Command usage
            new CommandUsage(ctx.getEvent())
                    .setCommand("welcome direct message")
                    .addUsages(
                            new Usage()
                                    .setUsage("welcome direct message toggle")
                                    .setEmoji("\uD83D\uDD11")
                                    .setDescription(Lang.lang(ctx).get("description.welcome.dm.toggle")),
                            new Usage()
                                    .setUsage("welcome direct message message <message>")
                                    .setEmoji("\uD83D\uDCAC")
                                    .setDescription(Lang.lang(ctx).get("description.welcome.dm.message")))
                    .send();
        }
    }
}
