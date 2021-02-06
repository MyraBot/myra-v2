package com.myra.dev.marian.commands.help;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.APIs.TopGG;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "vote",
        aliases = {"v", "top.gg"}
)
public class Vote implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        EmbedBuilder vote = new EmbedBuilder()
                .setAuthor("vote", "https://top.gg/bot/718444709445632122/vote", ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("You want to " + Utilities.getUtils().hyperlink("vote", "https://top.gg/bot/718444709445632122/vote") + " for me? That would be awesome!\nCurrent votes: `" + TopGG.getInstance().getUpVotes() + "`");
        ctx.getChannel().sendMessage(vote.build()).queue();
    }
}
