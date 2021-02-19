package com.myra.dev.marian.listeners.premium;

import com.myra.dev.marian.Myra;
import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.entities.Guild;

import java.awt.*;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class UnicornChange {
    public void change() {
        Utilities.TIMER.scheduleAtFixedRate(() -> {
            final Iterator<Guild> iterator = Myra.shardManager.getGuilds().iterator();
            while (iterator.hasNext()) {
                final Guild guild = iterator.next(); // Get next guild

                final Database db = new Database(guild); // Get database
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
