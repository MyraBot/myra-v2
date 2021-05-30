package com.github.m5rian.myra.listeners.suggestions;


import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.permissions.Administrator;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class SuggestionsHelp implements CommandHandler {

    @CommandEvent(
            name = "suggestions",
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        //usage
        if (ctx.getArguments().length == 0) {
            // Command usage
            new CommandUsage(ctx.getEvent())
                    .setCommand("welcome")
                    .addUsages(
                            new Usage().setUsage("suggestions toggle")
                                    .setEmoji("\uD83D\uDD11")
                                    .setDescription(Lang.lang(ctx).get("description.suggestions.toggle")),
                            new Usage().setUsage("suggestions channel <channel>")
                                    .setEmoji("\uD83D\uDCC1")
                                    .setDescription(Lang.lang(ctx).get("description.suggestions.channel")),
                            new Usage().setUsage("suggest <suggestion>")
                                    .setEmoji("\uD83D\uDDF3")
                                    .setDescription(Lang.lang(ctx).get("description.suggest")))
                    .send();
            return;
        }
    }
}
