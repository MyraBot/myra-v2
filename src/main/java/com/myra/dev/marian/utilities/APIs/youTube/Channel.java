package com.myra.dev.marian.utilities.APIs.youTube;

public class Channel {
    private final String id;
    private final String channelName;
    private final String avatar;

    public Channel(String id, String channelName, String avatar) {
        this.id = id;
        this.channelName = channelName;
        this.avatar = avatar;
    }

    public String getId() {
        return this.id;
    }

    public String getChannelName() {
        return this.channelName;
    }

    public String getAvatar() {
        if (!this.avatar.startsWith("http")) return "https:" + this.avatar;
        return this.avatar;
    }
}
