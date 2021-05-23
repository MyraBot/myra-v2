package com.myra.dev.marian.commands.administrator;


import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import static com.myra.dev.marian.utilities.language.Lang.*;
import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

public class Say implements CommandHandler {

@CommandEvent(
        name = "say",
        aliases = {"write"},
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

        ctx.getEvent().getMessage().delete().queue(); // Delete command usage
        ctx.getChannel().sendMessage(ctx.getArgumentsRaw()).queue(); // Send message
    }
}