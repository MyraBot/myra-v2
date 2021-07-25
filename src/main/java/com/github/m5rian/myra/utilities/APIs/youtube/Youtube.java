package com.github.m5rian.myra.utilities.APIs.youtube;

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

public class Youtube {

    public static List<YtChannel> searchChannelByName(String query) throws IOException {
        final String baseUrl = "https://www.youtube.com/results?search_query={query}&sp=EgIQAg%253D%253D";

        Document jsoup = Jsoup
                .connect(baseUrl.replace("{query}", query))
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .get();
        // Run until data is provided (When data is not provided it asks for cookies)
        while (!jsoup.html().contains("var ytInitialData =")) {
            jsoup = Jsoup
                    .connect(baseUrl.replace("{query}", query))
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .get();
        }

        JSONObject infoJson = null;
        final Iterator<Element> scripts = jsoup.getElementsByTag("script").iterator();
        while (scripts.hasNext()) {
            final Element script = scripts.next(); // Get next script

            if (!script.html().startsWith("var ytInitialData =")) continue;

            final String initializeVariable = "var ytInitialData = "; // Define initialize keywords
            final String jsonString = script.html().substring(initializeVariable.length()); // Remove initialization
            infoJson = new JSONObject(jsonString.substring(0, jsonString.length() - 1)); // Remove ';' at the end and parse it into a JsonObject
            break;
        }

        final int results = Integer.parseInt(infoJson.getString("estimatedResults"));

        final List<YtChannel> channels = new ArrayList<>(); // Create list for all channel objects
        final JSONArray jsonChannels = infoJson.getJSONObject("contents").getJSONObject("twoColumnSearchResultsRenderer").getJSONObject("primaryContents").getJSONObject("sectionListRenderer").getJSONArray("contents").getJSONObject(0).getJSONObject("itemSectionRenderer").getJSONArray("contents"); // Get JSONArray with all channels
        for (int i = 0; i < jsonChannels.length(); i++) {
            final JSONObject channelInfo = jsonChannels.getJSONObject(i).getJSONObject("channelRenderer"); // Get information about current channel

            final String channelAvatar = channelInfo.getJSONObject("thumbnail").getJSONArray("thumbnails").getJSONObject(1).getString("url"); // Get avatar url
            final String channelName = channelInfo.getJSONObject("title").getString("simpleText"); // Get channel name
            final String channelId = channelInfo.getString("channelId"); // Get channel id

            channels.add(new YtChannel(channelId, channelName, channelAvatar)); // Add channel
        }

        return channels;
    }

    public static YtChannel getChannel(String channelId) {
        final String baseUrl = "https://www.youtube.com/channel/{channelId}/about";

        JSONObject infoJson = null;
        // Run as long as infoJson is not null
        while (infoJson == null) {
            try {

                final Document jsoup = Jsoup
                        .connect(baseUrl.replace("{channelId}", channelId))
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .get();

                final Iterator<Element> scripts = jsoup.getElementsByTag("script").iterator();
                while (scripts.hasNext()) {
                    final Element script = scripts.next(); // Get next script

                    if (!script.html().startsWith("var ytInitialData =")) continue;

                    final String initializeVariable = "var ytInitialData = "; // Define initialize keywords
                    final String jsonString = script.html().substring(initializeVariable.length()); // Remove initialization
                    infoJson = new JSONObject(jsonString.substring(0, jsonString.length() - 1)); // Remove ';' at the end and parse it into a JsonObject
                }

            } catch (IOException e){
                e.printStackTrace();
            }
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

        return new YtChannel(channelId, channelName, channelAvatar);
    }

    public static YtVideos getLatestVideos(String channelId) throws IOException {
        final String baseUrlXml = "https://www.youtube.com/feeds/videos.xml?channel_id="; // Base request url

        final Document xml = Jsoup
                .connect(baseUrlXml + channelId)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .get();

        final Element content = xml.getElementsByTag("feed").get(0); // Get content

        final String name = content.getElementsByTag("author").get(0).getElementsByTag("name").get(0).text(); // Get channel name
        final YtChannel channel = new YtChannel(channelId, name, null); // Create channel object

        List<YtVideo> videos = new ArrayList<>();
        final Elements videosInfo = content.getElementsByTag("entry"); // Get videos information

        videosInfo.forEach(videoInfo -> {
            final String id = videoInfo.getElementsByTag("yt:videoId").get(0).text(); // Get video id
            final String title = videoInfo.getElementsByTag("title").get(0).text(); // Get video title
            final String publishedAtRaw = videoInfo.getElementsByTag("published").get(0).text(); // Get upload time
            final ZonedDateTime publishedAt = ZonedDateTime.parse(publishedAtRaw); // Parse upload time to ZoneDateTime

            final Element mediaGroup = videoInfo.getElementsByTag("media:group").get(0); // Get tag where more detailed information is stored
            final String thumbnail = mediaGroup.getElementsByTag("media:thumbnail").get(0).attr("url"); // Get video thumbnail url
            final String description = mediaGroup.getElementsByTag("media:description").get(0).text(); // Get description
            final String views = mediaGroup.getElementsByTag("media:community").get(0).getElementsByTag("media:statistics").get(0).attr("views"); // Get video view count

            final YtVideo video = new YtVideo(id, title, publishedAt, description, views); //  Create video object
            videos.add(video); // Add video
        });

        return new YtVideos(channel, videos);
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
    }
}
