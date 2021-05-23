package com.myra.dev.marian.listeners.suggestions;


import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.permissions.Administrator;

import static com.myra.dev.marian.utilities.language.Lang.lang;

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
                                    .setDescription(lang(ctx).get("description.suggestions.toggle")),
                            new Usage().setUsage("suggestions channel <channel>")
                                    .setEmoji("\uD83D\uDCC1")
                                    .setDescription(lang(ctx).get("description.suggestions.channel")),
                            new Usage().setUsage("suggest <suggestion>")
                                    .setEmoji("\uD83D\uDDF3")
                                    .setDescription(lang(ctx).get("description.suggest")))
                    .send();
            return;
        }
    }
}
