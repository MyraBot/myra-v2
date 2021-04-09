package com.myra.dev.marian.listeners.welcome;


import com.myra.dev.marian.database.guild.MongoGuild;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

@CommandSubscribe(
        name = "welcome channel",
        requires = Administrator.class
)
public class WelcomeChannel implements Command {


    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        // Usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder welcomeChannelUsage = new EmbedBuilder()
                    .setAuthor("welcome channel", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "welcome channel <channel>`", "\uD83D\uDCC1 │ Set the channel, the welcome message will go", true);
            ctx.getChannel().sendMessage(welcomeChannelUsage.build()).queue();
            return;
        }
        /**
         * Change welcome channel
         */
        //get channel
        TextChannel channel = utilities.getTextChannel(ctx.getEvent(), ctx.getArguments()[0], "welcome channel", "\uD83D\uDCC1");
        if (channel == null) return;
        // Get database
        MongoGuild db = new MongoGuild(ctx.getGuild());
        // Get current welcome channel
        String currentChannelId = db.getNested("welcome").getString("welcomeChannel");
        // Success
        Success success = new Success(ctx.getEvent())
                .setCommand("welcome channel")
                .setEmoji("\uD83D\uDCC1")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl());
        //remove welcome channel
        if (currentChannelId.equals(channel.getId())) {
            db.getNested("welcome").setString("welcomeChannel", "not set"); // Remove channel id
            success.setMessage("Welcome are no longer send in " + channel.getAsMention()).send(); // Success message
        } else {
            db.getNested("welcome").setString("welcomeChannel", channel.getId()); // Update database
            // Success message
            success.setMessage("Welcome messages are now send in " + channel.getAsMention()).send();
            success.setMessage("Welcome actions are now send in here").setChannel(channel).send();
        }
    }
}
