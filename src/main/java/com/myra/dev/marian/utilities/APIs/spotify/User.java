package com.myra.dev.marian.utilities.APIs.spotify;

public class User {
    private final String name;
    public final String id;
    private final String url;

    public User(String name,String id, String url) {
        this.name = name;
        this.id = id;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}
