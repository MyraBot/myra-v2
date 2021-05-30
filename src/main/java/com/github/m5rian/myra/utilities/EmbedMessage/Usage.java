package com.github.m5rian.myra.utilities.EmbedMessage;

public class Usage {
    private String usage;
    private String description;
    private String emoji;
    private boolean isPremium = false;

    public Usage setUsage(String usage) {
        this.usage = usage;
        return this;
    }

    public Usage setDescription(String description) {
        this.description = description;
        return this;
    }

    public Usage setEmoji(String emoji) {
        this.emoji = emoji;
        return this;
    }

    public Usage isPremium() {
        this.isPremium = true;
        return this;
    }

    public String getUsage() {
        return this.usage;
    }

    public String getDescription() {
        return this.description;
    }

    public String getEmoji() {
        return this.emoji;
    }

    public boolean getPremiumStatus() {
        return this.isPremium;
    }
}
