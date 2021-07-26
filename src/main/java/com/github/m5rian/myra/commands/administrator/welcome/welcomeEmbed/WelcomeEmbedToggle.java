package com.github.m5rian.myra.commands.administrator.welcome.welcomeEmbed;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import com.github.m5rian.myra.database.guild.MongoGuild;

public class WelcomeEmbedToggle implements CommandHandler {

    @CommandEvent(
            name = "welcome embed toggle",
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length == 0) {
            // Toggle feature
            MongoGuild.get(ctx.getGuild()).getListenerManager().toggle("welcomeEmbed", "\uD83D\uDCC7", ctx.getEvent());
        }
    }
}
