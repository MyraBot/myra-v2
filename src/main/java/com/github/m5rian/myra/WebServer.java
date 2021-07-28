package com.github.m5rian.myra;

import com.github.m5rian.myra.database.guild.MongoGuild;

import static spark.Spark.*;

public class WebServer {

    public WebServer() {
        port(Config.WEB_SERVER_PORT); // Set port

        // Youtube feed notifications
        //post("/youtube", YoutubeHttpHandler::onYoutubePost); // Youtube feed listener
        //get("/youtube", YoutubeHttpHandler::onYoutubeGet); // Youtube subscribe verification

        // Api
        path("/api", () -> {
            before("/*", (req, res) -> res.header("Access-Control-Allow-Origin", "*"));

            path("/retrieve", () -> {
                get("/guild", (req, res) -> MongoGuild.get(req.headers("guildId")).getDocument().toJson());
            });

            post("/embed", "application/json", Api::onEmbed); // Embed sending
            post("/welcoming", "application/json", Api::onWelcomeSave); // Save leveling settings
            post("/general", "application/json", Api::onGeneralSave); // Save general settings
        });
    }

}
