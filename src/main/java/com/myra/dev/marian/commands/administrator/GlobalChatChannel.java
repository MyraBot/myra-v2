package com.myra.dev.marian.commands.administrator;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Utilities;
import static com.myra.dev.marian.utilities.language.Lang.*;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.atomic.AtomicBoolean;

public class GlobalChatChannel implements CommandHandler {

    @CommandEvent(
            name = "global chat",
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("global chat")
                    .addUsages(new Usage()
                            .setUsage("global chat <channel>")
                            .setEmoji("\uD83C\uDF10")
                            .setDescription(lang(ctx).get("description.globalChatChannel")))
                    .send();
            return;
        }

        final TextChannel channel = Utilities.getTextChannel(ctx.getEvent(), ctx.getArguments()[0], "global chat", "\uD83C\uDF10"); // Get text channel
        if (channel == null) return;

        final String webhookUrl = new MongoGuild(ctx.getGuild()).getString("globalChat"); // Get current webhook url

        Success success = new Success(ctx.getEvent())
                .setCommand("global chat")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setEmoji("\uD83C\uDF10");

        // Create global chat
        if (webhookUrl == null) {
            createWebhook(channel);
            success.setMessage("Set the global chat to " + channel.getAsMention()).setChannel(ctx.getChannel()).send();
            success.setMessage("Here you will receive messages from other servers").setChannel(channel).send();
        }

        // Change global chat
        if (webhookUrl != null) {
            AtomicBoolean webhookUrlNull = new AtomicBoolean(true);
            ctx.getGuild().retrieveWebhooks().queue(webhooks -> webhooks.forEach(webhook -> {
                if (!webhook.getUrl().equals(webhookUrl)) return;

                // Change global chat
                if (webhook.getChannel() != channel) {
                    webhook.delete().queue(); // Delete webhook
                    createWebhook(channel); // Create webhook
                    success.setMessage(lang(ctx).get("command.globalChat.channel.info.changed")).send();
                    success.setMessage(lang(ctx).get("command.globalChat.channel.info.changedActive")
                            .replace("{$channel}", channel.getAsMention())) // New global chat channel
                            .setChannel(channel)
                            .send();

                }
                // Remove global chat
                else {
                    new MongoGuild(ctx.getGuild()).setNull("globalChat"); // Update database
                    webhook.delete().queue(); // Delete webhook
                    success.setMessage(lang(ctx).get("command.globalChat.channel.info.removed")).send();
                }
                webhookUrlNull.set(false);
            }));
        }
    }

    private void createWebhook(TextChannel channel) {
        channel.createWebhook("global chat").queue(webhook -> {
            final String url = webhook.getUrl(); // Get webhook url
            new MongoGuild(channel.getGuild()).setString("globalChat", url); // Update database
        });
    }
}
