package com.github.m5rian.myra.utilities.APIs.youtube;

import org.jetbrains.annotations.NotNull;

/**
 * @author Marian
 * <p>
 * This class represents a Youtube channel.
 */
public class YtChannel {
    private final String id;
    private final String channelName;
    private final String avatar;

    /**
     * Create a new {@link YtChannel} object.
     *
     * @param id          The id of the youtube channel
     * @param channelName The name of the youtube channel.
     * @param avatar      The avatar of the youtube channel.
     */
    public YtChannel(@NotNull String id, @NotNull String channelName, String avatar) {
        this.id = id;
        this.channelName = channelName;
        this.avatar = avatar;
    }

    /**
     * @return Returns the channel id.
     */
    public String getId() {
        return this.id;
    }

    /**
     * @return Returns the channel name.
     */
    public String getChannelName() {
        return this.channelName;
    }

    /**
     * @return Returns the channel avatar. Can return null if the parsed avatar in the constructor is null.
     */
    public String getAvatar() {
        if (!this.avatar.startsWith("http")) return "https:" + this.avatar;
        return this.avatar;
    }
}
