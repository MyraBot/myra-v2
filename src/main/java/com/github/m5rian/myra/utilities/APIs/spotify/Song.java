package com.github.m5rian.myra.utilities.APIs.spotify;

import javax.annotation.Nullable;
import java.util.List;

public class Song {
    private final String name;
    private final String id;
    private final String image;
    private final String url;
    private final String preview;
    private final List<User> artists;
    private final boolean isExplicit;

    public Song(String name, String id, String image, String url, String preview, List<User> artists, boolean isExplicit) {
        this.name = name;
        this.id = id;
        this.image = image;
        this.url = url;
        this.preview = preview;
        this.artists = artists;
        this.isExplicit = isExplicit;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }

    @Nullable
    public String getPreviewAudio() {
        return preview;
    }

    public List<User> getArtists() {
        return artists;
    }

    public boolean isExplicit() {
        return isExplicit;
    }
}
