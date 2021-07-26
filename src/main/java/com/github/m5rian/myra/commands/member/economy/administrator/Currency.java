package com.github.m5rian.myra.commands.member.economy.administrator;

import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.permissions.Administrator;

public class Currency implements CommandHandler {

    @CommandEvent(
            name = "economy currency",
            args = {"<currency>"},
            emoji = "\\\uD83D\uDCB1",
            description = "description.economy.currency",
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        final MongoGuild db = MongoGuild.get(ctx.getGuild()); // Get database

        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("economy currency")
                    .addUsages(new Usage()
                            .setUsage("economy currency <currency>")
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
                .setMessage(Lang.lang(ctx).get("command.economy.currency.info.success")
                        .replace("{$currency}", ctx.getArgumentsRaw()))
                .send();
    }
}
