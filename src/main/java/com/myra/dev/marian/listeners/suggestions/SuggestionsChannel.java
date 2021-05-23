package com.myra.dev.marian.listeners.suggestions;

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

public class SuggestionsChannel implements CommandHandler {

    @CommandEvent(
            name = "suggestions channel",
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 1) {
            // Command usage
            new CommandUsage(ctx.getEvent())
                    .setCommand("welcome")
                    .addUsages(new Usage().setUsage("suggestions channel <channel>")
                            .setEmoji("\uD83D\uDCC1")
                            .setDescription(lang(ctx).get("description.suggestions.channel")))
                    .send();
            return;
        }

        //connect to database
        MongoGuild db = new MongoGuild(ctx.getGuild());
        // Get given channel
        TextChannel channel = Utilities.getTextChannel(ctx.getEvent(), ctx.getArguments()[0], "suggestions", "\uD83D\uDDF3");
        if (channel == null) return;
        // Success
        Success success = new Success(ctx.getEvent())
                .setCommand("suggestions")
                .setEmoji("\uD83D\uDDF3")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl());
        //remove suggestions channel
        if (db.getString("suggestionsChannel").equals(channel.getId())) {
            db.setString("suggestionsChannel", "not set"); // Update database
            success.setMessage(lang(ctx).get("command.suggestions.channel.removed")).send(); // Success
        } else {
            db.setString("suggestionsChannel", channel.getId()); // Update database
            // Success messages
            success.setMessage(lang(ctx).get("command.suggestions.channel.changed")
                    .replace("{$channel.mention}", channel.getAsMention()))
                    .send();
            success.setMessage(lang(ctx).get("command.suggestions.channel.changedActive")).setChannel(channel).send();
        }
    }
}
