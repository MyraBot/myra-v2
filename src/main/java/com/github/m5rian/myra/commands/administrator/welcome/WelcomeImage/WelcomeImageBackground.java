package com.github.m5rian.myra.commands.administrator.welcome.WelcomeImage;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import com.github.m5rian.myra.database.guild.MongoGuild;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

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
                            .setDescription(Lang.lang(ctx).get("description.welcome.image.background")))
                    .send();
            return;
        }

        // Provided image isn't a url
        if (!Utilities.isValidURL(ctx.getArguments()[0])) {
            new Error(ctx.getEvent())
                    .setCommand("welcome image background")
                    .setEmoji("\uD83D\uDDBC")
                    .setMessage(Lang.lang(ctx).get("error.invalidUrl"))
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
                    .setMessage(Lang.lang(ctx).get("command.welcome.image.background.invalidImage"))
                    .send();
            return;
        }

        MongoGuild.get(ctx.getGuild()).getNested("welcome").setString("welcomeImageBackground", ctx.getArguments()[0]); // Save new url in database

        new Success(ctx.getEvent())
                .setCommand("welcome image background")
                .setEmoji("\uD83D\uDDBC")
                .setMessage(Lang.lang(ctx).get("command.welcome.image.background.done"))
                .setImage(ctx.getArguments()[0])
                .send();
    }
}
