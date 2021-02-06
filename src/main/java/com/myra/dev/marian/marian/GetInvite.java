package com.myra.dev.marian.marian;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.permissions.Marian;

@CommandSubscribe(
        name = "get invite",
        requires = Marian.class
)
public class GetInvite implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for marian
        if (!ctx.getAuthor().getId().equals(Config.marian)) return;
        // Get invite link to default channel
        String invite = ctx.getEvent().getJDA().getGuildById(ctx.getArguments()[0]).getDefaultChannel().createInvite().setMaxUses(1).complete().getUrl();
        // Send link
        ctx.getChannel().sendMessage(invite).queue();
    }
}
