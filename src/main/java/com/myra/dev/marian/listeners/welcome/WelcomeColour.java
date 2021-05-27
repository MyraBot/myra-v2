package com.myra.dev.marian.listeners.welcome;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.permissions.Administrator;

import java.awt.*;

import static com.myra.dev.marian.utilities.language.Lang.lang;

public class WelcomeColour implements CommandHandler {

    @CommandEvent(
            name = "welcome colour",
            aliases = {"welcome color"},
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("welcome colour")
                    .addUsages(new Usage()
                            .setUsage("welcome colour <hex colour>")
                            .setEmoji("\uD83C\uDFA8")
                            .setDescription(lang(ctx).get("description.welcome.colour")))
                    .send();
            return;
        }

        // Try to decode colour
        try {
            final Color color = Color.decode(ctx.getArguments()[0]); // try to decode colour
            final String hex = Integer.toHexString(color.getRGB()).substring(2); // Get hex from colour

            new MongoGuild(ctx.getGuild()).getNested("welcome").setString("welcomeColour", hex); // Save in database
            //success
            new Success(ctx.getEvent())
                    .setCommand("welcome colour")
                    .setEmoji("\uD83C\uDFA8")
                    .setMessage(lang(ctx).get("command.welcome.colour.done")
                            .replace("{$colour}", hex)) // New colour
                    .send();
        }
        // Colour input isn't a valid colour
        catch (Exception e) {
            new Error(ctx.getEvent())
                    .setCommand("welcome colour")
                    .setEmoji("\uD83C\uDFA8")
                    .setMessage(lang(ctx).get("error.invalidColour"))
                    .send();
        }
    }
}