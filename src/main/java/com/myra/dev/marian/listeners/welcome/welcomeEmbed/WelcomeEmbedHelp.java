package com.myra.dev.marian.listeners.welcome.welcomeEmbed;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

public class WelcomeEmbedHelp implements CommandHandler {

@CommandEvent(
        name = "welcome embed"
)
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Usage
        EmbedBuilder welcomeEmbed = new EmbedBuilder()
                .setAuthor("welcome embed", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().gray)
                .addField("`" + ctx.getPrefix() + "welcome embed toggle`", "\uD83D\uDD11 │ Toggle welcome embeds on and off", false)
                .addField("`" + ctx.getPrefix() + "welcome embed message <message>`", "\uD83D\uDCAC │ Set the text of the embed message", false);
        ctx.getChannel().sendMessage(welcomeEmbed.build()).queue();
        return;
    }
}
