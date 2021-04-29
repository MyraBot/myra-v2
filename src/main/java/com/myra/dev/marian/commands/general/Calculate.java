package com.myra.dev.marian.commands.general;


import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

public class Calculate implements CommandHandler {

@CommandEvent(
        name = "calculate",
        aliases = {"cal"}
)
    public void execute(CommandContext ctx) throws Exception {
        //usage
        if (ctx.getArguments().length == 0 || ctx.getArguments().length > 3) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("calculate", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("\uD83E\uDDEE │ let the bot calculate something for you", "`" + ctx.getPrefix() + "calculate <number> <operator> <number>`", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
        //calculate
        try {
            double number1 = Double.parseDouble(ctx.getArguments()[0].replace(",", "."));
            double number2 = Double.parseDouble(ctx.getArguments()[2].replace(",", "."));
            double result = 0;
            switch (ctx.getArguments()[1]) {
                case "+":
                    result = number1 + number2;
                    break;
                case "-":
                    result = number1 - number2;
                    break;
                case "*":
                case "⋅":
                case "x":
                    result = number1 * number2;
                    break;
                case "/":
                case ":":
                    result = number1 / number2;
                    break;
            }
            EmbedBuilder calculated = new EmbedBuilder()
                    .setAuthor("calculated", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().blue)
                    .setDescription("the result of " + ctx.getArguments()[0] + " " + ctx.getArguments()[1].replace("*", "⋅").replace("x", "⋅").replace("/", ":") + " " + ctx.getArguments()[2] + " = " + result);
            ctx.getChannel().sendMessage(calculated.build()).queue();
        } catch (Exception e) {
            e.printStackTrace();
            new Error(ctx.getEvent())
                    .setCommand("calculate")
                    .setEmoji("\uD83E\uDDEE")
                    .setMessage("I think I'm too stupid for this task")
                    .send();
        }
    }
}