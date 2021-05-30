package com.github.m5rian.myra.utilities.APIs.youtube;

import com.github.m5rian.myra.WebServer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import spark.Request;
import spark.Response;

/**
 * @author Marian
 * <p>
 * This class handles all http requests of youtube.
 * The calls are received from the {@link WebServer}.
 * <p>
 * Good reference:
 * <ul>
 *     <li>https://www.codeproject.com/Tips/1229912/Push-Notification-PubSubHubBub-from-Youtube-to-Csh</li>
 *     <li>https://github.com/youtube/api-samples/issues/177</li>
 * </ul>
 */
public class YoutubeHttpHandler {

    /**
     * We need to return the hub.challenge to verify youtube that everything on our side works.
     *
     * @param req A {@link Request} done by youtube.
     * @param res A {@link Response} done by us.
     * @return Returns a {@link Response} containing the hub.challenge request parameter
     */
    public static Object onYoutubeGet(spark.Request req, spark.Response res) {
        return req.queryParams("hub.challenge"); // Return hub.challenge parameter
    }

    public static Object onYoutubePost(Request req, Response res) {
        final String xmlRaw = req.body();
        final Document xml = Jsoup.parse(xmlRaw);
        new YoutubeFeedHandler(xml);

        // Return status code to tell the youtube server that the request was successfully received
        res.status(204);
        return "";
    }
}
