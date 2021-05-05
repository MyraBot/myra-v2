package com.myra.dev.marian.database.guild.member;

import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;

public class LeaderboardMember {
    private String id;

    private int level;
    private int xp;
    private int balance;
    private long voiceCallTime;

    public LeaderboardMember(Document memberDocument, Guild guild) {
        this.id = memberDocument.getString("userId");

        final Document guildMemberDocument = (Document) memberDocument.get(guild.getId()); // Get document of guild
        this.level = guildMemberDocument.getInteger("level");
        this.xp = guildMemberDocument.getInteger("xp");
        this.balance = guildMemberDocument.getInteger("balance");
        try {
            this.voiceCallTime = guildMemberDocument.getLong("voiceCallTime");
        }
        // If voice call time is an integer
        catch (ClassCastException e){
            this.voiceCallTime = Long.valueOf(guildMemberDocument.getInteger("voiceCallTime"));
        }
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getBalance() {
        return balance;
    }

    public long getVoiceCallTime() {
        return voiceCallTime;
    }
}