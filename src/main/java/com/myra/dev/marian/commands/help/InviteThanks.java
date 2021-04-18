package com.myra.dev.marian.commands.help;


import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;

public class InviteThanks {

    public void guildJoinEvent(GuildJoinEvent event) throws Exception {
        event.getGuild().retrieveOwner().queue(owner -> {
            owner.getUser().openPrivateChannel().queue(privateChannel -> {
                // Create embed
                final EmbedBuilder thank = new EmbedBuilder()
                        .setAuthor("Thank you for inviting me", null, event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                        .setColor(Utilities.getUtils().blue)
                        .setDescription("Thank you for inviting me to " + event.getGuild().getName() + ". I'm still in developing, so if you find any bugs please report it! For suggestions you can also join the " + Utilities.getUtils().hyperlink("support server", "https://discord.gg/nG4uKuB") + ".");
                privateChannel.sendMessage(thank.build()).queue();
            });
        });

    }
}
