package com.github.m5rian.myra.commands.member.fun;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.APIs.Reddit;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.language.Lang;

public class Meme implements CommandHandler {

@CommandEvent(
        name = "meme",
        aliases = {"memes"}
)
    public void execute(CommandContext ctx) throws Exception {
        try {
            ctx.getChannel().sendMessage(new Reddit().getMeme(ctx.getAuthor()).build()).queue();
        } catch (Exception e) {
            new Error(ctx.getEvent())
                    .setCommand("meme")
                    .setEmoji("\uD83E\uDD2A")
                    .setMessage(Lang.lang(ctx).get("command.fun.meme.error.unknown"))
                    .send();
        }
    }
}