package com.github.m5rian.myra.commands.member.music;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandUtils;
import com.github.m5rian.myra.utilities.APIs.LavaPlayer.PlayerManager;
import com.github.m5rian.myra.utilities.APIs.LavaPlayer.TrackScheduler;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.CustomEmoji;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MusicVoteListener {
    public void onMusicCommand(Message message) {
        final Emote emote = CustomEmoji.GREEN_TICK.getEmote();
        message.addReaction(emote).queue();
    }

    private final MusicSkip musicSkip = new MusicSkip();
    private final MusicClearQueue musicClearQueue = new MusicClearQueue();

    public void onVoteAdd(GuildMessageReactionAddEvent event) {
        if (!event.getReaction().getReactionEmote().isEmote()) return;
        final Emote emote = CustomEmoji.GREEN_TICK.getEmote(); // Get vote reaction emote
        if (!event.getReaction().getReactionEmote().getEmote().equals(emote)) return;

        event.retrieveMessage().queue(message -> { // Retrieve message
            event.retrieveMember().queue(member -> {
                if (member.getVoiceState() == null) return; // Message author isn't in a vc anymore
                if (!member.getVoiceState().inVoiceChannel()) return; // Reaction author isn't in a vc

                final VoiceChannel voiceChannel = event.getMember().getVoiceState().getChannel(); // Get voice channel
                if (voiceChannel.getMembers().isEmpty()) return; // Everyone left the voice call
                final List<Member> members = voiceChannel.getMembers(); // Get members in a voice channel
                final int size = (int) members.stream().filter(vcaller -> !vcaller.getUser().isBot()).count();

                AtomicInteger votes = new AtomicInteger();
                message.getReactions().forEach(reaction -> {
                    // Right emote
                    if (reaction.getReactionEmote().getEmote().equals(emote)) {
                        reaction.retrieveUsers().forEachAsync(user -> {
                            // Add 1 vote
                            if (members.stream().anyMatch(reactedMember -> reactedMember.getUser() == user) && !user.isBot())
                                votes.addAndGet(1);

                            return true; // Iterates over all entities until the provided action returns false
                        }).whenComplete((input, exception) -> { // Run after all entities are iterated
                            if (exception != null) {
                                exception.printStackTrace();
                            } else {
                                try {
                                    System.out.println(votes.get());
                                    if (votes.get() < size / 2) return; // Not enough votes

                                    final String prefix = MongoGuild.get(event.getGuild()).getString("prefix"); // Get prefix
                                    final TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler;  // Get track scheduler

                                    final List<String> skipExecutors = CommandUtils.getCommandExecutors(MusicSkip.class.getMethod("execute", CommandContext.class)); /** Get all executors from the {@link MusicSkip} class*/
                                    final List<String> clearQueueExecutors;
                                    clearQueueExecutors = CommandUtils.getCommandExecutors(MusicClearQueue.class.getMethod("execute", CommandContext.class)); /** Get all executors from the {@link MusicClearQueue} class*/

                                    // Skip song
                                    if (skipExecutors.stream().anyMatch(executor -> message.getContentRaw().equalsIgnoreCase(prefix + executor))) {
                                        musicSkip.skip(event.getGuild(), event.getChannel(), message.getMember());
                                    }
                                    // Clear queue
                                    if (clearQueueExecutors.stream().anyMatch(executor -> message.getContentRaw().equalsIgnoreCase(prefix + executor))) {
                                        musicClearQueue.clearQueue(scheduler, event.getChannel(), message.getMember());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            });
        });
    }
}
