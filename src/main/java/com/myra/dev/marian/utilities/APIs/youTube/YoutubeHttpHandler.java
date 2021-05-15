package com.myra.dev.marian.utilities.APIs.youtube;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import spark.Request;
import spark.Response;

/**
 * @author Marian
 * <p>
 * This class handles all http requests of youtube.
 * The calls are received from the {@link com.myra.dev.marian.WebServer}.
 */
public class YoutubeHttpHandler {

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
