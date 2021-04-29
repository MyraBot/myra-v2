package com.myra.dev.marian.listeners.suggestions;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;

public class SubmitSuggestion implements CommandHandler {

@CommandEvent(
        name = "suggest"
)
    public void execute(CommandContext ctx) throws Exception {
        // Get database
        MongoGuild db = new MongoGuild(ctx.getGuild());
        //check if feature is disabled
        if (!db.getListenerManager().check("suggestions")) return;
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        // Usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("suggest", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "suggest <suggestion>`", "\uD83D\uDDF3 │ Suggest something", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
        /**
         * Submit suggestion
         */
        //if no channel is set
        if (db.getString("suggestionsChannel").equals("not set")) {
            new Error(ctx.getEvent())
                    .setCommand("suggestions")
                    .setEmoji("\uD83D\uDCA1")
                    .setMessage("No suggestion channel specified")
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
                        .setAuthor("suggestion by " + ctx.getAuthor().getAsTag(), ctx.getEvent().getMessage().getJumpUrl(), ctx.getGuild().getIconUrl())
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
