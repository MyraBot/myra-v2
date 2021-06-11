package com.github.m5rian.myra.commands.member.general;


import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;

import static com.github.m5rian.myra.utilities.language.Lang.*;

public class Calculate implements CommandHandler {

    @CommandEvent(
            name = "calculate",
            aliases = {"cal"},
            args = {"<number 1>", "<operator>", "<number 2>"},
            emoji = "\uD83E\uDDEE",
            description = "description.general.calculate"
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0 || ctx.getArguments().length > 3) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("format")
                    .addUsages(new Usage()
                            .setUsage("calculate <number> <operator> <number>")
                            .setEmoji("\uD83E\uDDEE")
                            .setDescription(lang(ctx).get("description.general.calculate")))
                    .send();
            return;
        }

        // Calculate
        try {
            final double number1 = Double.parseDouble(ctx.getArguments()[0].replace(",", "."));
            final double number2 = Double.parseDouble(ctx.getArguments()[2].replace(",", "."));
            double result = switch (ctx.getArguments()[1]) {
                case "+" -> number1 + number2;
                case "-" -> number1 - number2;
                case "*", "⋅", "x" -> number1 * number2;
                case "/", ":" -> number1 / number2;
                default -> 0;
            };

            new Success(ctx.getEvent())
                    .setCommand("calculate")
                    .setEmoji("\uD83E\uDDEE")
                    .setMessage(lang(ctx).get("command.general.calculate.result")
                            .replace("{$calculation}", ctx.getArguments()[0] + " " + ctx.getArguments()[1].replace("*", "⋅").replace("x", "⋅").replace("/", ":") + " " + ctx.getArguments()[2])
                            .replace("{$result}", String.valueOf(result)))
                    .send();
        }
        // Error occurred
        catch (Exception e) {
            e.printStackTrace();
            new Error(ctx.getEvent())
                    .setCommand("calculate")
                    .setEmoji("\uD83E\uDDEE")
                    .setMessage(lang(ctx).get("command.general.calculate.error"))
                    .send();
        }
    }
}