package com.myra.dev.marian.listeners.suggestions;

import com.myra.dev.marian.database.guild.MongoGuild;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class SuggestionsChannel implements CommandHandler {

@CommandEvent(
        name = "suggestions channel",
        requires = Administrator.class
)
    public void execute(CommandContext ctx) throws Exception {
        // get utilities
        Utilities utilities = Utilities.getUtils();
        // Usage
        if (ctx.getArguments().length != 1) {
            // Usage
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("suggestions", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "suggestions channel <channel>`", "\uD83D\uDCC1 │ Set the channel in which the suggestions should go", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }

        //connect to database
        MongoGuild db = new MongoGuild(ctx.getGuild());
        // Get given channel
        TextChannel channel = utilities.getTextChannel(ctx.getEvent(), ctx.getArguments()[0], "suggestions", "\uD83D\uDDF3");
        if (channel == null) return;
        // Success
        Success success = new Success(ctx.getEvent())
                .setCommand("suggestions")
                .setEmoji("\uD83D\uDDF3")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl());
        //remove suggestions channel
        if (db.getString("suggestionsChannel").equals(channel.getId())) {
            db.setString("suggestionsChannel", "not set"); // Update database
            success.setMessage("Suggestions are no longer sent in " + ctx.getGuild().getTextChannelById(db.getString("suggestionsChannel")).getAsMention()).send(); // Success
        } else {
            db.setString("suggestionsChannel", channel.getId()); // Update database
            // Success messages
            success.setMessage("Suggestions are now sent in " + channel.getAsMention()).send();
            success.setMessage("Suggestions are now send in here").setChannel(channel).send();
        }
    }
}
