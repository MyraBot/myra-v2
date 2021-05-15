package com.myra.dev.marian.marian;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.Config;
import com.myra.dev.marian.utilities.permissions.Marian;

public class GetInvite implements CommandHandler {
    @CommandEvent(
        name = "get invite",
        requires = Marian.class
)
    public void execute(CommandContext ctx) throws Exception {
        // Check for marian
        if (!ctx.getAuthor().getId().equals(Config.MARIAN_SERVER_ID)) return;
        // Get invite link to default channel
        String invite = ctx.getEvent().getJDA().getGuildById(ctx.getArguments()[0]).getDefaultChannel().createInvite().setMaxUses(1).complete().getUrl();
        // Send link
        ctx.getChannel().sendMessage(invite).queue();
    }
}
