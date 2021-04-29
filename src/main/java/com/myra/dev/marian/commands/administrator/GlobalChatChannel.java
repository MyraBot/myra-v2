package com.myra.dev.marian.commands.administrator;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.EmbedBuilder;
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
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("global chat", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "global chat <channel>`", "\uD83C\uDF10 │ Communicate with other servers", false);
            ctx.getChannel().sendMessage(usage.build()).queue(); // Send usage
            return;
        }

        final TextChannel channel = Utilities.getUtils().getTextChannel(ctx.getEvent(), ctx.getArguments()[0], "global chat", "\uD83C\uDF10"); // Get text channel
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
                    success.setMessage("Global chat is now set to " + channel.getAsMention());
                    success.setMessage("Here you will receive messages from other servers").setChannel(channel).send();

                }
                // Remove global chat
                else {
                    new MongoGuild(ctx.getGuild()).setNull("globalChat"); // Update database
                    webhook.delete().queue(); // Delete webhook
                    success.setMessage("The global chat has been removed").send();
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
