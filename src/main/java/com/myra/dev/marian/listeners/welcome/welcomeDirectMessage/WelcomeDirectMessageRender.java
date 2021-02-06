package com.myra.dev.marian.listeners.welcome.welcomeDirectMessage;

import com.myra.dev.marian.database.allMethods.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class WelcomeDirectMessageRender {

    public MessageEmbed render(Guild guild, User user) {
        Database db = new Database(guild); // Get database
        // Get variables
        String welcomeColour = db.getNested("welcome").getString("welcomeColour");
        String welcomeDirectMessage = db.getNested("welcome").getString("welcomeDirectMessage");
        // Return message embed
        return new EmbedBuilder()
                .setAuthor("welcome", null, guild.getIconUrl())
                .setColor(Color.decode(welcomeColour))
                .setThumbnail(user.getEffectiveAvatarUrl())
                .setDescription(welcomeDirectMessage
                        .replace("{user}", user.getAsMention())
                        .replace("{server}", guild.getName())
                        .replace("{count}", Integer.toString(guild.getMemberCount())))
                .build();
    }
}
