package com.myra.dev.marian;

import com.myra.dev.marian.listeners.notifications.YoutubeNotification;
import com.myra.dev.marian.utilities.APIs.youtube.YoutubeHttpHandler;

import static spark.Spark.*;

public class WebServer {

    public WebServer() {
        port(Config.WEB_SERVER_PORT); // Set port

        // Youtube feed notifications
        post("/youtube", YoutubeHttpHandler::onYoutubePost); // Youtube feed listener
        get("/youtube", YoutubeHttpHandler::onYoutubeGet); // Youtube subscribe verification

        YoutubeNotification.renewSubscriptions(); // Renew all subscriptions
    }

}
