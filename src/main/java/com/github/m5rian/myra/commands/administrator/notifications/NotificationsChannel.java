package com.github.m5rian.myra.commands.administrator.notifications;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import static com.github.m5rian.myra.utilities.language.Lang.*;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import net.dv8tion.jda.api.entities.TextChannel;

public class NotificationsChannel implements CommandHandler {

    @CommandEvent(
            name = "notifications channel",
            aliases = {"notification channel"},
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("notifications channel")
                    .addUsages(new Usage()
                            .setUsage("notification channel <channel>")
                            .setEmoji("\uD83D\uDCC1")
                            .setDescription(lang(ctx).get("description.notificationsChannel")))
                    .send();
            return;
        }


        // Get provided text channel
        final TextChannel channel = Utilities.getTextChannel(ctx.getEvent(), ctx.getArguments()[0], "notification channel", "\uD83D\uDD14");
        if (channel == null) return;

        final MongoGuild db = new MongoGuild(ctx.getGuild()); //Get database
        final String currentChannelId = db.getNested("notifications").getString("channel"); // Get current notification channel
        // Remove notification channel
        if (currentChannelId.equals(channel.getId())) {
            db.getNested("notifications").setString("channel", "not set"); // Remove channel
            // Success
            new Success(ctx.getEvent())
                    .setCommand("notification channel")
                    .setEmoji("\uD83D\uDD14")
                    .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                    .setMessage(lang(ctx).get("command.notifications.channel.removed")
                            .replace("{$channel}", channel.getAsMention())) // Old channel
                    .send();
        }
        // Chane notification channel
        else {
            db.getNested("notifications").setString("channel", channel.getId()); // Set notification channel
            // Success
            Success success = new Success(ctx.getEvent())
                    .setCommand("notification channel")
                    .setEmoji("\uD83D\uDD14")
                    .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl());

            success.setMessage(lang(ctx).get("command.notifications.channel.added")
                    .replace("{$channel}", channel.getAsMention())) // New channel
                    .send(); // Send success message in current channel
            success.setMessage(lang(ctx).get("command.notifications.channel.addedActive"))
                    .setChannel(channel)
                    .send(); // Send message in new notification channel
        }

    }
}
