package com.myra.dev.marian.utilities.EmbedMessage;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandUsage {
    private final MessageReceivedEvent e;

    public CommandUsage(MessageReceivedEvent e) {
        this.e = e;
    }

    private String command;
    private String avatar;
    private final List<Usage> usages = new ArrayList<>();
    private String information = "";
    private TextChannel channel;

    public CommandUsage setCommand(String command) {
        this.command = command;
        return this;
    }

    public CommandUsage setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public CommandUsage addUsages(Usage... usages) {
        this.usages.addAll(Arrays.asList(usages));
        return this;
    }

    public CommandUsage addInformation(String information) {
        this.information += information;
        return this;
    }


    public CommandUsage setChannel(TextChannel channel) {
        this.channel = channel;
        return this;
    }

    public void send() {
        // Get avatar
        String avatar;
        if (this.avatar == null) avatar = this.e.getAuthor().getEffectiveAvatarUrl();
        else avatar = this.avatar;

        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(this.command, null, avatar)
                .setColor(0x2F3136)
                .setFooter(this.information);
        // Add all commands usages
        usages.forEach(usage -> {
            embed.addField(
                    String.format("`%s`", usage.getUsage()),
                    String.format("%s │ %s", usage.getEmoji(), usage.getDescription()),
                    false);
        });

        // Send command usage
        if (this.channel == null) this.e.getChannel().sendMessage(embed.build()).queue();
        else this.channel.sendMessage(embed.build()).queue();
    }
}
