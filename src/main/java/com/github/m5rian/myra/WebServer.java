package com.github.m5rian.myra;

import com.github.m5rian.myra.listeners.notifications.YoutubeNotification;
import com.github.m5rian.myra.utilities.APIs.youtube.YoutubeHttpHandler;

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