package com.github.m5rian.myra.commands.administrator;


import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Say implements CommandHandler {

    @CommandEvent(
            name = "say",
            aliases = {"write"},
            args = {"<message>"},
            emoji = "\uD83D\uDCAC",
            description = "description.say",
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("say")
                    .addUsages(new Usage()
                            .setUsage("say <message>")
                            .setEmoji("\uD83D\uDCAC")
                            .setDescription(lang(ctx).get("description.say")))
                    .send();
            return;
        }

        if (ctx.getBotMember().hasPermission((GuildChannel) ctx.getChannel(), Permission.MESSAGE_MANAGE)) {
            ctx.getEvent().getMessage().delete().queue(); // Delete command usage
        }
        ctx.getChannel().sendMessage(ctx.getArgumentsRaw()).queue(); // Send message
    }
}