package com.myra.dev.marian.management;

import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ErrorCatch {
    public void catchError(Exception exception, MessageReceivedEvent event) {
        if (event.isFromGuild()) {
            final TextChannel channel = event.getTextChannel();

            if (!event.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE)) return;
            if (!event.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_EMBED_LINKS)) return;
        }

        error(event, "An error accrued, please contact " + Utilities.getUtils().hyperlink("my developer", Utilities.getUtils().marianUrl()));
        exception.printStackTrace();
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
