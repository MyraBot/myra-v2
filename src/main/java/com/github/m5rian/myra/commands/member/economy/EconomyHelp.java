package com.github.m5rian.myra.commands.member.economy;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.commands.member.economy.administrator.Currency;
import com.github.m5rian.myra.commands.member.economy.administrator.EconomySet;
import com.github.m5rian.myra.utilities.permissions.Administrator;

public class EconomyHelp implements CommandHandler {

    @CommandEvent(
            name = "economy",
            emoji = "\uD83D\uDCB0",
            description = "description.economy",
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length == 0) {
            // Send command usages
            usage(ctx).addUsages(
                    EconomySet.class,
                    Currency.class)
                    .send();
        }
    }
}
