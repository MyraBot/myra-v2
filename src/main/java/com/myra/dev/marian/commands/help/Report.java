package com.myra.dev.marian.commands.help;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.Webhook;
import net.dv8tion.jda.api.EmbedBuilder;

public class Report implements CommandHandler {
    private final String webhookUrl = "https://discord.com/api/v6/webhooks/788764863106252800/ZN7j5NCIEtekAxyKXJ55BUp8UqLmvsUuGAh2-Dlsndul0ziuxxyxpGiDtVBmsLd_beBF";

    
@CommandEvent(
        name = "report",
        aliases = {"bug"}
)
    public void execute(final CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("report", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.gray)
                    .addField("`" + ctx.getPrefix() + "report <bug>`", "\uD83D\uDC1B │ Report a bug you found", false)
                    .setFooter("You can also add attachments");
            ctx.getChannel().sendMessage(usage.build()).queue(); // Send usage
            return;
        }

        // Bug report
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
        report.send(); // Send report as a webhook

        Success success = new Success(ctx.getEvent())
                .setCommand("report")
                .setEmoji("\uD83D\uDC1B")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setMessage("Your bug report was successfully reported");
        success.send();
    }
}
