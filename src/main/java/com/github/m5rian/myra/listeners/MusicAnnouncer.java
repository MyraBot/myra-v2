package com.github.m5rian.myra.listeners;

import com.github.m5rian.myra.DiscordBot;
import com.github.m5rian.myra.utilities.APIs.LavaPlayer.PlayerManager;
import com.github.m5rian.myra.utilities.APIs.LavaPlayer.RequestData;
import com.github.m5rian.myra.utilities.CustomEmoji;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.language.Lang;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

public class MusicAnnouncer extends AudioEventAdapter {
    private String oldMessageId;

    /**
     * @param player Audio player
     * @param track  Audio track that started
     */
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        final RequestData requestData = track.getUserData(RequestData.class); // Get request data

        requestData.getGuild().retrieveMemberById(requestData.getMemberId()).queue(member -> {
            final Long guildId = PlayerManager.getGuildIdFromPlayer(player); // Get guild id of audioPlayer
            final Guild guild = DiscordBot.shardManager.getGuildById(guildId); // Get guild by id
            final String announcement = Lang.lang(guild).get("listener.music.announcer")
                    .replace("{$emote}", CustomEmoji.VOICE.getAsMention()) // Voice call emote
                    .replace("{$track.name}", track.getInfo().title) // Track title
                    .replace("{$track.url}", track.getInfo().uri) // Track url
                    .replace("{$requester.mention}", member.getAsMention()); // Requester mention

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
                        error -> {
                        }
                );
            }
            // Send message
            requestData.getChannel().sendMessage(nowPlaying.build()).queue(msg -> oldMessageId = msg.getId());
        });
    }
}