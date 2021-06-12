package com.github.m5rian.myra.utilities.APIs.mee6;

import org.json.JSONObject;

public class Mee6User {

    private final String userId;
    private final String avatar;
    private final String name;
    private final String discriminator;

    private final String guildId;

    private final Integer level;
    private final Integer xp;
    private final Integer messageCount;


    public Mee6User(JSONObject data) {
        this.userId = data.getString("id");
        this.avatar = data.getString("avatar");
        this.name = data.getString("username");
        this.discriminator = data.getString("discriminator");

        this.guildId = data.getString("guild_id");
        
        this.level = data.getInt("level");
        this.xp = data.getInt("xp");
        this.messageCount = data.getInt("message_count");
    }

    public String getUserId() {
        return this.userId;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public String getName() {
        return this.name;
    }

    public String getDiscriminator() {
        return this.discriminator;
    }

    public String getGuildId() {
        return this.guildId;
    }

    public Integer getLevel() {
        return this.level;
    }

    public Integer getXp() {
        return this.xp;
    }

    public Integer getMessageCount() {
        return this.messageCount;
    }
}
