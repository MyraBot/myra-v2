package com.myra.dev.marian.commands.music;


import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.utilities.APIs.GoogleYouTube;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.MessageReaction;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("ConstantConditions") // Requires '.enableCache(CacheFlag.VOICE_STATE)' to be not null
@CommandSubscribe(
        name = "play",
        channel = Channel.GUILD
)
public class MusicPlay implements Command {
    private static HashMap<String, List<JSONObject>> results = new HashMap<>();

    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        //command usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("play", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "play <song>`", "\uD83D\uDCBF │ add a song to the queue*", false)
                    .setFooter("supported platforms: YoutTube, SoundCloud, Bandcamp, Vimeo, Twitch streams");
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
// Add a audio track to the queue
        // If bot isn't in a voice channel
        if (!ctx.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) {
            new Error(ctx.getEvent())
                    .setCommand("play")
                    .setEmoji("\uD83D\uDCBF")
                    .setMessage("I need to be in a voice channel")
                    .send();
            return;
        }
        // If author isn't in a voice channel yet
        if (!ctx.getEvent().getMember().getVoiceState().inVoiceChannel()) {
            new Error(ctx.getEvent())
                    .setCommand("play")
                    .setEmoji("\uD83D\uDCBF")
                    .setMessage("You need to join a voice channel first to use this command")
                    .send();
            return;
        }
        // If author isn't in the same voice channel as bot
        if (!ctx.getEvent().getMember().getVoiceState().getChannel().equals(ctx.getGuild().getSelfMember().getVoiceState().getChannel())) {
            new Error(ctx.getEvent())
                    .setCommand("play")
                    .setEmoji("\uD83D\uDCBF")
                    .setMessage("You need to join the same voice channel as me")
                    .send();
            return;
        }
        // Get song
        String song = utilities.getString(ctx.getArguments());
        // If song is url
        try {
            new URL(song).toURI();
            // Delete message
            ctx.getEvent().getMessage().delete().queue();
            // Play song
            PlayerManager.getInstance().loadAndPlay(ctx.getGuild(),ctx.getChannel(), song, ctx.getAuthor().getEffectiveAvatarUrl(), null);
        }
        // If song is given by name
        catch (Exception e) {
            // Search on YouTube for song name
            final List<JSONObject> videos = GoogleYouTube.getInstance().searchForVideo(song);
            // Nothing found
            if (videos.isEmpty()) {
                new Error(ctx.getEvent())
                        .setCommand("play")
                        .setEmoji("\uD83D\uDCBF")
                        .setMessage("No results")
                        .send();
                return;
            }
            // Song menu
            EmbedBuilder songs = new EmbedBuilder()
                    .setAuthor("choose a song", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.blue)
                    .addField("\uD83D\uDD0D │ track 1\uFE0F\u20E3", videos.get(0).getJSONObject("snippet").getString("title"), false)
                    .addField("\uD83D\uDD0D │ track 2\uFE0F\u20E3", videos.get(1).getJSONObject("snippet").getString("title"), false)
                    .addField("\uD83D\uDD0D │ track 3\uFE0F\u20E3", videos.get(2).getJSONObject("snippet").getString("title"), false)
                    .addField("\uD83D\uDD0D │ track 4\uFE0F\u20E3", videos.get(3).getJSONObject("snippet").getString("title"), false)
                    .addField("\uD83D\uDD0D │ track 5\uFE0F\u20E3", videos.get(4).getJSONObject("snippet").getString("title"), false);
            Message message = ctx.getChannel().sendMessage(songs.build()).complete();
            // Save results in HashMap
            results.put(message.getId(), videos);
            //add reactions
            message.addReaction("1\uFE0F\u20E3").queue();
            message.addReaction("2\uFE0F\u20E3").queue();
            message.addReaction("3\uFE0F\u20E3").queue();
            message.addReaction("4\uFE0F\u20E3").queue();
            message.addReaction("5\uFE0F\u20E3").queue();

            message.addReaction("\uD83D\uDEAB").queue();
            // Add reaction to HashMap
            MessageReaction.add(ctx.getGuild(), "play", message, ctx.getAuthor(), true, "1\uFE0F\u20E3", "2\uFE0F\u20E3", "3\uFE0F\u20E3", "4\uFE0F\u20E3", "5\uFE0F\u20E3");
        }
    }

    //chose song
    public void guildMessageReactionAddEvent(GuildMessageReactionAddEvent event) throws Exception {
        if (!MessageReaction.check(event, "play", true)) return;
        // Search canceled
        if (event.getReactionEmote().getEmoji().equals("\uD83D\uDEAB")) {
            // Clear reactions
            event.getChannel().retrieveMessageById(event.getMessageId()).complete().clearReactions().complete();
        }
        // Get chosen song
        else {
            JSONObject song = results.get(event.getMessageId()).get(Integer.parseInt(event.getReactionEmote().getEmoji().replace("1️⃣", "0").replace("2️⃣", "1").replace("3️⃣", "2").replace("4️⃣", "3").replace("5️⃣", "4")));
            if (song == null) return;
            //get video url
            String videoUrl = "https://www.youtube.com/watch?v=" + song.getJSONObject("id").getString("videoId");
            //play song
            PlayerManager.getInstance().loadAndPlay(event.getGuild() ,event.getChannel(), videoUrl, event.getUser().getEffectiveAvatarUrl(), "https://img.youtube.com/vi/" + song.getJSONObject("id").getString("videoId") + "/maxresdefault.jpg");
            //delete track selector
            event.getChannel().deleteMessageById(event.getMessageId()).queue();
        }
    }
}