package com.github.m5rian.myra.commands.administrator.leveling;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import static com.github.m5rian.myra.utilities.language.Lang.*;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import net.dv8tion.jda.api.entities.TextChannel;

public class LevelingChannel implements CommandHandler {

    @CommandEvent(
            name = "leveling channel",
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("leveling channel")
                    .addUsages(new Usage()
                            .setUsage("leveling channel <channel>")
                            .setEmoji("\uD83E\uDDFE")
                            .setDescription(lang(ctx).get("description.leveling.channel")))
                    .send();
            return;
        }

        // Get provided  text channel
        final TextChannel channel = Utilities.getTextChannel(ctx.getEvent(), ctx.getArguments()[0], "leveling channel", "\uD83E\uDDFE");
        if (channel == null) return;

        final MongoGuild db = MongoGuild.get(ctx.getGuild()); //  Get database
        final String channelId = db.getNested("leveling").getString("channel"); // Get leveling channel id

        Success success = new Success(ctx.getEvent())
                .setCommand("leveling channel")
                .setEmoji("\uD83E\uDDFE")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl());

        // Leveling channel got removed
        if (channelId.equals(channel.getId())) {
            db.getNested("leveling").setString("channel", "not set"); // Set leveling channel to `not set`
            success.setMessage(lang(ctx).get("command.leveling.channel.info.removed")).send(); // Send success message
        }
        // Leveling channel got changed
        else {
            db.getNested("leveling").setString("channel", channel.getId()); // Set leveling channel to the new channel
            success.setMessage(lang(ctx).get("command.leveling.channel.info.added")
                    .replace("{$channel}", channel.getAsMention())) // New channel
                    .send(); // Send success message
            success.setMessage(lang(ctx).get("command.leveling.channel.info.addedActive")).setChannel(channel).send(); // Send success message in new channel
        }
    }
}
