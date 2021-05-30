package com.github.m5rian.myra.commands.member.help;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Webhook;
import com.github.m5rian.myra.utilities.language.Lang;

public class Feature implements CommandHandler {
    private final String webhookUrl = "https://discord.com/api/v6/webhooks/788769270384558120/A_6jJ1gstVcqih6lD8pTIAereQBhTJRn9vtbljqevVQ4uiOXAEXPTWZBh6n99ZJJrwPd";


    @CommandEvent(
            name = "feature",
            aliases = {"submit"}
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
        final Webhook report = new Webhook(webhookUrl); // Set webhook
        report.setUsername(ctx.getAuthor().getAsTag()); // Set webhook name
        report.setAvatarUrl(ctx.getAuthor().getEffectiveAvatarUrl()); // Set webhook profile picture

        Webhook.EmbedObject bug = new Webhook.EmbedObject() // Create JSON embed
                .setDescription(ctx.getArgumentsRaw()); // Add bug description to JSON embed

        // Attachment is given
        if (!ctx.getEvent().getMessage().getAttachments().isEmpty()) {
            bug.setImage(ctx.getEvent().getMessage().getAttachments().get(0).getUrl()); // Add image to JSON embed
        }

        report.addEmbed(bug); // Add the JSON embed to webhook
        report.send(); // Send feature submit as a webhook

        ctx.getEvent().getJDA().getGuildById(Config.MARIAN_SERVER_ID).retrieveWebhooks().queue(webhooks -> webhooks.forEach(webhook -> { // Go through every webhook
            if (webhook.getUrl().equals(webhookUrl)) { // Webhook is the feature submit webhook
                final String messageId = webhook.getChannel().getLatestMessageId(); // Get latest message id
                webhook.getChannel().retrieveMessageById(messageId).queue(message -> { // Retrieve feature suggestion
                    // Add reactions
                    message.addReaction("\uD83D\uDC4D").queue(); // 👍
                    message.addReaction("\uD83D\uDC4E").queue(); // 👎
                });
            }
        }));

        // Success information
        new Success(ctx.getEvent())
                .setCommand("feature")
                .setEmoji("\uD83D\uDCCC")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setMessage(Lang.lang(ctx).get("command.help.feature.success"))
                .send();
    }
}
