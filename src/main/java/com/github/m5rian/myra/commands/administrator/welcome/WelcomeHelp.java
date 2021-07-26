package com.github.m5rian.myra.commands.administrator.welcome;


import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.permissions.Administrator;

public class WelcomeHelp implements CommandHandler {

    @CommandEvent(
            name = "welcome",
            emoji = "\uD83D\uDC4B",
            description = "description.welcome",
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length == 0) {
            // Command usage
            new CommandUsage(ctx.getEvent())
                    .setCommand("welcome")
                    .addUsages(
                            new Usage().setUsage("welcome image toggle")
                                    .setEmoji("\uD83D\uDDBC")
                                    .setDescription(Lang.lang(ctx).get("description.welcome.image")),
                            new Usage().setUsage("welcome image")
                                    .setEmoji("\uD83D\uDCC7")
                                    .setDescription(Lang.lang(ctx).get("description.welcome.embed")),
                            new Usage().setUsage("welcome embed")
                                    .setEmoji("\u2709\uFE0F")
                                    .setDescription(Lang.lang(ctx).get("description.welcome.dm")),
                            new Usage().setUsage("welcome channel <channel>")
                                    .setEmoji("\uD83D\uDCC1")
                                    .setDescription(Lang.lang(ctx).get("description.welcome.channel")),
                            new Usage().setUsage("welcome colour <hex colour>")
                                    .setEmoji("\uD83C\uDFA8")
                                    .setDescription(Lang.lang(ctx).get("description.welcome.colour")),
                            new Usage().setUsage("welcome preview")
                                    .setEmoji("\uD83D\uDCF8")
                                    .setDescription(Lang.lang(ctx).get("description.welcome.preview")))
                    .send();
        }
    }
}
