package com.myra.dev.marian.utilities.APIs.youtube.data;

public class YoutubeChannel {
    private final String id;
    private final String name;

    public YoutubeChannel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
