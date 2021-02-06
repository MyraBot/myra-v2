package com.myra.dev.marian.commands.administrator;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.myra.dev.marian.database.allMethods.Database;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.permissions.Administrator;

@CommandSubscribe(
        name = "music voting",
        aliases = {"music vote"},
        requires = Administrator.class,
        channel = Channel.GUILD
)
public class MusicVotingToggle implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return;

        final Database db = new Database(ctx.getGuild()); // Get database
        final boolean value = !db.getBoolean("musicVoting"); // Get new value
        db.setBoolean("musicVoting", value); // Update database

        Success success = new Success(ctx.getEvent())
                .setCommand("music voting toggle")
                .setEmoji("\uD83D\uDDF3")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl());
        if (value) success.setMessage("Music voting is now turned `on`");
        else success.setMessage("Music voting is now turned `off`");
        success.send(); // Send Message
    }
}
