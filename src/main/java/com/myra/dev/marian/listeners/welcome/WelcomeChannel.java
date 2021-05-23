package com.myra.dev.marian.listeners.welcome;


import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.entities.TextChannel;

import static com.myra.dev.marian.utilities.language.Lang.lang;

public class WelcomeChannel implements CommandHandler {

    @CommandEvent(
            name = "welcome channel",
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("welcome channel")
                    .addUsages(new Usage()
                            .setUsage("welcome channel <channel>")
                            .setEmoji("\uD83D\uDCC1")
                            .setDescription(lang(ctx).get("description.welcome.channel")))
                    .send();
            return;
        }

        // Get provided channel
        final TextChannel channel = Utilities.getTextChannel(ctx.getEvent(), ctx.getArguments()[0], "welcome channel", "\uD83D\uDCC1");
        if (channel == null) return;

        MongoGuild db = new MongoGuild(ctx.getGuild()); // Get database
        final String currentChannelId = db.getNested("welcome").getString("welcomeChannel"); // Get current welcome channel
        // Success
        Success success = new Success(ctx.getEvent())
                .setCommand("welcome channel")
                .setEmoji("\uD83D\uDCC1");
        //remove welcome channel
        if (currentChannelId.equals(channel.getId())) {
            db.getNested("welcome").setString("welcomeChannel", "not set"); // Remove channel id
            success.setMessage(lang(ctx).get("command.welcome.channel.removed").replace("{$channel}", channel.getAsMention())).send(); // Success message
        } else {
            db.getNested("welcome").setString("welcomeChannel", channel.getId()); // Update database
            // Success message
            success.setMessage(lang(ctx).get("command.welcome.channel.added").replace("{$channel}", channel.getAsMention())).send();
            success.setMessage(lang(ctx).get("command.welcome.channel.addedActive")).setChannel(channel).send();
        }
    }
}
