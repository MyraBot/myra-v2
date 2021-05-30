package com.github.m5rian.myra.commands.administrator.welcome.welcomeEmbed;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import com.github.m5rian.myra.database.guild.MongoGuild;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class WelcomeEmbedMessage implements CommandHandler {

    @CommandEvent(
            name = "welcome embed message",
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("welcome embed message")
                    .addUsages(new Usage()
                            .setUsage("welcome embed message <message>")
                            .setEmoji("\uD83D\uDCAC")
                            .setDescription(Lang.lang(ctx).get("description.welcome.embed.message")))
                    .addInformation(Lang.lang(ctx).get("info.variables") + "\n" +
                            "\n{member} - " + Lang.lang(ctx).get("command.welcome.info.variables.member") +
                            "\n{server} - " + Lang.lang(ctx).get("command.welcome.info.variables.server") +
                            "\n{count} - " + Lang.lang(ctx).get("command.welcome.info.variables.count"))
                    .send();
            return;
        }

        final MongoGuild db = new MongoGuild(ctx.getGuild()); // Get database
        db.getNested("welcome").setString("welcome", ctx.getArgumentsRaw()); // Update database
        // Success
        Success success = new Success(ctx.getEvent())
                .setCommand("welcome embed message")
                .setEmoji("\uD83D\uDCAC")
                .setMessage(Lang.lang(ctx).get("command.welcome.embed.info.done")
                        .replace("{$message}", ctx.getArgumentsRaw()));
        success.send();
    }
}