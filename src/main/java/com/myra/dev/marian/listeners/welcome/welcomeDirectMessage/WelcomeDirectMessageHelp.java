package com.myra.dev.marian.listeners.welcome.welcomeDirectMessage;


import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.permissions.Administrator;

import static com.myra.dev.marian.utilities.language.Lang.lang;

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
                                    .setDescription(lang(ctx).get("description.welcome.dm.toggle")),
                            new Usage()
                                    .setUsage("welcome direct message message <message>")
                                    .setEmoji("\uD83D\uDCAC")
                                    .setDescription(lang(ctx).get("description.welcome.dm.message")))
                    .send();
        }
    }
}
