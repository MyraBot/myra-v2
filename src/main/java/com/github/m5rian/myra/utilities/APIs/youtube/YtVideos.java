package com.github.m5rian.myra.utilities.APIs.youtube;

import java.util.List;

public class YtVideos {
    private final YtChannel channel;
    private final List<YtVideo> videos;

    public YtVideos(YtChannel channel, List<YtVideo> videos) {
        this.channel = channel;
        this.videos = videos;
    }

    public YtChannel getChannel() {
        return channel;
    }

    public List<YtVideo> getVideos() {
        return videos;
    }
}
