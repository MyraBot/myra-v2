package com.myra.dev.marian.listeners.welcome.WelcomeImage;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Administrator;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

import static com.myra.dev.marian.utilities.language.Lang.lang;

public class WelcomeImageBackground implements CommandHandler {

    @CommandEvent(
            name = "welcome image background",
            aliases = {"welcome image image"},
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("welcome image background")
                    .addUsages(new Usage()
                            .setUsage("welcome image background <url>")
                            .setEmoji("\uD83D\uDDBC")
                            .setDescription(lang(ctx).get("description.welcome.image.background")))
                    .send();
            return;
        }

        // Provided image isn't a url
        if (!Utilities.isValidURL(ctx.getArguments()[0])) {
            new Error(ctx.getEvent())
                    .setCommand("welcome image background")
                    .setEmoji("\uD83D\uDDBC")
                    .setMessage(lang(ctx).get("error.invalidUrl"))
                    .send();
            return;
        }
        // Check if url is an image
        try {
            ImageIO.read(new URL(ctx.getArguments()[0]));
        }
        // Url isn't an image
        catch (IOException e) {
            new Error(ctx.getEvent())
                    .setCommand("welcome image background")
                    .setEmoji("\uD83D\uDDBC")
                    .setMessage(lang(ctx).get("command.welcome.image.background.invalidImage"))
                    .send();
            return;
        }

        new MongoGuild(ctx.getGuild()).getNested("welcome").setString("welcomeImageBackground", ctx.getArguments()[0]); // Save new url in database

        new Success(ctx.getEvent())
                .setCommand("welcome image background")
                .setEmoji("\uD83D\uDDBC")
                .setMessage(lang(ctx).get("command.welcome.image.background.done"))
                .setImage(ctx.getArguments()[0])
                .send();
    }
}
