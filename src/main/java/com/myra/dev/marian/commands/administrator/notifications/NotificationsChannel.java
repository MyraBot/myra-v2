package com.myra.dev.marian.commands.administrator.notifications;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.entities.TextChannel;

public class NotificationsChannel implements CommandHandler {

@CommandEvent(
        name = "notifications channel",
        aliases = {"notification channel"},
        requires = Administrator.class,
        channel = Channel.GUILD
)
    public void execute(CommandContext ctx) throws Exception {
        // Usage
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("notifications channel")
                    .addUsages(new Usage()
                            .setUsage("notification channel <channel>")
                            .setEmoji("\uD83D\uDCC1")
                            .setDescription("Set the channel the notifications will go"))
                    .send();
            return;
        }


        final MongoGuild db = new MongoGuild(ctx.getGuild()); //Get database
        // Get mentioned text channel
        final TextChannel channel = Utilities.getUtils().getTextChannel(ctx.getEvent(), ctx.getArguments()[0], "notification channel", "\uD83D\uDD14");
        if (channel == null) return;

        final String currentChannelId = db.getNested("notifications").getString("channel"); // Get current notification channel
        // Remove notification channel
        if (currentChannelId.equals(channel.getId())) {
            db.getNested("notifications").setString("channel", "not set"); // Remove channel
            // Success
            new Success(ctx.getEvent())
                    .setCommand("notification channel")
                    .setEmoji("\uD83D\uDD14")
                    .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                    .setMessage("Notifications are no longer send in " + channel.getAsMention())
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

            success.setMessage("Notifications are now send in " + channel.getAsMention()).send();
            success.setMessage("Media notifications are now send in here").setChannel(channel).send();
        }

    }
}
