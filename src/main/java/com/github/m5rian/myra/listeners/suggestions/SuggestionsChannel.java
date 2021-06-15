package com.github.m5rian.myra.listeners.suggestions;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
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
                            .setDescription(Lang.lang(ctx).get("description.suggestions.channel")))
                    .send();
            return;
        }

        //connect to database
        MongoGuild db = MongoGuild.get(ctx.getGuild());
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
            success.setMessage(Lang.lang(ctx).get("command.suggestions.channel.removed")).send(); // Success
        } else {
            db.setString("suggestionsChannel", channel.getId()); // Update database
            // Success messages
            success.setMessage(Lang.lang(ctx).get("command.suggestions.channel.changed")
                    .replace("{$channel.mention}", channel.getAsMention()))
                    .send();
            success.setMessage(Lang.lang(ctx).get("command.suggestions.channel.changedActive")).setChannel(channel).send();
        }
    }
}
