package com.myra.dev.marian.listeners.welcome.welcomeEmbed;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "welcome embed"
)
public class WelcomeEmbedHelp implements Command {
    @Override
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
