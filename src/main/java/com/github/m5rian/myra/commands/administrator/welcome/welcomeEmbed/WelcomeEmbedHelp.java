package com.github.m5rian.myra.commands.administrator.welcome.welcomeEmbed;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.language.Lang;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class WelcomeEmbedHelp implements CommandHandler {

    @CommandEvent(
            name = "welcome embed"
    )
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length == 0) {
            // Command usage
            new CommandUsage(ctx.getEvent())
                    .setCommand("welcome embed")
                    .addUsages(
                            new Usage()
                                    .setUsage("welcome embed toggle")
                                    .setEmoji("\uD83D\uDD11")
                                    .setDescription(Lang.lang(ctx).get("description.welcome.embed.toggle")),
                            new Usage()
                                    .setUsage("welcome embed message <message>")
                                    .setEmoji("\uD83D\uDCAC")
                                    .setDescription(Lang.lang(ctx).get("description.welcome.embed.message")))
                    .send();
        }
    }
}
