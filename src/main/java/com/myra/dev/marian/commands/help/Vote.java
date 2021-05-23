package com.myra.dev.marian.commands.help;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.Config;
import com.myra.dev.marian.utilities.APIs.TopGG;
import com.myra.dev.marian.utilities.EmbedMessage.Success;

import static com.myra.dev.marian.utilities.language.Lang.lang;

public class Vote implements CommandHandler {

    @CommandEvent(
            name = "vote",
            aliases = {"v", "top.gg"}
    )
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;

        new Success(ctx.getEvent())
                .setCommand("vote")
                .setEmoji("\uD83D\uDDF3")
                .setMessage(lang(ctx).get("command.help.vote.message")
                        .replace("{$url}", "https://top.gg/bot/" + Config.MYRA_ID + "/vote") // Vote url
                        .replace("{$votes}", TopGG.getInstance().getUpVotes())); // Bot votes
    }
}
