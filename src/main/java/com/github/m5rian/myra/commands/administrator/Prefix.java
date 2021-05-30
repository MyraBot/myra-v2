package com.github.m5rian.myra.commands.administrator;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;

public class Prefix implements CommandHandler {

    @CommandEvent(
            name = "prefix",
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command arguments
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("prefix")
                    .addUsages(new Usage()
                            .setUsage("prefix <prefix>")
                            .setEmoji("\uD83D\uDCCC")
                            .setDescription(Lang.lang(ctx).get("description.prefix")))
                    .send();
            return;
        }
        // Change the prefix
        new MongoGuild(ctx.getGuild()).setString("prefix", ctx.getArguments()[0]); // Change prefix
        // Success information
        Success success = new Success(ctx.getEvent())
                .setCommand("prefix")
                .setEmoji("\uD83D\uDCCC")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setMessage(
                        Lang.lang(ctx).get("command.prefix.info.success").replace("{$prefix}", ctx.getArguments()[0])
                );
        success.send();
    }
}
