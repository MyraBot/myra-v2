package com.myra.dev.marian.management;

import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ErrorCatch {
    // Errors
    private final String missingPermsMESSAGE_WRITE = "Cannot perform action due to a lack of Permission. Missing permission: MESSAGE_WRITE";
    private final String missingPermsMESSAGE_EMBED = "Cannot perform action due to a lack of Permission. Missing permission: MESSAGE_EMBED_LINKS";
    private final String missingPermsVIEW_CHANNEL = "Cannot perform action due to a lack of Permission. Missing permission: VIEW_CHANNEL";

    public void catchError(Exception exception, MessageReceivedEvent event) {
        final String error = exception.getMessage(); // Get error
        if (exception.getMessage() == null) {
            exception.printStackTrace();
            return;
        }
        // Missing permissions: MESSAGE_WRITE
        if (error.startsWith(missingPermsMESSAGE_WRITE)) {
        }
        else if (error.startsWith(missingPermsMESSAGE_EMBED)) {

        }
        // Missing permissions: VIEW_CHANNEL
        else if (error.equals(missingPermsVIEW_CHANNEL)) {
            error(event, "I'm not able to see the channel."); // Send error}
        }
        // Other error
        else {
            error(event, "An error accrued, please contact " + Utilities.getUtils().hyperlink("my developer", Utilities.getUtils().marianUrl()));
            exception.printStackTrace();
        }
    }

    private void error(MessageReceivedEvent event, String error) {
        final Utilities utils = Utilities.getUtils(); // Get utilities

        event.getChannel().sendMessage(new EmbedBuilder()
                .setAuthor("error", "https://discord.gg/nG4uKuB", event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setColor(utils.red)
                .setDescription(error + "\n" + utils.hyperlink("If you need more help please join the support server", "https://discord.gg/nG4uKuB"))
                .build()
        ).queue();
    }
}
