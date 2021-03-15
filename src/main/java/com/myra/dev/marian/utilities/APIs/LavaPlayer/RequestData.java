package com.myra.dev.marian.utilities.APIs.LavaPlayer;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class RequestData {
    private final Long memberId;
    private final TextChannel channel;

    public RequestData(Long memberId, TextChannel channel) {
        this.memberId = memberId;
        this.channel = channel;
    }

    public Long getMemberId() {
        return this.memberId;
    }

    public Guild getGuild() {
        return this.channel.getGuild();
    }

    public TextChannel getChannel() {
        return this.channel;
    }
}
