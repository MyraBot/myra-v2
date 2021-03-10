package com.myra.dev.marian.commands.administrator.notifications;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.database.managers.NotificationsYoutubeManager;
import com.myra.dev.marian.utilities.APIs.youTube.Channel;
import com.myra.dev.marian.utilities.APIs.youTube.YouTube;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.EmbedBuilder;

import java.net.URL;

@CommandSubscribe(
        name = "notification youtube",
        aliases = {"notifications youtube", "notification youtuber", "notifications youtuber"},
        requires = Administrator.class,
        channel = com.github.m5rian.jdaCommandHandler.Channel.GUILD
)
public class YouTuber implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("notification youtube")
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "notifications youtube <youtube channel>`", "\\\uD83D\uDCFA │ Add and remove auto notifications for YouTube", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }

        final String channel = Utilities.getUtils().getString(ctx.getArguments()); // Get the arguments as one string

        Channel c;
        // Get channel by url
        try {
            new URL(channel); // Try making a url out of it
            c = YouTube.getApi().getChannel(channel.split("/")[4]); // Get channel information
        }
        // Get channel by name
        catch (Exception e) {
            c = YouTube.getApi().searchChannelByName(channel).get(0); // Get channel information
        }

        // Remove youtuber
        if (NotificationsYoutubeManager.getInstance().getYoutubers(ctx.getGuild()).contains(c.getId())) {
            NotificationsYoutubeManager.getInstance().removeYoutuber(c.getId(), ctx.getGuild()); // Remove youtuber from notifications list

            EmbedBuilder success = new EmbedBuilder()
                    .setAuthor("notification youtube", "https://www.youtube.com/channel/" + c.getId(), ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().blue)
                    .setThumbnail(c.getAvatar())
                    .setDescription("Removed **" + c.getChannelName() + "** from the notifications");
            ctx.getChannel().sendMessage(success.build()).queue(); // Send success message
        } else {
            NotificationsYoutubeManager.getInstance().addYoutuber(c.getId(), ctx.getGuild()); // Add youtuber to notifications list

            EmbedBuilder success = new EmbedBuilder()
                    .setAuthor("notification youtube", "https://www.youtube.com/channel/" + c.getId(), ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().blue)
                    .setThumbnail(c.getAvatar())
                    .setDescription("Added **" + c.getChannelName() + "** to the notifications");
            ctx.getChannel().sendMessage(success.build()).queue(); // Send success message
        }
    }
}
