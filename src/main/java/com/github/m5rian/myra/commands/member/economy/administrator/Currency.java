package com.github.m5rian.myra.commands.member.economy.administrator;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;

public class Currency implements CommandHandler {

    @CommandEvent(
            name = "economy currency",
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        final MongoGuild db = new MongoGuild(ctx.getGuild()); // Get database

        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("economy currency")
                    .addUsages(new Usage()
                            .setUsage("economy currency <emoji>")
                            .setEmoji(db.getNested("economy").getString("currency"))
                            .setDescription(Lang.lang(ctx).get("description.economy.currency")))
                    .send();
            return;
        }

        db.getNested("economy").setString("currency", ctx.getArgumentsRaw()); // Update database
        // Send success message
        new Success(ctx.getEvent())
                .setCommand("economy currency")
                .setEmoji(ctx.getArgumentsRaw())
                .setMessage(Lang.lang(ctx).get("command.economy.currency.info.success"))
                .send();
    }
}