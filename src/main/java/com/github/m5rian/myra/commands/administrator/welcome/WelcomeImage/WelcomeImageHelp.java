package com.github.m5rian.myra.commands.administrator.welcome.WelcomeImage;


import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.permissions.Administrator;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

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
                                    .setDescription(Lang.lang(ctx).get("description.welcome.image.toggle")),
                            new Usage()
                                    .setUsage("welcome image background <url>")
                                    .setEmoji("\uD83D\uDDBC")
                                    .setDescription(Lang.lang(ctx).get("description.welcome.image.background")),
                            new Usage()
                                    .setUsage("welcome image font")
                                    .setEmoji("\uD83D\uDDDB")
                                    .setDescription(Lang.lang(ctx).get("description.welcome.image.font")))
                    .send();
        }
    }
}
