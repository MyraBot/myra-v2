package com.myra.dev.marian.listeners.welcome.welcomeEmbed;

import com.myra.dev.marian.database.guild.MongoGuild;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import net.dv8tion.jda.api.Permission;

@CommandSubscribe(
        name = "welcome embed toggle"
)
public class WelcomeEmbedToggle implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        //missing permissions
        if (!ctx.getEvent().getMember().hasPermission(Permission.ADMINISTRATOR)) return;
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        //toggle feature
        new MongoGuild(ctx.getGuild()).getListenerManager().toggle("welcomeEmbed", "\uD83D\uDCC7", ctx.getEvent());
    }
}
