package com.myra.dev.marian.listeners.welcome.WelcomeImage;


import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "welcome image",
        requires = Administrator.class
)
public class WelcomeImageHelp implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Usage
        EmbedBuilder usage = new EmbedBuilder()
                .setAuthor("welcome image", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().gray)
                .addField("`" + ctx.getPrefix() + "welcome image toggle`", "\uD83D\uDD11 │ Toggle welcome images on and off", false)
                .addField("`" + ctx.getPrefix() + "welcome image background <url>`", "\uD83D\uDDBC │ Change the background of the welcome images", false)
                .addField("`" + ctx.getPrefix() + "welcome image font`", "\uD83D\uDDDB │ Change the font of the text", false);
        ctx.getChannel().sendMessage(usage.build()).queue();
    }
}
