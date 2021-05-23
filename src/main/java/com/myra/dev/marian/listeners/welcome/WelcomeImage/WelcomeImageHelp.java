package com.myra.dev.marian.listeners.welcome.WelcomeImage;


import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.permissions.Administrator;

import static com.myra.dev.marian.utilities.language.Lang.lang;

public class WelcomeImageHelp implements CommandHandler {

    @CommandEvent(
            name = "welcome image",
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length == 0) {
            // Command usage
            new CommandUsage(ctx.getEvent())
                    .setCommand("welcome image")
                    .addUsages(
                            new Usage()
                                    .setUsage("welcome image toggle")
                                    .setEmoji("\uD83D\uDD11")
                                    .setDescription(lang(ctx).get("description.welcome.image.toggle")),
                            new Usage()
                                    .setUsage("welcome image background <url>")
                                    .setEmoji("\uD83D\uDDBC")
                                    .setDescription(lang(ctx).get("description.welcome.image.background")),
                            new Usage()
                                    .setUsage("welcome image font")
                                    .setEmoji("\uD83D\uDDDB")
                                    .setDescription(lang(ctx).get("description.welcome.image.font")))
                    .send();
        }
    }
}
