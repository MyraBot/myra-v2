package com.github.m5rian.myra.utilities.APIs.youtube.data;

import java.time.ZonedDateTime;

public class YoutubeVideo {
    private final String id;
    private final String title;
    private final ZonedDateTime publishedAt;
    private final String description;
    private final String views;

    public YoutubeVideo(String id, String title, ZonedDateTime publishedAt, String description, String views) {
        this.title = title;
        this.id = id;
        this.publishedAt = publishedAt;
        this.description = description;
        this.views = views;
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getThumbnail() {
        return "https://img.youtube.com/vi/" + this.id + "/mqdefault.jpg";
    }

    public ZonedDateTime getUploadTime() {
        return this.publishedAt;
    }

    public String getDescription() {
        return this.description;
    }

    public String getViews() {
        return this.views;
    }
}
