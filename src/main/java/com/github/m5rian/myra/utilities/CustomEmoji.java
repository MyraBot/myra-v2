package com.github.m5rian.myra.utilities;

import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.DiscordBot;
import net.dv8tion.jda.api.entities.Emote;

import java.util.Arrays;
import java.util.Optional;

public enum CustomEmoji {
    // Badges
    HYPESQUAD_BRAVERY("HypeSquad Bravery", Config.MYRA_SERVER_ID),
    HYPESQUAD_BRILLIANCE("HypeSquad Brilliance", Config.MYRA_SERVER_ID),
    HYPESQUAD_BALANCE("HypeSquad Balance", Config.MYRA_SERVER_ID),
    PARTNER("Partner", Config.MYRA_SERVER_ID),
    STAFF("Discord Employee", Config.MYRA_SERVER_ID),
    BUG_HUNTER_LEVEL_1("Bug Hunter Level 1", Config.MYRA_SERVER_ID),
    BUG_HUNTER_LEVEL_2("Bug Hunter Level 2", Config.MYRA_SERVER_ID),

    MYRA_STAFF("Myra Staff", Config.MYRA_SERVER_ID),
    MYRA_TRANSLATOR("Myra Translator", Config.MYRA_SERVER_ID),
    MYRA_PARTNER("Myra Partner", Config.MYRA_SERVER_ID),
    // Ticks
    GREEN_TICK("Green Tick", Config.MYRA_SERVER_ID),
    RED_TICK("Red Tick", Config.MYRA_SERVER_ID),
    // Online status
    ONLINE("Online", Config.MYRA_SERVER_ID),
    DO_NOT_DISTURB("Do Not Disturb", Config.MYRA_SERVER_ID),
    IDLE("Idle", Config.MYRA_SERVER_ID),
    OFFLINE("Offline", Config.MYRA_SERVER_ID),
    // Other
    COIN("Coin", Config.MYRA_SERVER_ID),
    VOICE("Voice", Config.MYRA_SERVER_ID),
    NITRO("Nitro", Config.MYRA_SERVER_ID),
    CATALAN("Catalan", Config.MYRA_SERVER_ID),
    UNKNOWN("unknown", "0");

    private final String name;
    private final String serverId;

    CustomEmoji(String name, String serverId) {
        this.name = name;
        this.serverId = serverId;
    }

    public Emote getEmote() {
        return DiscordBot.shardManager.getGuildById(this.serverId).getEmotesByName(this.name.replaceAll("\\s+", ""), true).get(0);
    }

    public String getAsMention() {
        return DiscordBot.shardManager.getGuildById(this.serverId).getEmotesByName(this.name.replaceAll("\\s+", ""), true).get(0).getAsMention();
    }

    public String getImage() {
        return DiscordBot.shardManager.getGuildById(this.serverId).getEmotesByName(this.name.replaceAll("\\s+", ""), true).get(0).getImageUrl();
    }

    public String getCodepoints() {
        return DiscordBot.shardManager.getGuildById(this.serverId).getEmotesByName(this.name.replaceAll("\\s+", ""), true).get(0).toString().substring(2);
    }

    public static CustomEmoji search(String query) {
        Optional<CustomEmoji> first = Arrays.stream(CustomEmoji.values())
                .filter(emoji -> emoji.name.equalsIgnoreCase(query) || emoji.name.replace("\\s+", "").equalsIgnoreCase(query))
                .findFirst();
        if (!first.isPresent()) System.out.println(query);
        return first.get();
    }
}
