package com.myra.dev.marian.management;

import com.myra.dev.marian.Config;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static com.myra.dev.marian.utilities.language.Lang.lang;

public class ErrorCatch {
    public void catchError(Exception exception, MessageReceivedEvent event) {
        if (event.isFromGuild()) {
            final TextChannel channel = event.getTextChannel();

            if (!event.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE)) return;
            if (!event.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_EMBED_LINKS)) return;
        }

        error(event);
        exception.printStackTrace();
    }

    private void error(MessageReceivedEvent event) {
        event.getChannel().sendMessage(new EmbedBuilder()
                .setAuthor("error", "https://discord.gg/nG4uKuB", event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setColor(Utilities.red)
                .setDescription(lang(event).get("error")
                        .replace("{$marian.profile.url}", Config.DISCORD_PROFILE_URL + Config.MARIAN_ID)
                        .replace("{$support.invite}", Config.MARIANS_DISCORD_INVITE))
                .build())
                .queue();
    }
}
