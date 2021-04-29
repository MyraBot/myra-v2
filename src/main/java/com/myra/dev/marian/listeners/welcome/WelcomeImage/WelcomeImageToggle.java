package com.myra.dev.marian.listeners.welcome.WelcomeImage;

import com.myra.dev.marian.database.guild.MongoGuild;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import net.dv8tion.jda.api.Permission;

public class WelcomeImageToggle implements CommandHandler {

@CommandEvent(
        name = "welcome image toggle"
)
    public void execute(CommandContext ctx) throws Exception {
        MongoGuild db = new MongoGuild(ctx.getGuild());
        //missing permissions
        if (!ctx.getMember().hasPermission(Permission.ADMINISTRATOR)) return;
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        //toggle
        db.getListenerManager().toggle("welcomeImage", "\uD83D\uDDBC", ctx.getEvent());
    }
}