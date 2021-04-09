package com.myra.dev.marian.listeners.welcome;

import com.myra.dev.marian.database.guild.MongoGuild;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

@CommandSubscribe(
        name = "welcome colour",
        aliases = {"welcome color"},
        requires = Administrator.class
)
public class WelcomeColour implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        // Usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder welcomeChannelUsage = new EmbedBuilder()
                    .setAuthor("welcome colour", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "welcome colour <hex colour>`", "\uD83C\uDFA8 │ Set the colour of the embeds", false);
            ctx.getChannel().sendMessage(welcomeChannelUsage.build()).queue();
            return;
        }
        String hex = null;
        //remove #
        if (ctx.getArguments()[0].startsWith("#")) {
            StringBuilder raw = new StringBuilder(ctx.getArguments()[0]);
            raw.deleteCharAt(0);
            hex = "0x" + raw.toString();
        }
        //add 0x
        else {
            hex = "0x" + ctx.getArguments()[0];
        }
        //if colour doesn't exist
        try {
            Color.decode(hex);
        } catch (Exception e) {
            new Error(ctx.getEvent())
                    .setCommand("welcome embed colour")
                    .setEmoji("\uD83C\uDFA8")
                    .setMessage("Invalid colour")
                    .send();
            return;
        }
        //save in database
        new MongoGuild(ctx.getGuild()).getNested("welcome").setInteger("welcomeColour", Integer.parseInt(hex));
        //success
        Success success = new Success(ctx.getEvent())
                .setCommand("welcome embed colour")
                .setEmoji("\uD83C\uDFA8")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setMessage("Colour changed to `" + hex.replace("0x", "#") + "`");
        success.send();
    }
}