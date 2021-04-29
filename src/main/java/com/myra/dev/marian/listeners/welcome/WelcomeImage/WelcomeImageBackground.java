package com.myra.dev.marian.listeners.welcome.WelcomeImage;

import com.myra.dev.marian.database.guild.MongoGuild;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

public class WelcomeImageBackground implements CommandHandler {

@CommandEvent(
        name = "welcome image background",
        aliases = {"welcome image image"},
        requires = Administrator.class
)
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        // Usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("welcome image background", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "welcome image background <url>`", "\uD83D\uDDBC │ Change the background of the welcome images", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
        /**
         * set background image
         */
        //invalid url
        try {
            ImageIO.read(new URL(ctx.getArguments()[0]));
        } catch (IOException e) {
            new Error(ctx.getEvent())
                    .setCommand("welcome image background")
                    .setEmoji("\uD83D\uDDBC")
                    .setMessage("Invalid background URL")
                    .send();
            return;
        }
        //save in database
        new MongoGuild(ctx.getGuild()).getNested("welcome").setString("welcomeImageBackground", ctx.getArguments()[0]);
        //success
        Success success = new Success(ctx.getEvent())
                .setCommand("welcome image background")
                .setEmoji("\uD83D\uDDBC")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setMessage("The background has been changed to:")
                .setImage(ctx.getArguments()[0]);
        success.send();
    }
}
