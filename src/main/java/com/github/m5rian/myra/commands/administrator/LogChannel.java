package com.github.m5rian.myra.commands.administrator;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import net.dv8tion.jda.api.entities.TextChannel;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class LogChannel implements CommandHandler {

    @CommandEvent(
            name = "log channel",
            aliases = {"logging channel", "logs channel"},
            args = {"<channel>"},
            emoji = "\uD83E\uDDFE",
            description = "description.logChannel",
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("log channel")
                    .addUsages(new Usage()
                            .setUsage("log channel <channel>")
                            .setEmoji("\uD83E\uDDFE")
                            .setDescription(lang(ctx).get("description.logChannel")))
                    .send();
            return;
        }

        // Get provided text channel
        final TextChannel channel = Utilities.getTextChannel(ctx.getEvent(), ctx.getArguments()[0], "log channel", "\uD83E\uDDFE"); // Get channel
        if (channel == null) return;

        final MongoGuild db = MongoGuild.get(ctx.getGuild()); // Get database
        // Remove log channel
        if (channel.getId().equals(db.getString("logChannel"))) {
            db.setString("logChannel", "not set"); // Update database
            // Send success message
            new Success(ctx.getEvent())
                    .setCommand("log channel")
                    .setEmoji("\uD83E\uDDFE")
                    .setMessage(lang(ctx).get("command.logChannel.info.removed")
                            .replace("{$channel}", channel.getAsMention())) // Old log channel
                    .send();
        }
        // Change log channel
        else {
            db.setString("logChannel", channel.getId()); // Update database
            // Success message
            final Success success = new Success(ctx.getEvent())
                    .setCommand("log channel")
                    .setEmoji("\uD83E\uDDFE");

            success.setMessage(lang(ctx).get("command.logChannel.info.changed")
                    .replace("{$channel}", channel.getAsMention())) // New log channel
                    .send(); // Send success in current channel
            // Success message in the new log channel
            success.setMessage(lang(ctx).get("command.logChannel.info.changedActive")
                    .replace("{$channel}", channel.getAsMention())) // New log channel
                    .setChannel(channel)
                    .send(); // Send success in new log channel
        }
    }
}