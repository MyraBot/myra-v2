package com.github.m5rian.myra.commands.administrator.welcome;


import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import com.github.m5rian.myra.database.guild.MongoGuild;
import net.dv8tion.jda.api.entities.TextChannel;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

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
                            .setDescription(Lang.lang(ctx).get("description.welcome.channel")))
                    .send();
            return;
        }

        // Get provided channel
        final TextChannel channel = Utilities.getTextChannel(ctx.getEvent(), ctx.getArguments()[0], "welcome channel", "\uD83D\uDCC1");
        if (channel == null) return;

        MongoGuild db = MongoGuild.get(ctx.getGuild()); // Get database
        final String currentChannelId = db.getNested("welcome").getString("welcomeChannel"); // Get current welcome channel
        // Success
        Success success = new Success(ctx.getEvent())
                .setCommand("welcome channel")
                .setEmoji("\uD83D\uDCC1");
        //remove welcome channel
        if (currentChannelId.equals(channel.getId())) {
            db.getNested("welcome").setString("welcomeChannel", "not set"); // Remove channel id
            success.setMessage(Lang.lang(ctx).get("command.welcome.channel.removed").replace("{$channel}", channel.getAsMention())).send(); // Success message
        } else {
            db.getNested("welcome").setString("welcomeChannel", channel.getId()); // Update database
            // Success message
            success.setMessage(Lang.lang(ctx).get("command.welcome.channel.added").replace("{$channel}", channel.getAsMention())).send();
            success.setMessage(Lang.lang(ctx).get("command.welcome.channel.addedActive")).setChannel(channel).send();
        }
    }
}
