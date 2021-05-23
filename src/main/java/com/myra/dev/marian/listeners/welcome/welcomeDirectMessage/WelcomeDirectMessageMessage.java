package com.myra.dev.marian.listeners.welcome.welcomeDirectMessage;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.permissions.Administrator;

import static com.myra.dev.marian.utilities.language.Lang.lang;

public class WelcomeDirectMessageMessage implements CommandHandler {

    @CommandEvent(
            name = "welcome direct message message",
            aliases = {"welcome dm message"},
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("welcome direct message message")
                    .addUsages(new Usage()
                            .setUsage("welcome direct message message <message>")
                            .setEmoji("\uD83D\uDCAC")
                            .setDescription(lang(ctx).get("description.welcome.dm.message")))
                    .addInformation(lang(ctx).get("info.variables") + "\n" +
                            "\n{member} - " + lang(ctx).get("command.welcome.info.variables.member") +
                            "\n{server} - " + lang(ctx).get("command.welcome.info.variables.server") +
                            "\n{count} - " + lang(ctx).get("command.welcome.info.variables.count"))
                    .send();
            return;
        }

        new MongoGuild(ctx.getGuild()).getNested("welcome").setString("welcomeDirectMessage", ctx.getArgumentsRaw()); // Update database

        Success success = new Success(ctx.getEvent())
                .setCommand("welcome direct message")
                .setEmoji("\uD83D\uDCAC")
                .setMessage(lang(ctx).get("command.welcome.dm.info.done")
                        .replace("{$message}", ctx.getArgumentsRaw()));
        success.send();
    }
}
