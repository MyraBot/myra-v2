package com.github.m5rian.myra.database.guild.member;

public class IdMemberData {

    private final String guildId;
    private final String memberId;

    public IdMemberData(String guildId, String memberId) {
        this.guildId = guildId;
        this.memberId = memberId;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getMemberId() {
        return memberId;
    }
}
