package com.myra.dev.marian.utilities.APIs.LavaPlayer;

import com.myra.dev.marian.listeners.MusicAnnouncer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildMusicManager {
    /**
     * Audio player for the guild.
     */
    public final AudioPlayer audioPlayer;
    /**
     * Track schedulers for the player.
     */
    public final TrackScheduler scheduler;

    private final AudioPlayerSendHandler sendHandler;

    /**
     * Creates a player and a track scheduler.
     *
     * @param manager Audio player manager to use for creating the player.
     */
    public GuildMusicManager(AudioPlayerManager manager) {
        this.audioPlayer = manager.createPlayer();
        this.scheduler = new TrackScheduler(this.audioPlayer);
        this.audioPlayer.addListener(this.scheduler);
        this.audioPlayer.addListener(new MusicAnnouncer()); // Register track announcer
        this.sendHandler = new AudioPlayerSendHandler(this.audioPlayer);
    }

    /**
     * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
     */
    public AudioPlayerSendHandler getSendHandler() {
        return sendHandler;
    }
}