package com.myra.dev.marian.listeners.welcome.welcomeEmbed;

import com.myra.dev.marian.database.guild.MongoGuild;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import net.dv8tion.jda.api.Permission;

public class WelcomeEmbedToggle implements CommandHandler {

@CommandEvent(
        name = "welcome embed toggle"
)
    public void execute(CommandContext ctx) throws Exception {
        //missing permissions
        if (!ctx.getEvent().getMember().hasPermission(Permission.ADMINISTRATOR)) return;
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        //toggle feature
        new MongoGuild(ctx.getGuild()).getListenerManager().toggle("welcomeEmbed", "\uD83D\uDCC7", ctx.getEvent());
    }
}
