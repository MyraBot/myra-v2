package com.myra.dev.marian.listeners.welcome;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.listeners.welcome.WelcomeImage.WelcomeImageRender;
import com.myra.dev.marian.listeners.welcome.welcomeDirectMessage.WelcomeDirectMessageRender;
import com.myra.dev.marian.listeners.welcome.welcomeEmbed.WelcomeEmbedRender;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.InputStream;

@CommandSubscribe(
        name = "welcome preview",
        requires = Administrator.class
)
public class WelcomePreview implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return; // Check for no arguments
        ctx.getChannel().sendTyping().queue(); // Send typing
        Database db = new Database(ctx.getGuild()); // Get database
        // Get greetings
        final MessageEmbed privateMessage = new WelcomeDirectMessageRender().render(ctx.getGuild(), ctx.getAuthor()); // Get direct message
        final MessageEmbed embed = new WelcomeEmbedRender().render(ctx.getGuild(), ctx.getAuthor()); // Get embed message
        final InputStream welcomeImage = new WelcomeImageRender().render(ctx.getGuild(), ctx.getAuthor()); // Get welcome image

        // Welcome direct message is enabled
        if (db.getListenerManager().check("welcomeDirectMessage")) {
            ctx.getAuthor().openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage(privateMessage).queue();
            });
        }
        // Welcome embed is enabled
        if (db.getListenerManager().check("welcomeEmbed")) {
            ctx.getChannel().sendMessage(embed).queue();
        }
        // Welcome Image is enabled
        if (db.getListenerManager().check("welcomeImage")) {
            ctx.getChannel().sendFile(welcomeImage, ctx.getAuthor().getName().toLowerCase() + "_welcome.png").queue();
        }
    }
}
