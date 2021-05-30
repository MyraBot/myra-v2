package com.github.m5rian.myra.database.guild.member;

import com.github.m5rian.myra.utilities.Utilities;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;

public class LeaderboardMember {
    private String id;

    private int level;
    private long xp;
    private int balance;
    private long voiceCallTime;

    public LeaderboardMember(Document memberDocument, Guild guild) {
        this.id = memberDocument.getString("userId");

        final Document guildMemberDocument = (Document) memberDocument.get(guild.getId()); // Get document of guild
        this.level = guildMemberDocument.getInteger("level");
        this.xp = Utilities.getBsonLong(guildMemberDocument, "xp");
        this.balance = guildMemberDocument.getInteger("balance");
        this.voiceCallTime = Utilities.getBsonLong(guildMemberDocument, "voiceCallTime");
    }

    public String getId() {
        return id;
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