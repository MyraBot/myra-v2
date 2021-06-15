package com.github.m5rian.myra.commands.administrator.leveling.levelingRoles;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import static com.github.m5rian.myra.utilities.language.Lang.*;
import com.github.m5rian.myra.utilities.permissions.Administrator;

public class LevelingRolesUnique implements CommandHandler {

@CommandEvent(
        name = "leveling roles unique",
        aliases = {"leveling role unique"},
        requires = Administrator.class,
        channel = Channel.GUILD
)
    public void execute(CommandContext ctx) throws Exception {
        final MongoGuild db = MongoGuild.get(ctx.getGuild()); // Get database
        final boolean unique = !db.getNested("leveling").getBoolean("uniqueRoles"); // Get new value
        db.getNested("leveling").setBoolean("uniqueRoles", unique); // Update database

        Success success = new Success(ctx.getEvent())
                .setCommand("leveling roles unique")
                .setEmoji("\uD83E\uDD84");
        if (unique) success.setMessage(lang(ctx).get("command.leveling.roles.unique.info.true"));
        else success.setMessage(lang(ctx).get("command.leveling.roles.unique.info.false"));
        success.send();
    }
}
