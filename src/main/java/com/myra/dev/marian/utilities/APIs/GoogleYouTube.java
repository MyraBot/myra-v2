package com.myra.dev.marian.utilities.APIs;

import com.myra.dev.marian.utilities.Utilities;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleYouTube {
    private final static GoogleYouTube YOU_TUBE = new GoogleYouTube();

    public static GoogleYouTube getInstance() {
        return YOU_TUBE;
    }


    // OkHttpRequest methods

    private final static OkHttpClient client = Utilities.getUtils().HTTP_CLIENT;

    public List<JSONObject> searchForVideo(String search) throws IOException {
        // Create request
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/youtube/v3/search?" +
                        "&part=id" +
                        "&part=snippet" +
                        "&type=video" + // Only search for videos
                        "&q=" + search + // Search for the given keyword
                        "&key=" + Utilities.getUtils().youTubeKey // Provide my youtube key
                )
                .build();
        //Execute call
        String channelOutput = client.newCall(request).execute().body().string();

        final List<JSONObject> videos = new ArrayList<>(); // Create list
        final JSONArray items = new JSONObject(channelOutput).getJSONArray("items"); // Create Json object

        // Add all videos to the list
        for (Object videoInfo : items) {
            final JSONObject video = (JSONObject) videoInfo; // Parse Object to JSONObject
            videos.add(video); // Add video to list
        }

        return videos;
    }

    public JSONObject getChannelByName(String name) throws IOException {
        // Create channel get request
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/youtube/v3/search?" +
                        "part=snippet" +
                        "&type=channel" +
                        //"&order=rating" +
                        "&q=" + name +
                        "&maxResults=1" +
                        "&key=" + Utilities.getUtils().youTubeKey
                )
                .build();
        //Execute call
        String channelOutput;
        try (Response channelResponse = client.newCall(request).execute()) {
            channelOutput = channelResponse.body().string();
        }

        final JSONArray items = new JSONObject(channelOutput).getJSONArray("items"); // Create Json object
        final JSONObject channel = items.getJSONObject(0);
        final JSONObject channelInformation = channel.getJSONObject("snippet");
        return channelInformation;
    }

    public JSONObject getChannelByUrl(String url) throws IOException {
        // Channel has no custom url
        if (url.startsWith("https://www.youtube.com/channel/")) {
            url = url.replace("?view_as=subscriber", ""); // Remove 'view_as=subscriber' tag
            final String id = url.split("/")[4]; // Get channel id
            // Create channel get request
            Request request = new Request.Builder()
                    .url("https://www.googleapis.com/youtube/v3/search?" +
                            "part=snippet" +
                            "&type=channel" +
                            "&channelId=" + id +
                            "&maxResults=1" +
                            "&key=" + Utilities.getUtils().youTubeKey
                    )
                    .build();
            // Execute call
            String channelOutput;
            try (Response channelResponse = client.newCall(request).execute()) {
                channelOutput = channelResponse.body().string();
            }

            final JSONArray items = new JSONObject(channelOutput).getJSONArray("items"); // Create Json object
            final JSONObject channel = items.getJSONObject(0); // Get first channel
            final JSONObject channelInformation = channel.getJSONObject("snippet");
            return channelInformation;
        }

        // Channel has custom url
        if (url.startsWith("https://www.youtube.com/user/")) {
            final String name = url.split("/")[4]; // Get channel id
            // Create channel get request
            Request request = new Request.Builder()
                    .url("https://www.googleapis.com/youtube/v3/search?" +
                            "part=snippet" +
                            "&type=channel" +
                            "&q=" + name +
                            "&maxResults=1" +
                            "&key=" + Utilities.getUtils().youTubeKey
                    )
                    .build();

            //Execute call
            String channelOutput;
            try (Response channelResponse = client.newCall(request).execute()) {
                channelOutput = channelResponse.body().string();
            }

            final JSONArray items = new JSONObject(channelOutput).getJSONArray("items"); // Create Json object
            final JSONObject channel = items.getJSONObject(0); // Get first channel
            final JSONObject channelInformation = channel.getJSONObject("snippet");
            return channelInformation;
        }

        return null; // Error
    }

    public JSONObject getChannelById(String id) throws IOException {
        // Create channel get request
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/youtube/v3/search?" +
                        "part=snippet" +
                        "&type=channel" +
                        "&channelId=" + id +
                        "&maxResults=1" +
                        "&key=" + Utilities.getUtils().youTubeKey
                )
                .build();
        //Execute call
        String channelOutput;
        try (Response channelResponse = client.newCall(request).execute()) {
            channelOutput = channelResponse.body().string();
        }

        final JSONArray items = new JSONObject(channelOutput).getJSONArray("items"); // Create Json object
        final JSONObject channel = items.getJSONObject(0);
        final JSONObject channelInformation = channel.getJSONObject("snippet");
        return channelInformation;
    }

    public List<JSONObject> getLatestVideos(String channelId) throws IOException {
        // Create video get request
        Request request = new Request.Builder()
                .url(
                        "https://www.googleapis.com/youtube/v3/search?" +
                                "part=snippet" +
                                "&type=video" +
                                "&channelId=" + channelId +
                                "&order=date" +
                                "&key=" + Utilities.getUtils().youTubeKey
                ) // Will return by default 5 videos
                .build();
        // Execute call
        String channelOutput;
        try (Response channelResponse = client.newCall(request).execute()) {
            channelOutput = channelResponse.body().string();
        }

        if (new JSONObject(channelOutput).isNull("items")) {
            System.out.println(channelOutput);
        }
        final JSONArray items = new JSONObject(channelOutput).getJSONArray("items"); // Create JSON object
        List<JSONObject> videos = new ArrayList<>(); // Create a list for all ids
        for (Object videoObject : items) { // Loop through every video
            final JSONObject video = (JSONObject) videoObject; // Pass Object to JSON object
            //final String id = video.getJSONObject("id").getString("videoId"); // Get video id
            videos.add(video); // Add video id to list
        }

        return videos; // Return the list
    }

    public JSONObject getVideoById(String id) throws IOException {
        // Create video get request
        Request request = new Request.Builder()
                .url(
                        "https://www.googleapis.com/youtube/v3/videos?" +
                                "part=snippet" +
                                "&id=" + id +
                                "&maxResults=1" +
                                "&key=" + Utilities.getUtils().youTubeKey
                ) // Will return by default 5 videos
                .build();
        // Execute call
        String channelOutput;
        try (Response channelResponse = client.newCall(request).execute()) {
            channelOutput = channelResponse.body().string();
        }

        final JSONArray items = new JSONObject(channelOutput).getJSONArray("items"); // Create JSON object
        final JSONObject video = items.getJSONObject(0).getJSONObject("snippet"); // Get first video information

        return video; // Return video information
    }
}
/*    private YouTube youtube;

    private YouTube getYouTube() {
        if (youtube == null) {
            try {
                youtube = new com.google.api.services.youtube.YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
                        request -> {
                        })
                        .setApplicationName("Myra bot")
                        .setYouTubeRequestInitializer(new YouTubeRequestInitializer(Utilities.getUtils().youTubeKey))
                        .build();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return youtube;
    }

    public List<SearchResult> search(String video) throws IOException, GeneralSecurityException {
        com.google.api.services.youtube.YouTube.Search.List search = getYouTube().search()
                .list("id,snippet")
                //Search for keyword
                .setQ(video)
                // Return only videos
                .setType("video")
                // Number of returned videos (maximum is 50)
                .setMaxResults(5L)
                .setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
        // Save results in variable
        return search.execute().getItems();
    }


    public SearchResultSnippet getChannelByName(String name) throws IOException {
        // Create a request
        com.google.api.services.youtube.YouTube.Search.List searchedChannel = getYouTube().search()
                .list("snippet")
                .setType("channel")
                .setQ(name)
                .setMaxResults(1L);
        return searchedChannel.execute().getItems().get(0).getSnippet(); // Return the information of the first result
    }

    public SearchResultSnippet getChannelByUrl(String url) throws IOException {
        // Channel has no custom url
        if (url.startsWith("https://www.youtube.com/channel/")) {
            url = url.replace("?view_as=subscriber", ""); // Remove 'view_as=subscriber' tag
            final String id = url.split("/")[4]; // Get channel id
            // Create a request
            com.google.api.services.youtube.YouTube.Search.List searchedChannel = getYouTube().search()
                    .list("snippet")
                    .setType("channel")
                    .setChannelId(id)
                    .setMaxResults(1L);
            return searchedChannel.execute().getItems().get(0).getSnippet(); // Return the information of the first result
        }

        // Channel has custom url
        if (url.startsWith("https://www.youtube.com/user/")) {
            final String name = url.split("/")[4]; // Get channel id
            // Create a request
            YouTube.Search.List searchedChannel = getYouTube().search()
                    .list("snippet")
                    .setType("channel")
                    .setQ(name)
                    .setMaxResults(1L);
            return searchedChannel.execute().getItems().get(0).getSnippet(); // Return the information of the first result
        }

        return null; // Error
    }

    public SearchResultSnippet getChannelById(String id) throws IOException {
        // Create a request
        YouTube.Search.List searchedChannel = getYouTube().search()
                .list("snippet")
                .setType("channel")
                .setChannelId(id)
                .setMaxResults(1L);
        return searchedChannel.execute().getItems().get(0).getSnippet(); // Return the information of the first result
    }

    public List<SearchResult> getLatestVideos(String channelId) throws IOException {
        // Create a request
        YouTube.Search.List search = getYouTube().search() // Will return by default 5 videos
                .list("snippet")
                .setType("video")
                .setChannelId(channelId)
                .setOrder("date");

        return search.execute().getItems(); // Return the information of the last videos
    }


    public void getChannelByNameTEST(String name) throws IOException {
        final URL url = new URL("https://www.googleapis.com/youtube/v3/search?" +
                "part=snippet" +
                "&type=channel" +
                "&q=" + name +
                "&maxResults=1" +
                "&key=" + Utilities.getUtils().youTubeKey
        );
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Connection", "keep-alive");

        String s = connection.getInputStream().toString();
        System.out.println(s);
    }*/
