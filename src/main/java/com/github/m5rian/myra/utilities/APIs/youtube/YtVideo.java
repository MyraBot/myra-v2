package com.github.m5rian.myra.utilities.APIs.youtube;

import java.time.ZonedDateTime;

public class YtVideo {
    private final String id;
    private final String title;
    private final ZonedDateTime publishedAt;
    private final String description;
    private final String views;

    public YtVideo(String id, String title, ZonedDateTime publishedAt, String description, String views) {
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
        final String baseUrl = "https://img.youtube.com/vi/{videoId}/mqdefault.jpg";
        return baseUrl.replace("{videoId}", this.id);
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
