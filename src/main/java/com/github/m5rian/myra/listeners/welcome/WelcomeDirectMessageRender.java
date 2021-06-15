package com.github.m5rian.myra.listeners.welcome;

import com.github.m5rian.myra.database.guild.MongoGuild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class WelcomeDirectMessageRender {

    public MessageEmbed render(Guild guild, User user) {
        MongoGuild db = MongoGuild.get(guild); // Get database
        // Get variables
        final String welcomeColour = db.getNested("welcome").getString("welcomeColour");
        final String welcomeDirectMessage = db.getNested("welcome").getString("welcomeDirectMessage");
        // Return message embed
        return new EmbedBuilder()
                .setAuthor("welcome", null, guild.getIconUrl())
                .setColor(Color.decode(welcomeColour))
                .setThumbnail(user.getEffectiveAvatarUrl())
                .setDescription(welcomeDirectMessage
                        .replace("{member}", user.getAsMention()) // Member mention
                        .replace("{server}", guild.getName()) // Guild name
                        .replace("{count}", Integer.toString(guild.getMemberCount()))) // Guild member count
                .build();
    }
}
