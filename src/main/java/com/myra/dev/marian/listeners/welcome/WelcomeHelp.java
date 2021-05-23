package com.myra.dev.marian.listeners.welcome;


import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.permissions.Administrator;

import static com.myra.dev.marian.utilities.language.Lang.lang;

public class WelcomeHelp implements CommandHandler {

    @CommandEvent(
            name = "welcome",
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length == 0) {
            // Command usage
            new CommandUsage(ctx.getEvent())
                    .setCommand("welcome")
                    .addUsages(
                            new Usage().setUsage("welcome image toggle")
                                    .setEmoji("\uD83D\uDDBC")
                                    .setDescription(lang(ctx).get("description.welcome.image")),
                            new Usage().setUsage("welcome image")
                                    .setEmoji("\uD83D\uDCC7")
                                    .setDescription(lang(ctx).get("description.welcome.embed")),
                            new Usage().setUsage("welcome embed")
                                    .setEmoji("\u2709\uFE0F")
                                    .setDescription(lang(ctx).get("description.welcome.dm")),
                            new Usage().setUsage("welcome channel <channel>")
                                    .setEmoji("\uD83D\uDCC1")
                                    .setDescription(lang(ctx).get("description.welcome.channel")),
                            new Usage().setUsage("welcome colour <hex colour>")
                                    .setEmoji("\uD83C\uDFA8")
                                    .setDescription(lang(ctx).get("description.welcome.colour")),
                            new Usage().setUsage("welcome preview")
                                    .setEmoji("\uD83D\uDCF8")
                                    .setDescription(lang(ctx).get("description.welcome.preview")))
                    .send();
        }
    }
}
