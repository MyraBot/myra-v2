package com.github.m5rian.myra.commands.member.help;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.utilities.APIs.TopGG;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

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
                .setMessage(Lang.lang(ctx).get("command.help.vote.message")
                        .replace("{$url}", "https://top.gg/bot/" + Config.MYRA_ID + "/vote") // Vote url
                        .replace("{$votes}", TopGG.getInstance().getUpVotes())) // Bot votes
                .send();
    }
}
