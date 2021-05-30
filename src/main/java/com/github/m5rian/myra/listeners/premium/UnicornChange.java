package com.github.m5rian.myra.listeners.premium;

import com.github.m5rian.myra.DiscordBot;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.database.guild.MongoGuild;
import net.dv8tion.jda.api.entities.Guild;

import java.awt.*;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class UnicornChange {
    public void change() {
        Utilities.TIMER.scheduleAtFixedRate(() -> {
            final Iterator<Guild> iterator = DiscordBot.shardManager.getGuilds().iterator();
            while (iterator.hasNext()) {
                final Guild guild = iterator.next(); // Get next guild

                final MongoGuild db = new MongoGuild(guild); // Get database
                if (!db.getBoolean("premium")) continue; // Check for premium
                // Get unicorn role
                final Long unicorn = db.getLong("unicorn"); // Get unicorn role
                if (unicorn == 0) continue;
                // Get high saturated colour
                Random random = new Random();
                final float hue = random.nextFloat();
                final float saturation = 0.5f; //1.0 for brilliant, 0.0 for dull
                final float brightness = 1.0f; //1.0 for brighter, 0.0 for black
                Color colour = Color.getHSBColor(hue, saturation, brightness);
                // Update colour
                guild.getRoleById(unicorn).getManager().setColor(colour).queue();
            }
        }, 60, 60, TimeUnit.MINUTES);
    }
}
