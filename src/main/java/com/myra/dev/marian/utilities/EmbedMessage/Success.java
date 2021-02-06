package com.myra.dev.marian.utilities.EmbedMessage;

import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class Success {
    private final MessageReceivedEvent e;

    public Success(MessageReceivedEvent e) {
        this.e = e;
    }

    private String command;
    private String emoji;
    private String avatar;
    private int colour;
    private String message;
    private String image;
    private String footer;
    private boolean timestamp;
    private MessageChannel channel;
    private boolean delete;

    public Success setCommand(String command) {
        this.command = command;
        return this;
    }

    public Success setEmoji(String emoji) {
        this.emoji = emoji;
        return this;
    }

    public Success setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public Success setColour(int colour) {
        this.colour = colour;
        return this;
    }

    public Success setMessage(String message) {
        this.message = message;
        return this;
    }

    public Success appendMessage(String message) {
        this.message += message;
        return this;
    }

    public Success setImage(String url) {
        this.image = url;
        return this;
    }

    public Success setFooter(String footer) {
        this.footer = footer;
        return this;
    }

    public Success addTimestamp() {
        this.timestamp = true;
        return this;
    }

    public Success setChannel(MessageChannel channel) {
        this.channel = channel;
        return this;
    }

    public Success delete() {
        this.delete = true;
        return this;
    }

    public EmbedBuilder getEmbed() {
        // Exceptions
        if (this.command == null) throw new IllegalArgumentException("You need to set a command");
        if (this.message == null) throw new IllegalArgumentException("You need to set a message");

        int colour;
        if (this.colour == 0) colour = Utilities.getUtils().green;
        else colour = this.colour;

        String avatar;
        if (this.avatar == null) avatar = e.getAuthor().getEffectiveAvatarUrl();
        else avatar = this.avatar;

        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(command, null, avatar)
                .setColor(colour)
                .setDescription(message);

        if (this.image != null) embed.setImage(this.image);
        if (this.footer != null) embed.setFooter(this.footer);
        if (this.timestamp) embed.setTimestamp(Instant.now());

        return embed;
    }

    public void send() {
        // Exceptions
        if (this.command == null) throw new IllegalArgumentException("You need to set a command");
        if (this.message == null) throw new IllegalArgumentException("You need to set a message");

        int colour;
        if (this.colour == 0) colour = Utilities.getUtils().green;
        else colour = this.colour;

        String avatar;
        if (this.avatar == null) avatar = e.getAuthor().getEffectiveAvatarUrl();
        else avatar = this.avatar;

        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(command, null, avatar)
                .setColor(colour)
                .setDescription(message);

        if (this.image != null) embed.setImage(this.image);
        if (this.footer != null) embed.setFooter(this.footer);
        if (this.timestamp) embed.setTimestamp(Instant.now());

        MessageChannel channel;
        if (this.channel == null) channel = e.getTextChannel();
        else channel = this.channel;

        channel.sendMessage(embed.build()).queue(msg -> {
            if (!delete) return;
            Utilities.TIMER.schedule(() -> {
                if (msg != null) msg.delete().queue(); // Delete message if message isn't deleted yet
            }, 5, TimeUnit.SECONDS);
        });
    }
}
