package com.myra.dev.marian.utilities.APIs.youtube;

import com.myra.dev.marian.Config;
import com.myra.dev.marian.exceptions.OkhttpExecuteException;
import com.myra.dev.marian.utilities.APIs.youtube.data.YoutubeChannel;
import com.myra.dev.marian.utilities.Utilities;
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

public class Youtube {
    private static final String CALLBACK_URL = Config.SERVER_ADDRESS + ":" + Config.WEB_SERVER_PORT + "/youtube";
    private static final String TOPIC_URL = "https://www.youtube.com/xml/feeds/videos.xml?channel_id=";
    private static final String SUBSCRIBE_URL = "https://pubsubhubbub.appspot.com/subscribe";

    private static final String LIST_URL = "https://www.googleapis.com/youtube/v3/search";

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
            if (response.isSuccessful()) {
                // TODO Return something so you know if the request was successful or not
            } else {
                // TODO Return something so you know if the request was successful or not
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

    public static YoutubeChannel getChannel(String id) {

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
