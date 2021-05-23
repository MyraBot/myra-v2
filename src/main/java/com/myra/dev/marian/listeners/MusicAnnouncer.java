package com.myra.dev.marian.listeners;

import com.myra.dev.marian.DiscordBot;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.RequestData;
import com.myra.dev.marian.utilities.CustomEmoji;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

import static com.myra.dev.marian.utilities.language.Lang.lang;

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
            final String announcement = lang(guild).get("listener.music.announcer")
                    .replace("{$emote}", CustomEmoji.VOICE.getAsEmoji()) // Voice call emote
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