package com.myra.dev.marian.utilities.APIs.youTube;

import java.util.List;

public class Videos {
    private final Channel channel;
    private final List<Video> videos;

    public Videos(Channel channel, List<Video> videos) {
        this.channel = channel;
        this.videos = videos;
    }

    public Channel getChannel() {
        return channel;
    }

    public List<Video> getVideos() {
        return videos;
    }
}
