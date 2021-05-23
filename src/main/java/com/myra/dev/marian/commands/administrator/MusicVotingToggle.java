package com.myra.dev.marian.commands.administrator;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import static com.myra.dev.marian.utilities.language.Lang.*;
import com.myra.dev.marian.utilities.permissions.Administrator;

public class MusicVotingToggle implements CommandHandler {

    @CommandEvent(
            name = "music voting",
            aliases = {"music vote"},
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return;

        final MongoGuild db = new MongoGuild(ctx.getGuild()); // Get database
        final boolean value = !db.getBoolean("musicVoting"); // Get new value
        db.setBoolean("musicVoting", value); // Update database

        final Success success = new Success(ctx.getEvent())
                .setCommand("music voting toggle")
                .setEmoji("\uD83D\uDDF3");
        if (value) success.setMessage(lang(ctx).get("command.musicVoting.info").replace("{$on/off}", "on"));
        else success.setMessage(lang(ctx).get("command.musicVoting.info").replace("{$on/off}", "off"));
        success.send(); // Send Message
    }
}
