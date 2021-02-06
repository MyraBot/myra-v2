package com.myra.dev.marian.listeners.welcome.WelcomeImage;

import com.myra.dev.marian.database.allMethods.Database;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import net.dv8tion.jda.api.Permission;

@CommandSubscribe(
        name = "welcome image toggle"
)
public class WelcomeImageToggle implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        Database db = new Database(ctx.getGuild());
        //missing permissions
        if (!ctx.getMember().hasPermission(Permission.ADMINISTRATOR)) return;
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        //toggle
        db.getListenerManager().toggle("welcomeImage", "\uD83D\uDDBC", ctx.getEvent());
    }
}