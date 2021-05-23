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

        String hex;
        //remove #
        if (ctx.getArguments()[0].startsWith("#")) {
            StringBuilder raw = new StringBuilder(ctx.getArguments()[0]);
            raw.deleteCharAt(0);
            hex = "0x" + raw;
        }
        //add 0x
        else {
            hex = "0x" + ctx.getArguments()[0];
        }
        //if colour doesn't exist
        try {
            Color color = Color.decode(hex);
            hex = Integer.toHexString(color.getRGB()).substring(2);
        } catch (Exception e) {
            new Error(ctx.getEvent())
                    .setCommand("welcome colour")
                    .setEmoji("\uD83C\uDFA8")
                    .setMessage(lang(ctx).get("error.invalidColour"))
                    .send();
            return;
        }

        new MongoGuild(ctx.getGuild()).getNested("welcome").setInteger("welcomeColour", Integer.parseInt(hex)); // Save in database
        //success
        new Success(ctx.getEvent())
                .setCommand("welcome colour")
                .setEmoji("\uD83C\uDFA8")
                .setMessage(lang(ctx).get("command.welcome.colour.done")
                        .replace("{$colour}", hex.replace("0x", "#"))) // New colour
                .send();
    }
}