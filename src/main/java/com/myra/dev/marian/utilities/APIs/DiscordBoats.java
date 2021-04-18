package com.myra.dev.marian.utilities.APIs;

import com.myra.dev.marian.Config;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.entities.User;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;

public class DiscordBoats {
    private final static DiscordBoats DISCORD_BOATS = new DiscordBoats();

    public static DiscordBoats getInstance() {
        return DISCORD_BOATS;
    }

    public boolean hasVoted(User user) throws IOException {
        // Create request
        final Request request = new Request.Builder()
                .url("https://discord.boats/api/bot/" + Config.myra + "/voted?id=" + user.getId() + "/")
                .build();

        final Response execute = Utilities.HTTP_CLIENT.newCall(request).execute();
        final String response = execute.body().string();
        execute.close();

        final JSONObject json = new JSONObject(response);

        if (json.getBoolean("error")) return false; // User hasn't logged in in discord.boats yet
        return json.getBoolean("voted"); // Return voted state
    }
}
