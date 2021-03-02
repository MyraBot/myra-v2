package com.myra.dev.marian.commands.administrator.leveling;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.utilities.permissions.Administrator;

@CommandSubscribe(
        name = "leveling toggle",
        requires = Administrator.class,
        channel = Channel.GUILD
)
public class LevelingToggle implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return;

        Database db = new Database(ctx.getGuild());
        db.getListenerManager().toggle("leveling", "\uD83C\uDFC6", ctx.getEvent());
    }
}
