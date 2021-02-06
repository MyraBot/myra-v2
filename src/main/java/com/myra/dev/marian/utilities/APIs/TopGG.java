package com.myra.dev.marian.utilities.APIs;

import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.entities.User;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;

public class TopGG {
    // Create Instance
    private final static TopGG TOP_GG = new TopGG();

    // Return the Instance
    public static TopGG getInstance() {
        return TOP_GG;
    }

    /**
     * Get the amount of votes for Myra.
     *
     * @return Returns the amount of votes as a String object.
     * @throws IOException
     */
    public String getUpVotes() throws IOException {
        //make get request
        Request channel = new Request.Builder()
                .url("https://top.gg/api/bots/718444709445632122")
                .header("Authorization", Utilities.getUtils().topGgKey)
                .build();
        //execute call
        String channelOutput;
        try (Response channelResponse = Utilities.HTTP_CLIENT.newCall(channel).execute()) {
            channelOutput = channelResponse.body().string();
        }
        //create Json object
        final int votes = new JSONObject(channelOutput).getInt("points");
        // Return votes
        return String.valueOf(votes);
    }

    /**
     * Checks if a user has voted.
     *
     * @param user The user to check.
     * @return Returns a boolean value if the user voted for my bot in the last 12 hours.
     * @throws IOException
     */
    public boolean hasVoted(User user) throws IOException {
        //make get request
        Request channel = new Request.Builder()
                .url("https://top.gg/api/bots/718444709445632122/check?userId=" + user.getId())
                .header("Authorization", Utilities.getUtils().topGgKey)
                .build();
        //execute call
        String channelOutput;
        try (Response channelResponse = Utilities.HTTP_CLIENT.newCall(channel).execute()) {
            channelOutput = channelResponse.body().string();
        }
        //create Json object
        Integer votes = new JSONObject(channelOutput).getInt("voted");
        // Return if user voted
        if (votes != 0) return true;
        else return false;
    }
}