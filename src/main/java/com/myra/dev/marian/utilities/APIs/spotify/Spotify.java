package com.myra.dev.marian.utilities.APIs.spotify;

import com.myra.dev.marian.utilities.Utilities;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Spotify {
    private static final Spotify INSTANCE = new Spotify();

    public static Spotify getApi() {
        return INSTANCE;
    }

    private String accessToken;

    public void generateAuthToken() {
        // Form parameters
        final RequestBody body = new FormBody.Builder()
                .add("client_id", Utilities.getUtils().spotifyClientId) // Add client id
                .add("client_secret", Utilities.getUtils().spotifyClientSecret) // Add client secret
                .add("grant_type", "client_credentials")
                .build();
        //build request
        final Request request = new Request.Builder()
                .url("https://accounts.spotify.com/api/token")
                .post(body)
                .build(); // Make post request
        // Execute call
        try (Response response = Utilities.HTTP_CLIENT.newCall(request).execute()) {
            final String output = response.body().string(); // Fetch response
            final JSONObject json = new JSONObject(output); // Parse response to JSONObject
            final String accessToken = json.getString("access_token"); // Get access token
            this.accessToken = accessToken;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Playlist getPlaylist(String playlistId) {
        Request playlistRequest = new Request.Builder()
                .addHeader("Authorization", "Bearer " + this.accessToken)
                .url("https://api.spotify.com/v1/playlists/" + playlistId)
                .build();
        // Execute call
        JSONObject jsonPlaylist = null;
        try (Response response = Utilities.HTTP_CLIENT.newCall(playlistRequest).execute()) {
            final String output = response.body().string(); // Fetch response
            jsonPlaylist = new JSONObject(output); // Parse to JSONObject
        } catch (IOException e) {
            e.printStackTrace();
        }

        Request playlistThumbnailRequest = new Request.Builder()
                .addHeader("Authorization", "Bearer " + this.accessToken)
                .url("https://api.spotify.com/v1/playlists/" + playlistId + "/images")
                .build();
        // Execute call
        JSONArray jsonPlaylistThumbnail = null;
        try (Response response = Utilities.HTTP_CLIENT.newCall(playlistThumbnailRequest).execute()) {
            final String output = response.body().string(); // Fetch response
            jsonPlaylistThumbnail = new JSONArray(output); // Parse to JSONObject
        } catch (IOException e) {
            try {
                System.out.println(Utilities.HTTP_CLIENT.newCall(playlistThumbnailRequest).execute().body().string());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            e.printStackTrace();
        }

        final String playlistName = jsonPlaylist.getString("name"); // Get playlist name
        final String playlistUrl = jsonPlaylist.getJSONObject("external_urls").getString("spotify"); // Get playlist url
        final String playlistThumbnail = jsonPlaylistThumbnail.getJSONObject(0).getString("url"); // Get playlist thumbnail url

        final String ownerName = jsonPlaylist.getJSONObject("owner").getString("display_name"); // Get author name
        final String ownerId = jsonPlaylist.getJSONObject("owner").getString("id"); // Get author id
        final String ownerUrl = jsonPlaylist.getJSONObject("owner").getJSONObject("external_urls").getString("spotify"); // Get author url
        final User owner = new User(ownerName, ownerUrl, ownerId); // Create owner object

        final JSONArray tracksJson = jsonPlaylist.getJSONObject("tracks").getJSONArray("items"); // Get tracks
        final List<Song> songs = new ArrayList<>(); // Create list for all songs
        for (int i0 = 0; i0 < tracksJson.length(); i0++) {
            final JSONObject track = tracksJson.getJSONObject(i0).getJSONObject("track"); // Get current track

            final String trackName = track.getString("name"); // Get song name
            final String trackId = track.getString("id");
            final String trackImage = track.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url"); // Get song image
            final String trackUrl = track.getJSONObject("external_urls").getString("spotify"); // Get song url
            String trackPreview = null; // Set audio preview to null
            if (!track.isNull("preview_url")) {
                trackPreview = track.getString("preview_url"); // Get audio preview
            }
            final boolean isExplicit = track.getBoolean("explicit"); // Is song explicit

            final JSONArray artistsJson = track.getJSONArray("artists"); // Get artists
            final List<User> artists = new ArrayList<>(); // Create list for all artists
            for (int i1 = 0; i1 < artistsJson.length(); i1++) {
                final JSONObject artist = artistsJson.getJSONObject(i1); // Get current artists
                final String artistName = artist.getString("name"); // Get name of artist
                final String artistId = artist.getString("id"); // Get id of artist
                final String artistUrl = artist.getJSONObject("external_urls").getString("spotify"); // Get name of artist

                final User user = new User(artistName, artistId, artistUrl); // Create user object
                artists.add(user); // Add artist to list
            }

            final Song song = new Song(trackName, trackId, trackImage, trackUrl, trackPreview, artists, isExplicit); // Create song object
            songs.add(song); // Add song to list
        }

        return new Playlist(playlistName, playlistId, playlistUrl, playlistThumbnail, owner, songs); // Return playlist
    }
}
