package com.myra.dev.marian.utilities;

import com.myra.dev.marian.Config;
import com.myra.dev.marian.DiscordBot;
import net.dv8tion.jda.api.entities.Emote;

import java.util.Arrays;
import java.util.Optional;

public enum CustomEmoji {
    // Badges
    HYPESQUAD_BRAVERY("HypeSquad Bravery", Config.myraServer),
    HYPESQUAD_BRILLIANCE("HypeSquad Brilliance", Config.myraServer),
    HYPESQUAD_BALANCE("HypeSquad Balance", Config.myraServer),
    PARTNER("Partner", Config.myraServer),
    STAFF("Discord Employee", Config.myraServer),
    BUG_HUNTER_LEVEL_1("Bug Hunter Level 1", Config.myraServer),
    BUG_HUNTER_LEVEL_2("Bug Hunter Level 2", Config.myraServer),

    MYRA_STAFF("Myra Staff", Config.myraServer),
    MYRA_PARTNER("Myra Partner", Config.myraServer),
    // Ticks
    GREEN_TICK("Green Tick", Config.myraServer),
    RED_TICK("Red Tick", Config.myraServer),
    // Other
    COIN("Coin", Config.myraServer),
    VOICE("Voice", Config.myraServer),
    NITRO("Nitro", Config.myraServer),
    UNKNOWN("unknown", "0");

    private final String name;
    private final String serverId;

    CustomEmoji(String name, String serverId) {
        this.name = name;
        this.serverId = serverId;
    }

    public Emote getAsEmote() {
        return DiscordBot.shardManager.getGuildById(this.serverId).getEmotesByName(this.name.replaceAll("\\s+", ""), true).get(0);
    }

    public String getAsEmoji() {
        return DiscordBot.shardManager.getGuildById(this.serverId).getEmotesByName(this.name.replaceAll("\\s+", ""), true).get(0).getAsMention();
    }

    public String getImage() {
        return DiscordBot.shardManager.getGuildById(this.serverId).getEmotesByName(this.name.replaceAll("\\s+", ""), true).get(0).getImageUrl();
    }

    public String getAsReactionEmote() {
        return "R" + DiscordBot.shardManager.getGuildById(this.serverId).getEmotesByName(this.name.replaceAll("\\s+", ""), true).get(0);
    }

    public static CustomEmoji search(String query) {
        Optional<CustomEmoji> first = Arrays.stream(CustomEmoji.values())
                .filter(emoji -> emoji.name.equalsIgnoreCase(query) || emoji.name.replace("\\s+", "").equalsIgnoreCase(query))
                .findFirst();
        if (!first.isPresent()) System.out.println(query);
        return first.get();
    }
}
