package com.myra.dev.marian.commands.economy.administrator;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import static com.myra.dev.marian.utilities.language.Lang.*;
import com.myra.dev.marian.utilities.permissions.Administrator;

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
                            .setDescription(lang(ctx).get("description.economy.currency")))
                    .send();
            return;
        }

        db.getNested("economy").setString("currency", ctx.getArgumentsRaw()); // Update database
        // Send success message
        new Success(ctx.getEvent())
                .setCommand("economy currency")
                .setEmoji(ctx.getArgumentsRaw())
                .setMessage(lang(ctx).get("command.economy.currency.info.success"))
                .send();
    }
}
