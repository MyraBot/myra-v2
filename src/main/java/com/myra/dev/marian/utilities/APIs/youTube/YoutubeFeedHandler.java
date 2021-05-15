package com.myra.dev.marian.utilities.APIs.youtube;

import com.myra.dev.marian.listeners.notifications.YoutubeNotification;
import com.myra.dev.marian.utilities.APIs.youtube.data.YoutubeChannel;
import com.myra.dev.marian.utilities.APIs.youtube.data.YoutubeVideo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.ZonedDateTime;

public class YoutubeFeedHandler {

    private final String channelId;
    private final String channelName;
    private final String videoId;
    private final ZonedDateTime published;


    public YoutubeFeedHandler(Document xml) {
        this.channelId = xml.getElementsByTag("feed").get(0).getElementsByTag("yt:channelId").get(0).text(); // Channel id
        this.channelName = xml.getElementsByTag("feed").get(0).getElementsByTag("author").get(0).getElementsByTag("name").get(0).text(); // Channel name
        this.videoId = xml.getElementsByTag("feed").get(0).getElementsByTag("yt:videoId").get(0).text(); // Video id
        this.published = ZonedDateTime.parse(xml.getElementsByTag("feed").get(0).getElementsByTag("published").get(0).text()); // Parse upload time to ZoneDateTime

        YoutubeNotification.onVideoUpload(getChannel(), getVideo());
    }

    /**
     * @return Returns all important information as a {@link YoutubeVideo}.
     */
    private YoutubeVideo getVideo() {
        final String baseUrlXml = "https://www.youtube.com/feeds/videos.xml?channel_id="; // Base request url

        Request request = new Request.Builder()
                .url(baseUrlXml + channelId)
                .get()
                .build();
        try (Response response = new OkHttpClient().newCall(request).execute()) {
            final Document xml = Jsoup.parse(response.body().string()); // Get response

            final Element content = xml.getElementsByTag("feed").get(0); // Get content
            final Elements videos = content.getElementsByTag("entry"); // Get videos information
            for (Element video : videos) { // Search right video
                final String videoId = video.getElementsByTag("yt:videoId").get(0).text(); // Get video id
                if (videoId.equals(this.videoId)) { // Right video
                    final String title = video.getElementsByTag("title").get(0).text(); // Get video title

                    final Element mediaGroup = video.getElementsByTag("media:group").get(0); // Get tag where more detailed information is stored
                    final String thumbnail = mediaGroup.getElementsByTag("media:thumbnail").get(0).attr("url"); // Get video thumbnail url
                    final String description = mediaGroup.getElementsByTag("media:description").get(0).text(); // Get description
                    final String views = mediaGroup.getElementsByTag("media:community").get(0).getElementsByTag("media:statistics").get(0).attr("views"); // Get video view count

                    return new YoutubeVideo(this.videoId, title, this.published, description, views); //  Create video object
                }
            }
        }
        // There was an issue while making the GET request
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private YoutubeChannel getChannel() {
        return new YoutubeChannel(this.channelId, this.channelName);
    }

}
