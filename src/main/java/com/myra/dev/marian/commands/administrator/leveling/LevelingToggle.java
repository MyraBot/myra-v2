package com.myra.dev.marian.commands.administrator.leveling;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.permissions.Administrator;

public class LevelingToggle implements CommandHandler {

@CommandEvent(
        name = "leveling toggle",
        requires = Administrator.class,
        channel = Channel.GUILD
)
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return;

        MongoGuild db = new MongoGuild(ctx.getGuild());
        db.getListenerManager().toggle("leveling", "\uD83C\uDFC6", ctx.getEvent());
    }
}
