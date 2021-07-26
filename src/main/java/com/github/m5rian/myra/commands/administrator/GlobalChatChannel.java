package com.github.m5rian.myra.commands.administrator;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class GlobalChatChannel implements CommandHandler {

    @CommandEvent(
            name = "global chat",
            emoji = "\uD83C\uDF10",
            description = "description.globalChat",
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

        final String webhookUrl = MongoGuild.get(ctx.getGuild()).getString("globalChat"); // Get current webhook url

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
                    MongoGuild.get(ctx.getGuild()).setNull("globalChat"); // Update database
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
            MongoGuild.get(channel.getGuild()).setString("globalChat", url); // Update database
        });
    }
}
