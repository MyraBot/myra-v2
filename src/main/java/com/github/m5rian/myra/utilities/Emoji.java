package com.github.m5rian.myra.utilities;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageReaction;

public class Emoji {

    private final Emote emote;

    private final String unicode;
    private final String codepoints;

    public Emoji(String unicode, String codepoints, Emote emote) {
        this.unicode = unicode;
        this.codepoints = codepoints;

        this.emote = emote;
    }

    public static Emoji fromUnicode(String unicode) {
        final String codepoints = Utilities.toCodepoints(unicode);
        return new Emoji(unicode, codepoints, null);
    }

    public static Emoji fromReactionEmote(MessageReaction.ReactionEmote reactionEmote) {
        // Is emoji
        if (reactionEmote.isEmoji()) return fromUnicode(reactionEmote.getName());
        // Is emote
        return new Emoji(null, null, reactionEmote.getEmote());
    }

    public Emote getEmote() {
        return emote;
    }

    public String getUnicode() {
        return unicode;
    }

    public String getCodepoints() {
        return codepoints;
    }

    public boolean equals(Emoji emoji) {
        final boolean emote = this.emote != null && emoji.getEmote() != null && this.emote.equals(emoji.getEmote()) || (this.emote == null && emoji.getEmote() == null);
        final boolean unicode = this.unicode != null && emoji.getUnicode() != null && this.unicode.equals(emoji.getUnicode()) || (this.unicode == null && emoji.getUnicode() == null);
        final boolean codepoints = this.codepoints != null && emoji.getCodepoints() != null && this.codepoints.equals(emoji.getCodepoints()) || (this.codepoints == null && emoji.getCodepoints() == null);

        return emote && unicode && codepoints;
    }
}
