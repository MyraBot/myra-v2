package com.github.m5rian.myra.commands.administrator.welcome.welcomeDirectMessage;

import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import com.github.m5rian.myra.database.guild.MongoGuild;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

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
                            .setDescription(Lang.lang(ctx).get("description.welcome.dm.message")))
                    .addInformation(Lang.lang(ctx).get("info.variables") + "\n" +
                            "\n{member} - " + Lang.lang(ctx).get("command.welcome.info.variables.member") +
                            "\n{server} - " + Lang.lang(ctx).get("command.welcome.info.variables.server") +
                            "\n{count} - " + Lang.lang(ctx).get("command.welcome.info.variables.count"))
                    .send();
            return;
        }

        MongoGuild.get(ctx.getGuild()).getNested("welcome").setString("welcomeDirectMessage", ctx.getArgumentsRaw()); // Update database

        Success success = new Success(ctx.getEvent())
                .setCommand("welcome direct message")
                .setEmoji("\uD83D\uDCAC")
                .setMessage(Lang.lang(ctx).get("command.welcome.dm.info.done")
                        .replace("{$message}", ctx.getArgumentsRaw()));
        success.send();
    }
}
