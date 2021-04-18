package com.myra.dev.marian.utilities.EmbedMessage;

import com.myra.dev.marian.Config;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Instant;

public class Error {
    private final MessageReceivedEvent e;

    public Error(MessageReceivedEvent e) {
        this.e = e;
    }

    private String command;
    private String link;
    private String emoji;
    private String avatar;
    private int colour;
    private String message;
    private String footer;
    private boolean timestamp;
    private MessageChannel channel;

    public Error setCommand(String command) {
        this.command = command;
        return this;
    }

    public Error setLink(String link) {
        this.link = link;
        return this;
    }

    public Error setEmoji(String emoji) {
        this.emoji = emoji;
        return this;
    }

    public Error setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public Error setColour(int colour) {
        this.colour = colour;
        return this;
    }

    public Error setMessage(String message) {
        this.message = message;
        return this;
    }

    public Error appendMessage(String message) {
        this.message += message;
        return this;
    }

    public Error setFooter(String footer) {
        this.footer = footer;
        return this;
    }

    public Error addTimestamp() {
        this.timestamp = true;
        return this;
    }

    public Error setChannel(MessageChannel channel) {
        this.channel = channel;
        return this;
    }

    public void send() {
        // Exceptions
        if (this.command == null) throw new IllegalArgumentException("You need to set a command");
        if (this.message == null) throw new IllegalArgumentException("You need to set a message");

        int colour;
        if (this.colour == 0) colour = Utilities.getUtils().red;
        else colour = this.colour;

        String avatar;
        if (this.avatar == null) {
            // Runs if no event is set and the icon url is a default one
            if (this.e == null) avatar = Config.DEFAULT_AVATAR;
            else avatar = e.getAuthor().getEffectiveAvatarUrl();
        } else avatar = this.avatar;

        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(this.command, this.link, avatar)
                .setColor(colour)
                .setDescription(message);

        if (this.footer != null) embed.setFooter(this.footer);
        if (this.timestamp) embed.setTimestamp(Instant.now());

        if (this.channel == null) this.e.getChannel().sendMessage(embed.build()).queue();
        else this.channel.sendMessage(embed.build()).queue();
    }
}