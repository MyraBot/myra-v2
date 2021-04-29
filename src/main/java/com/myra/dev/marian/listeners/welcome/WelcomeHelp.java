package com.myra.dev.marian.listeners.welcome;


import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

public class WelcomeHelp implements CommandHandler {

@CommandEvent(
        name = "welcome",
        requires = Administrator.class
)
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Usage
        EmbedBuilder welcomeUsage = new EmbedBuilder()
                .setAuthor("welcome", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().gray)
                .addField("`" + ctx.getPrefix() + "welcome image`", "\uD83D\uDDBC │ Change the settings for the welcome image", false)
                .addField("`" + ctx.getPrefix() + "welcome embed`", "\uD83D\uDCC7 │ Change the settings for the welcome embed", false)
                .addField("`" + ctx.getPrefix() + "welcome direct message`", "\u2709\uFE0F │ Change the settings for the welcome direct message", false)
                .addField("`" + ctx.getPrefix() + "welcome channel <channel>`", "\uD83D\uDCC1 │ Set the channel, the welcome message will go", false)
                .addField("`" + ctx.getPrefix() + "welcome colour <hex colour>`", "\uD83C\uDFA8 │ Set the colour of the embeds", false)
                .addField("`" + ctx.getPrefix() + "welcome preview`", "\uD83D\uDCF8 │ Get a preview of the current welcome", false);
        ctx.getChannel().sendMessage(welcomeUsage.build()).queue();
    }
}
