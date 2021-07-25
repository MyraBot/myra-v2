package com.github.m5rian.myra.utilities.APIs.youtube.deprecated;

import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.exceptions.OkhttpExecuteException;
import com.github.m5rian.myra.utilities.APIs.youtube.deprecated.data.YoutubeChannel;
import com.github.m5rian.myra.utilities.Utilities;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which can execute Youtube requests.
 * <p>
 * For subscribing and unsubscribing to Youtubers look at
 * https://pubsubhubbub.appspot.com/subscribe
 */
public class Youtube {
    private static final String CALLBACK_URL = Config.LOCAL_ADDRESS + ":" + Config.WEB_SERVER_PORT + "/youtube";
    private static final String TOPIC_URL = "https://www.youtube.com/channel/";
    private static final String SUBSCRIBE_URL = "https://pubsubhubbub.appspot.com/subscribe";

    private static final String LIST_URL = "https://www.googleapis.com/youtube/v3/search";
    private static final String CHANNELS_LIST_URL = "https://www.googleapis.com/youtube/v3/channels";

    public static void subscribe(String channelId) {
        final RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("hub.callback", CALLBACK_URL)
                .addFormDataPart("hub.topic", TOPIC_URL + channelId)
                .addFormDataPart("hub.verify", "async")
                .addFormDataPart("hub.mode", "subscribe")
                .build();

        final Request request = new Request.Builder()
                .url(SUBSCRIBE_URL)
                .post(requestBody)
                .build();

        try (Response response = Utilities.HTTP_CLIENT.newCall(request).execute()) {
            System.out.println("Executing a request");
            if (response.isSuccessful()) {
                // Response successful
            }
            else {
                // Subscription failed
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unsubscribe(String channelId) {
        final RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("hub.callback", CALLBACK_URL)
                .addFormDataPart("hub.topic", TOPIC_URL + channelId)
                .addFormDataPart("hub.verify", "async")
                .addFormDataPart("hub.mode", "unsubscribe")
                .build();

        final Request request = new Request.Builder()
                .url(SUBSCRIBE_URL)
                .post(requestBody)
                .build();

        try (Response response = Utilities.HTTP_CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // TODO Return something so you know if the request was successful or not
            } else {
                // TODO Return something so you know if the request was successful or not
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get more detailed information about one or more youtube channels using their id.
     *
     * @param ids The youtube channel ids to get information from.
     * @return Returns a List of {@link YoutubeChannel}s for the given channel ids.
     */
    public static List<YoutubeChannel> getChannels(String... ids) {
        // Create request
        final Request request = new Request.Builder()
                .url(CHANNELS_LIST_URL + "?" +
                        "&part=snippet" +
                        "&id=" + String.join(",", ids) +
                        "&key=" + Config.YOUTUBE_KEY)
                .addHeader("client_id", Config.YOUTUBE_KEY)
                .get()
                .build();

        // Execute response
        try (Response response = Utilities.HTTP_CLIENT.newCall(request).execute()) {
            final List<YoutubeChannel> channels = new ArrayList<>(); // Create list for youtube channels
            final JSONObject json = new JSONObject(response.body().string()); // Convert response to JSONArray
            final JSONArray items = json.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                final JSONObject info = items.getJSONObject(i).getJSONObject("snippet");
                final String id = items.getJSONObject(i).getString("id"); // Get channel id
                final String name = info.getString("title"); // Get channel name
                final String description = info.getString("description"); // Get channel description
                final String avatar = info.getJSONObject("thumbnails").getJSONObject("high").getString("url"); // Get channel avatar

                channels.add(new YoutubeChannel(id, name)); // Add channel to list
            }
            return channels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<YoutubeChannel> searchChannel(String query) throws OkhttpExecuteException {
        final List<YoutubeChannel> channels = new ArrayList<>(); // Create list for videos

        final Request request = new Request.Builder()
                .url(LIST_URL + "?" +
                        "&part=snippet" +
                        "&type=channel" + // Only search for channels
                        "&q=" + URLEncoder.encode(query, StandardCharsets.UTF_8) + // Search for the given keyword
                        "&key=" + Config.YOUTUBE_KEY)
                .addHeader("client_id", Config.YOUTUBE_KEY)
                .get()
                .build();

        try (Response response = Utilities.HTTP_CLIENT.newCall(request).execute()) {
            final JSONObject json = new JSONObject(response.body().string());

            final JSONArray items = json.getJSONArray("items"); // Get the found results
            for (int i = 0; i < items.length(); i++) {
                final JSONObject video = items.getJSONObject(i); // Get next item

                final String channelId = video.getJSONObject("snippet").getString("channelId"); // Get channel id
                final String channelName = video.getJSONObject("snippet").getString("title"); // Get channel name
                channels.add(new YoutubeChannel(channelId, channelName));
            }
        } catch (Exception e) {
            throw new OkhttpExecuteException(e);
        }

        return channels;
    }

}
