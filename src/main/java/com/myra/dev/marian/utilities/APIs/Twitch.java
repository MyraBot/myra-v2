package com.myra.dev.marian.utilities.APIs;


import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.events.ReadyEvent;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;


public class Twitch {
    public static String accessToken;

    // Get access token
    public void jdaReady() throws IOException {
        //form parameters
        RequestBody body = new FormBody.Builder()
                .add("scope", "channel_read")
                .add("client_id", Utilities.getUtils().twitchClientId)
                .add("client_secret", Utilities.getUtils().twitchClientSecret)
                .add("grant_type", "client_credentials")
                .build();
        //build request
        Request request = new Request.Builder()
                .url("https://id.twitch.tv/oauth2/token")
                .post(body)
                .build();
        //make request
        try (Response response = Utilities.HTTP_CLIENT.newCall(request).execute()) {
            //return access token
            String output = response.body().string();
            JSONObject obj = new JSONObject(output);
            String accessToken = obj.getString("access_token");
            Twitch.accessToken = accessToken;
        }
    }

    //get game
    public String getGame(String gameId) throws IOException {
        //search channel request
        Request game = new Request.Builder()
                .addHeader("client-id", Utilities.getUtils().twitchClientId)
                .addHeader("Authorization", "Bearer " + Twitch.accessToken)
                .url("https://api.twitch.tv/helix/games?id=" + gameId)
                .build();
        //make request
        try (Response response = Utilities.HTTP_CLIENT.newCall(game).execute()) {
            //return access token
            String output = response.body().string();
            JSONArray games = new JSONObject(output).getJSONArray("data");
            if (games.isNull(0)) return null;
            else return games.getJSONObject(0).getString("name");
        }
    }

    // Get user
    public JSONObject getChannel(String name) throws Exception {
        //search channel request
        Request channel = new Request.Builder()
                .addHeader("client-id", Utilities.getUtils().twitchClientId)
                .addHeader("Authorization", "Bearer " + Twitch.accessToken)
                .url("https://api.twitch.tv/helix/users?login=" + name.toLowerCase())
                .build();
        //execute call
        String channelOutput;
        try (Response channelResponse = Utilities.HTTP_CLIENT.newCall(channel).execute()) {
            channelOutput = channelResponse.body().string();
        }

        JSONObject jsonChannel = new JSONObject(channelOutput); // Create Json object

        // Error
        if (jsonChannel.has("errors") || !jsonChannel.has("data")) {
            System.out.println(name);
            System.out.println("Error accrued!!!");
            System.out.println(jsonChannel);
            return null;
        }

        // If no channel found
        if (jsonChannel.getJSONArray("data").length() == 0) return null;

        JSONObject channelData = jsonChannel.getJSONArray("data").getJSONObject(0);

        String user = channelData.getString("display_name"); // Get display name
        String profilePicture = channelData.getString("profile_image_url"); // Get profile picture
        return new JSONObject().put("user", user).put("profilePicture", profilePicture);
    }

    // Get stream
    public JSONObject getStream(String channelName) throws IOException {
        // Create channel http get request
        Request channel = new Request.Builder()
                .addHeader("client-id", Utilities.getUtils().twitchClientId)
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(String.format("https://api.twitch.tv/helix/streams?user_login=%s&first=1", channelName))
                .build();
        // Execute request
        String channelOutput;
        try (Response channelResponse = Utilities.HTTP_CLIENT.newCall(channel).execute()) {
            channelOutput = channelResponse.body().string();
        }

        JSONObject stream = new JSONObject(channelOutput); // Create Json object

        if (stream.getJSONArray("data").length() == 0) return null; // No channel found
        return stream.getJSONArray("data").getJSONObject(0); // Return channel information
    }
}
