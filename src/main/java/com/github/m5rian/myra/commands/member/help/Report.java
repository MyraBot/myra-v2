package com.github.m5rian.myra.commands.member.help;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.Webhook;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class Report implements CommandHandler {

    @CommandEvent(
            name = "report",
            aliases = {"bug"},
            emoji = "\uD83D\uDC1B",
            description = "description.help.report",
            args = "<bug description>"
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
        final Webhook report = new Webhook(Config.MYRA_BUG_WEBHOOK) // Set webhook
                .setUsername(ctx.getAuthor().getAsTag()) // Set webhook name
                .setAvatarUrl(ctx.getAuthor().getEffectiveAvatarUrl()); // Set webhook profile picture

        Webhook.EmbedObject bug = new Webhook.EmbedObject() // Create JSON embed
                .setDescription(ctx.getArgumentsRaw()) // Add bug description to JSON embed
                .setColor(Color.decode(String.valueOf(Utilities.red)));

        // Attachment is given
        if (!ctx.getEvent().getMessage().getAttachments().isEmpty()) {
            bug.setImage(ctx.getEvent().getMessage().getAttachments().get(0).getUrl()); // Add image to JSON embed
        }

        report.addEmbed(bug); // Add the JSON embed to webhook
        report.send(); // Send report as a webhook

        // Send success
        info(ctx).setDescription("Your bug report was successfully reported").send();
    }
}
