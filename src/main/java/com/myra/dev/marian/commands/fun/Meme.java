package com.myra.dev.marian.commands.fun;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.APIs.Reddit;
import com.myra.dev.marian.utilities.EmbedMessage.Error;

@CommandSubscribe(
        name = "meme",
        aliases = {"memes"}
)
public class Meme implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        try {
            ctx.getChannel().sendMessage(new Reddit().getMeme(ctx.getAuthor()).build()).queue();
        } catch (Exception e) {
            new Error(ctx.getEvent())
                    .setCommand("meme")
                    .setEmoji("\uD83E\uDD2A")
                    .setMessage(String.format("Yo, feel honored. This is a very rare error (:%n*Also I couldn't load the meme*"))
                    .send();
        }
    }
}