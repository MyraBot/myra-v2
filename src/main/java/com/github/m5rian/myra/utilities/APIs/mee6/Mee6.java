package com.github.m5rian.myra.utilities.APIs.mee6;

import com.github.m5rian.myra.utilities.Utilities;
import net.dv8tion.jda.api.entities.Guild;
import okhttp3.Request;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Mee6 {

    public final Guild guild;
    private final String baseUrl = "https://mee6.xyz/api/plugins/levels/leaderboard/";

    public Mee6(Guild guild) {
        this.guild = guild;
    }

    /**
     * @return Returns a {@link List} containing all {@link Mee6User}s of the server.
     * @throws IOException
     */
    public List<Mee6User> getUsers() {
        List<Mee6User> users = new ArrayList<>(); // List of all users

        try {

            int i = 0;
            while (true) {
                // Create request
                final Request request = new Request.Builder()
                        .url(baseUrl + this.guild.getId() + "?page=" + i) // Url with current page
                        .get()
                        .build();

                final String response = Utilities.HTTP_CLIENT.newCall(request).execute().body().string(); // Execute response
                final JSONObject guildLeveling = new JSONObject(response); // Parse response to JSONObject
                final JSONArray players = guildLeveling.getJSONArray("players"); // Get users
                if (players.length() == 0) break; // Page doesn't have more players

                for (int current = 0; current < players.length(); current++) {
                    final JSONObject data = players.getJSONObject(current); // Get user data from JSONObject
                    users.add(new Mee6User(data)); // Add user to list
                }
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return users;
    }
}
