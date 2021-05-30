package com.github.m5rian.myra.commands.administrator.leveling;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import static com.github.m5rian.myra.utilities.language.Lang.*;
import com.github.m5rian.myra.utilities.permissions.Administrator;

public class LevelingHelp implements CommandHandler {

    @CommandEvent(
            name = "leveling",
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;

        // Send command usages
        new CommandUsage(ctx.getEvent())
                .setCommand("leveling")
                .addUsages(new Usage()
                                .setUsage("leveling toggle")
                                .setEmoji("\uD83D\uDD11")
                                .setDescription(lang(ctx).get("description.leveling.toggle")),
                        new Usage()
                                .setUsage("leveling set <user> <level>")
                                .setEmoji("\uD83C\uDFC6")
                                .setDescription(lang(ctx).get("description.leveling.set")),
                        new Usage()
                                .setUsage("leveling roles")
                                .setEmoji("\uD83D\uDD17")
                                .setDescription(lang(ctx).get("description.leveling.roles")),
                        new Usage()
                                .setUsage("leveling channel <channel>")
                                .setEmoji("\uD83E\uDDFE")
                                .setDescription(lang(ctx).get("description.leveling.channel")))
                .send();
    }
}
