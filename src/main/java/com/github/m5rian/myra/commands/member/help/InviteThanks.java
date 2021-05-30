package com.github.m5rian.myra.commands.member.help;


import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.language.Lang;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class InviteThanks {

    public void guildJoinEvent(GuildJoinEvent event) throws Exception {
        event.getGuild().retrieveOwner().queue(owner -> {
            owner.getUser().openPrivateChannel().queue(privateChannel -> {
                // Create embed
                final Success thank = new Success(null)
                        .setCommand("Hello!")
                        .setAvatar(event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                        .setThumbnail(event.getGuild().getIconUrl())
                        .setMessage(Lang.lang(event.getGuild()).get("listener.inviteThanks")
                                .replace("{$url}", Config.MARIANS_DISCORD_INVITE));
                privateChannel.sendMessage(thank.getEmbed().build()).queue(null, Throwable::printStackTrace);
            });
        });

    }
}
