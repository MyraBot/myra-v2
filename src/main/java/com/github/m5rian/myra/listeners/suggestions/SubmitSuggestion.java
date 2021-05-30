package com.github.m5rian.myra.listeners.suggestions;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.database.guild.MongoGuild;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class SubmitSuggestion implements CommandHandler {

    @CommandEvent(
            name = "suggest"
    )
    public void execute(CommandContext ctx) throws Exception {
        MongoGuild db = new MongoGuild(ctx.getGuild()); // Get database
        // Feature is disabled
        if (!db.getListenerManager().check("suggestions")) return;

        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("suggest")
                    .addUsages(new Usage()
                            .setUsage("suggest <suggestion>")
                            .setEmoji("\uD83D\uDDF3")
                            .setDescription(Lang.lang(ctx).get("description.suggest")))
                    .send();
            return;
        }

        //if no channel is set
        if (db.getString("suggestionsChannel").equals("not set")) {
            new Error(ctx.getEvent())
                    .setCommand("suggestions")
                    .setEmoji("\uD83D\uDCA1")
                    .setMessage(Lang.lang(ctx).get("command.suggest.error.noChannel"))
                    .send();
            return;
        }
        // Get suggestion
        String suggestion = "";
        for (int i = 0; i < ctx.getArguments().length; i++) {
            suggestion += ctx.getArguments()[i] + " ";
        }
        //remove last space
        suggestion = suggestion.substring(0, suggestion.length() - 1);
        //send suggestion
        ctx.getGuild().getTextChannelById(db.getString("suggestionsChannel")).sendMessage(
                new EmbedBuilder()
                        .setAuthor(ctx.getAuthor().getAsTag(), ctx.getEvent().getMessage().getJumpUrl(), ctx.getAuthor().getEffectiveAvatarUrl())
                        .setColor(ctx.getMember().getColor())
                        .setThumbnail(ctx.getAuthor().getEffectiveAvatarUrl())
                        .setDescription(suggestion)
                        .setTimestamp(Instant.now())
                        .build()
        ).queue((message) -> {
            //add reactions
            message.addReaction("\uD83D\uDC4D").queue();
            message.addReaction("\uD83D\uDC4E").queue();
        });
    }
}
