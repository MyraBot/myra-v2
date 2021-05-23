package com.myra.dev.marian.commands.help;


import com.myra.dev.marian.Config;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;

import static com.myra.dev.marian.utilities.language.Lang.lang;

public class InviteThanks {

    public void guildJoinEvent(GuildJoinEvent event) throws Exception {
        event.getGuild().retrieveOwner().queue(owner -> {
            owner.getUser().openPrivateChannel().queue(privateChannel -> {
                // Create embed
                final Success thank = new Success(null)
                        .setCommand("Hello!")
                        .setAvatar(event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                        .setThumbnail(event.getGuild().getIconUrl())
                        .setMessage(lang(event.getGuild()).get("listener.inviteThanks")
                                .replace("{$url}", Config.MARIANS_DISCORD_INVITE));
                privateChannel.sendMessage(thank.getEmbed().build()).queue(null, Throwable::printStackTrace);
            });
        });

    }
}
