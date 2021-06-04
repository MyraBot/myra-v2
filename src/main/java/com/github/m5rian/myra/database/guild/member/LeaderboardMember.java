package com.github.m5rian.myra.database.guild.member;

import com.github.m5rian.myra.utilities.Utilities;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;

public class LeaderboardMember {
    private final String id;
    private final String name;
    private final String discriminator;
    private final String avatar;

    private final int level;
    private final long xp;
    private final int balance;
    private final long voiceCallTime;

    public LeaderboardMember(Document memberDocument, Guild guild) {
        this.id = memberDocument.getString("userId");
        this.name = memberDocument.getString("name");
        this.discriminator = memberDocument.getString("discriminator");
        this.avatar = memberDocument.getString("avatar");

        final Document guildMemberDocument = (Document) memberDocument.get(guild.getId()); // Get document of guild
        this.level = guildMemberDocument.getInteger("level");
        this.xp = Utilities.getBsonLong(guildMemberDocument, "xp");
        this.balance = guildMemberDocument.getInteger("balance");
        this.voiceCallTime = Utilities.getBsonLong(guildMemberDocument, "voiceCallTime");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getLevel() {
        return level;
    }

    public long getXp() {
        return xp;
    }

    public int getBalance() {
        return balance;
    }

    public long getVoiceCallTime() {
        return voiceCallTime;
    }
}