package com.myra.dev.marian.utilities.APIs.spotify;

import java.util.List;

public class Playlist {
    private final String name;
    private final String url;
    private final String thumbnail;
    private final User owner;
    private final List<Song> songs;

    public Playlist(String name, String id, String url, String thumbnail, User owner, List<Song> songs) {
        this.name = name;
        this.url = url;
        this.thumbnail = thumbnail;
        this.owner = owner;
        this.songs = songs;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }

    public User getOwner() {
        return owner;
    }

    public List<Song> getSongs() {
        return songs;
    }
}