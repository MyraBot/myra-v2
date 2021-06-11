package com.github.m5rian.myra.commands.member.help;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.Webhook;
import com.github.m5rian.myra.utilities.language.Lang;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Feature implements CommandHandler {
    @CommandEvent(
            name = "feature",
            aliases = {"submit"},
            emoji = "\uD83D\uDCCC",
            description = "description.help.feature",
            args = {"<feature description>"}
    )
    public void execute(final CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("format")
                    .addUsages(new Usage()
                            .setUsage("feature <feature description>")
                            .setEmoji("\uD83D\uDCCC")
                            .setDescription(Lang.lang(ctx).get("description.general.feature")))
                    .addInformation(Lang.lang(ctx).get("command.help.feature.info"))
                    .send();
            return;
        }

        // Feature submit
        final Webhook report = new Webhook(Config.MYRA_FEATURE_WEBHOOK) // Set webhook
                .setUsername(ctx.getAuthor().getAsTag()) // Set webhook name
                .setAvatarUrl(ctx.getAuthor().getEffectiveAvatarUrl()); // Set webhook profile picture

        Webhook.EmbedObject bug = new Webhook.EmbedObject() // Create JSON embed
                .setDescription(ctx.getArgumentsRaw()) // Add bug description to JSON embed
                .setColor(Color.decode(String.valueOf(Utilities.blue)));

        // Attachment is given
        if (!ctx.getEvent().getMessage().getAttachments().isEmpty()) {
            bug.setImage(ctx.getEvent().getMessage().getAttachments().get(0).getUrl()); // Add image to JSON embed
        }

        report.addEmbed(bug); // Add the JSON embed to webhook
        report.send(); // Send feature submit as a webhook

        ctx.getEvent().getJDA().getGuildById(Config.MARIAN_SERVER_ID).retrieveWebhooks().queue(webhooks -> webhooks.forEach(webhook -> { // Go through every webhook
            if (webhook.getUrl().equals(Config.MYRA_FEATURE_WEBHOOK)) { // Webhook is the feature submit webhook
                System.out.println("found a matching url");
                final String messageId = webhook.getChannel().getLatestMessageId(); // Get latest message id
                webhook.getChannel().retrieveMessageById(messageId).queueAfter(5, TimeUnit.SECONDS, message -> { // Retrieve feature suggestion
                    // Add reactions
                    message.addReaction("\uD83D\uDC4D").queue(); // 👍
                    message.addReaction("\uD83D\uDC4E").queue(); // 👎
                }, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
            }
        }));

        // Success information
        info(ctx).setDescription(Lang.lang(ctx).get("command.help.feature.success")).send();
    }
}
