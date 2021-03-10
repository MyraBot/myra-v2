package com.myra.dev.marian.utilities.APIs.youTube;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class YouTube {
    private final static YouTube INSTANCE = new YouTube();

    public static YouTube getApi() {
        return INSTANCE;
    }

    public List<Channel> searchChannelByName(String query) throws IOException {
        final String baseUrl = "https://www.youtube.com/results?search_query={query}&sp=EgIQAg%253D%253D";

        final Document jsoup = Jsoup
                .connect(baseUrl.replace("{query}", query))
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .get();

        JSONObject infoJson = null;
        final Iterator<Element> scripts = jsoup.getElementsByTag("script").iterator();
        while (scripts.hasNext()) {
            final Element script = scripts.next(); // Get next script

            if (!script.html().startsWith("var ytInitialData =")) continue;

            final String initializeVariable = "var ytInitialData = "; // Define initialize keywords
            final String jsonString = script.html().substring(initializeVariable.length()); // Remove initialization
            infoJson = new JSONObject(jsonString.substring(0, jsonString.length() - 1)); // Remove ';' at the end and parse it into a JsonObject
        }

        final int results = Integer.parseInt(infoJson.getString("estimatedResults"));

        final List<Channel> channels = new ArrayList<>(); // Create list for all channel objects
        final JSONArray jsonChannels = infoJson.getJSONObject("contents").getJSONObject("twoColumnSearchResultsRenderer").getJSONObject("primaryContents").getJSONObject("sectionListRenderer").getJSONArray("contents").getJSONObject(0).getJSONObject("itemSectionRenderer").getJSONArray("contents"); // Get JSONArray with all channels
        for (int i = 0; i < jsonChannels.length(); i++) {
            final JSONObject channelInfo = jsonChannels.getJSONObject(i).getJSONObject("channelRenderer"); // Get information about current channel

            final String channelAvatar = channelInfo.getJSONObject("thumbnail").getJSONArray("thumbnails").getJSONObject(1).getString("url"); // Get avatar url
            final String channelName = channelInfo.getJSONObject("title").getString("simpleText"); // Get channel name
            final String channelId = channelInfo.getString("channelId"); // Get channel id

            channels.add(new Channel(channelId, channelName, channelAvatar)); // Add channel
        }

        return channels;
    }

    public Channel getChannel(String channelId) throws IOException {
        final String baseUrl = "https://www.youtube.com/channel/{channelId}/about";

        final Document json = Jsoup
                .connect(baseUrl.replace("{channelId}", channelId))
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .get();

        JSONObject infoJson = null;
        final Iterator<Element> scripts = json.getElementsByTag("script").iterator();
        while (scripts.hasNext()) {
            final Element script = scripts.next(); // Get next script

            if (!script.html().startsWith("var ytInitialData =")) continue;

            final String initializeVariable = "var ytInitialData = "; // Define initialize keywords
            final String jsonString = script.html().substring(initializeVariable.length()); // Remove initialization
            infoJson = new JSONObject(jsonString.substring(0, jsonString.length() - 1)); // Remove ';' at the end and parse it into a JsonObject
        }

        final JSONObject channelInfo = infoJson.getJSONObject("metadata").getJSONObject("channelMetadataRenderer");
        final String channelName = channelInfo.getString("title"); // Get channel name
        final String channelDescription = channelInfo.getString("description"); // Get channel description
        final String channelKeywordsRaw = channelInfo.getString("keywords"); // Get raw keywords
        final List<String> channelKeywords = new ArrayList<>(); // Create a list for all keywords
        for (String s : channelKeywordsRaw.split("\"")) { // Loop through keywords raw list
            if (s.equals("  ")) continue; // s isn't a keyword
            channelKeywords.add(s); // Add keyword to list
        }
        final String channelAvatar = channelInfo.getJSONObject("avatar").getJSONArray("thumbnails").getJSONObject(0).getString("url"); // Get avatar url
        final boolean isFamilySafe = channelInfo.getBoolean("isFamilySafe"); // Is the channel family save?
        final String channelVanityUrl = channelInfo.getString("vanityChannelUrl");

        return new Channel(channelId, channelName, channelAvatar);
    }

    public Videos getLatestVideos(String channelId) throws IOException {
        final String baseUrlXml = "https://www.youtube.com/feeds/videos.xml?channel_id=";
        final String baseUrl = "https://www.youtube.com/channel/";


        final Document xml = Jsoup
                .connect(baseUrlXml + channelId)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .get();
        // Get xml content
        final Element content = xml.getElementsByTag("feed").get(0);
        final Elements videosInfoXml = content.getElementsByTag("entry");


        final Document json = Jsoup
                .connect(baseUrl + channelId)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .get();
        // Create iterator for all script tags
        final Iterator<Element> scripts = json.getElementsByTag("script").iterator();
        JSONObject infoJson = null;

        while (scripts.hasNext()) {
            final Element script = scripts.next(); // Get next script

            if (!script.html().startsWith("var ytInitialData =")) continue;

            final String initializeVariable = "var ytInitialData = "; // Define initialize keywords
            final String jsonString = script.html().substring(initializeVariable.length()); // Remove initialization
            infoJson = new JSONObject(jsonString.substring(0, jsonString.length() - 1)); // Remove ';' at the end and parse it into a JsonObject
        }

        if (infoJson == null) System.out.println(json);
        // Get youtube channel information
        final JSONObject channelInfo = infoJson.getJSONObject("metadata").getJSONObject("channelMetadataRenderer");
        final String channelName = channelInfo.getString("title"); // Get username
        final String avatar = channelInfo.getJSONObject("avatar").getJSONArray("thumbnails").getJSONObject(0).getString("url"); //  Get avatar url

        final Channel channelClass = new Channel(channelId, channelName, avatar); // Create channel Object


        final List<Video> videos = new ArrayList(); // Create new array list for all videos
        for (int i = 0; i < videosInfoXml.size(); i++) {
            final Element videoInfoXml = videosInfoXml.get(i); // Get video

            final String videoId = videoInfoXml.getElementsByTag("yt:videoId").get(0).html(); // Get video id
            final String videoTitle = videoInfoXml.getElementsByTag("title").get(0).html(); // Get title of video
            final String videoPublishedAtRaw = videoInfoXml.getElementsByTag("published").get(0).html(); // Get upload time
            final ZonedDateTime videoPublishedAt = ZonedDateTime.parse(videoPublishedAtRaw); // Parse upload time to ZoneDateTime
            final String videoDescription = videoInfoXml.getElementsByTag("media:description").get(0).html(); // Get description of video
            final String videoViews = videoInfoXml.getElementsByTag("media:community").get(0).getElementsByTag("media:statistics").get(0).attr("views"); // Get views


            final Video videoClass = new Video(videoId, videoTitle, videoPublishedAt, videoDescription, videoViews); // Create new video object
            videos.add(videoClass); // Add video to all videos
        }

        return new Videos(channelClass, videos);
    }

    public void getVideo(String videoId) throws IOException {
        final String baseUrl = "https://www.youtube.com/watch?v=";

        final Document web = Jsoup
                .connect(baseUrl + videoId)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .get();

        // Create iterator for all script tags
        final Iterator<Element> scripts = web.getElementsByTag("script").iterator();
        JSONObject info = null;

        while (scripts.hasNext()) {
            final Element script = scripts.next(); // Get next script

            if (!script.html().startsWith("var ytInitialData =")) continue;

            final String initializeVariable = "var ytInitialData = "; // Define initialize keywords
            final String jsonString = script.html().substring(initializeVariable.length()); // Remove initialization
            info = new JSONObject(jsonString.substring(0, jsonString.length() - 1)); // Remove ';' at the end and parse it into a JsonObject
        }

        final JSONObject videoInfo = info.getJSONObject("contents").getJSONObject("twoColumnWatchNextResults").getJSONObject("results").getJSONObject("results").getJSONArray("contents").getJSONObject(1).getJSONObject("videoSecondaryInfoRenderer"); // Get info about video

        final JSONObject ownerInfo = videoInfo.getJSONObject("owner").getJSONObject("videoOwnerRenderer"); // Get info for owner
        final String ownerName = ownerInfo.getJSONObject("title").getJSONArray("runs").getJSONObject(0).getString("text"); // Get name of owner
        final String ownerId = ownerInfo.getJSONObject("title").getJSONArray("runs").getJSONObject(0).getJSONObject("navigationEndpoint").getJSONObject("browseEndpoint").getString("browseId"); // Get id of owner
        final String ownerAvatar = ownerInfo.getJSONObject("thumbnail").getJSONArray("thumbnails").getJSONObject(2).getString("url"); // Get avatar of owner


        System.out.println(info);
    }
}