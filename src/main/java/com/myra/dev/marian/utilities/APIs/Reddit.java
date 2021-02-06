package com.myra.dev.marian.utilities.APIs;

import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.util.Random;

public class Reddit {
    public EmbedBuilder getMeme(User author) throws Exception {
        Random random = new Random();
        //subreddits
        String[] subreddits = {
                "memes",
                "meme",
                "dankmemes",
                "hmmm"
        };
        //get a random subreddit
        String subreddit = subreddits[random.nextInt(subreddits.length)];
        //make get request
        Request channel = new Request.Builder()
                .url("https://www.reddit.com/r/" + subreddit + "/top.json")
                .build();
        //execute call
        String channelOutput;
        try (Response channelResponse = Utilities.HTTP_CLIENT.newCall(channel).execute()) {
            channelOutput = channelResponse.body().string();
        }
        //create Json object
        JSONObject data = new JSONObject(channelOutput).getJSONObject("data");
        //get random number
        int randomNumber = random.nextInt(data.getInt("dist"));
        while (data.getJSONArray("children").getJSONObject(randomNumber).getJSONObject("data").getBoolean("over_18")) {
            randomNumber = random.nextInt(data.getInt("dist"));
        }
        //get meme
        JSONObject meme = data.getJSONArray("children").getJSONObject(randomNumber).getJSONObject("data");
        //embed
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(meme.getString("title"), "https://www.reddit.com" + meme.getString("permalink"), author.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setImage(meme.getString("url_overridden_by_dest"))
                .setFooter("\uD83D\uDC4D " + meme.getInt("ups") + " │ \uD83D\uDCAC " + meme.getInt("num_comments"));
        return embed;
    }
}
