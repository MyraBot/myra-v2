package com.myra.dev.marian.listeners.welcome.welcomeDirectMessage;


import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.EmbedBuilder;

public class WelcomeDirectMessageHelp implements CommandHandler {

@CommandEvent(
        name = "welcome direct message",
        aliases = {"welcome dm"},
        requires = Administrator.class
)
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Usage
        EmbedBuilder welcomeDirectMessage = new EmbedBuilder()
                .setAuthor("welcome direct message", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .addField("`" + ctx.getPrefix() + "welcome direct message toggle`", "\uD83D\uDD11 │ Toggle welcome images on and off", false)
                .addField("`" + ctx.getPrefix() + "welcome direct message message <message>`", "\uD83D\uDCAC │ change the text of the direct messages", false);
        ctx.getChannel().sendMessage(welcomeDirectMessage.build()).queue();
    }
}
