package com.myra.dev.marian.listeners.welcome;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.listeners.welcome.WelcomeImage.WelcomeImageRender;
import com.myra.dev.marian.listeners.welcome.welcomeDirectMessage.WelcomeDirectMessageRender;
import com.myra.dev.marian.listeners.welcome.welcomeEmbed.WelcomeEmbedRender;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

import java.io.InputStream;

public class WelcomeListener {

    public void welcome(GuildMemberJoinEvent event) throws Exception {
        Database db = new Database(event.getGuild()); // Get database

        // Get welcome channel
        if (db.getNested("welcome").getString("welcomeChannel").equals("not set")) return; // Return if no welcome channel is set
        final TextChannel channel = event.getGuild().getTextChannelById(db.getNested("welcome").getString("welcomeChannel"));

        // Welcome direct message is enabled
        if (db.getListenerManager().check("welcomeDirectMessage")) {
            final MessageEmbed privateMessage = new WelcomeDirectMessageRender().render(event.getGuild(), event.getUser()); // Get direct message
            event.getUser().openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage(privateMessage).queue();
            });
        }
        // Welcome embed is enabled
        if (db.getListenerManager().check("welcomeEmbed")) {
            final MessageEmbed embed  = new WelcomeEmbedRender().render(event.getGuild(), event.getUser()); // Get embed message
            channel.sendMessage(embed).queue(); // Send embed
        }
        // Welcome Image is enabled
        if (db.getListenerManager().check("welcomeImage")) {
            final InputStream welcomeImage = new WelcomeImageRender().render(event.getGuild(), event.getUser()); // Get welcome image
            channel.sendFile(welcomeImage, event.getUser().getName().toLowerCase() + "_welcome.png").queue();
        }
    }
}
