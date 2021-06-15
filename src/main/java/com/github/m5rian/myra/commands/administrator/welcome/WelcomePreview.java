package com.github.m5rian.myra.commands.administrator.welcome;

import com.github.m5rian.myra.listeners.welcome.WelcomeImageRender;
import com.github.m5rian.myra.listeners.welcome.WelcomeDirectMessageRender;
import com.github.m5rian.myra.listeners.welcome.WelcomeEmbedRender;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.InputStream;

public class WelcomePreview implements CommandHandler {

@CommandEvent(
        name = "welcome preview",
        requires = Administrator.class
)
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return; // Check for no arguments
        ctx.getChannel().sendTyping().queue(); // Send typing
        MongoGuild db = MongoGuild.get(ctx.getGuild()); // Get database
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
