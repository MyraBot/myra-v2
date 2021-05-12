package com.myra.dev.marian.listeners;

import com.myra.dev.marian.utilities.APIs.LavaPlayer.RequestData;
import com.myra.dev.marian.utilities.CustomEmoji;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Utilities;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;

public class MusicAnnouncer extends AudioEventAdapter {
    private String oldMessageId;

    /**
     * @param player Audio player
     * @param track  Audio track that started
     */
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        final RequestData requestData = track.getUserData(RequestData.class); // Get request data

        requestData.getGuild().retrieveMemberById(requestData.getMemberId()).queue(member -> {
            final AudioTrackInfo trackInfo = track.getInfo(); // Get track information
            String announcement = String.format("%s Now playing %s [%s]", CustomEmoji.VOICE.getAsEmoji(), Utilities.getUtils().hyperlink(String.format("`%s - %s`", trackInfo.title, trackInfo.author), trackInfo.uri), member.getAsMention());

            final EmbedBuilder nowPlaying = new Success(null)
                    .setCommand("Now playing")
                    .setAvatar(member.getUser().getEffectiveAvatarUrl())
                    .setMessage(announcement)
                    .setChannel(requestData.getChannel())
                    .getEmbed();

            // Delete old message
            if (oldMessageId != null) {
                requestData.getChannel().retrieveMessageById(oldMessageId).queue(
                        msg -> msg.delete().queue(),
                        error -> {}
                );
            }
            // Send message
            requestData.getChannel().sendMessage(nowPlaying.build()).queue(msg -> oldMessageId = msg.getId());
        });
    }
}