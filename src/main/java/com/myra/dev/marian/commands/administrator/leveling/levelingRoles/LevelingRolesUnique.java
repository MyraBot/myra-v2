package com.myra.dev.marian.commands.administrator.leveling.levelingRoles;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.permissions.Administrator;

public class LevelingRolesUnique implements CommandHandler {

@CommandEvent(
        name = "leveling roles unique",
        aliases = {"leveling role unique"},
        requires = Administrator.class
)
    public void execute(CommandContext ctx) throws Exception {
        final MongoGuild db = new MongoGuild(ctx.getGuild()); // Get database
        final boolean oldValue = db.getNested("leveling").getBoolean("uniqueRoles"); // Get old value
        final boolean newValue = !oldValue; // Get new value
        db.getNested("leveling").setBoolean("uniqueRoles", newValue); // Update database

        Success success = new Success(ctx.getEvent())
                .setCommand("leveling roles unique")
                .setEmoji("\uD83E\uDD84");
        if (newValue) success.setMessage("Members get now only one leveling role at the same time");
        else success.setMessage("Leveling roles get now stacked on each other");
        success.send();
    }
}
