package com.github.m5rian.myra.commands.member.general;


import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Calculate implements CommandHandler {

    @CommandEvent(
            name = "calculate",
            aliases = {"cal"},
            args = {"<calculation>"},
            emoji = "\uD83E\uDDEE",
            description = "description.general.calculate"
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            usage(ctx).send();
            return;
        }

            try {
                final Calculator.Calculation calculation = Calculator.calculate(ctx.getArgumentsRaw().replaceAll("\\s+", ""));
                info(ctx).setDescription(lang(ctx).get("command.general.calculate.result")
                        .replace("{$calculation}", calculation.calculation())
                        .replace("{$result}", String.valueOf(calculation.result())))
                        .send();
            } catch (IllegalArgumentException e) {
                error(ctx).setDescription(lang(ctx).get("command.general.calculate.error")).send();
            }
    }
}