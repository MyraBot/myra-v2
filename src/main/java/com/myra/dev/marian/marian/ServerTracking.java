package com.myra.dev.marian.marian;


import com.myra.dev.marian.Config;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;

public class ServerTracking {

    public void onGuildJoin(GuildJoinEvent event) {
        EmbedBuilder server = new EmbedBuilder()
                .setTitle("I joined " + event.getGuild().getName())
                .setColor(Utilities.getUtils().blue)
                .setThumbnail(event.getGuild().getIconUrl())
                .setTimestamp(event.getGuild().getMember(event.getJDA().getSelfUser()).getTimeJoined().toInstant());

        event.getGuild().retrieveOwner().queue(
                owner -> {
                    server.addField("\uD83D\uDC51 │ owner ", owner.getUser().getAsTag(), true)
                            .addField("\uD83C\uDF9F │ guild id ", event.getGuild().getId(), true)
                            .addField("\uD83E\uDDEE │ member count", Integer.toString(event.getGuild().getMemberCount()), true);
                    event.getJDA().getGuildById(Config.MARIAN_SERVER_ID).getTextChannelById("788448343927160852").sendMessage(server.build()).queue();
                }, error -> {
                    server.addField("\uD83D\uDC51 │ owner ", "unknown", true)
                            .addField("\uD83C\uDF9F │ guild id ", event.getGuild().getId(), true)
                            .addField("\uD83E\uDDEE │ member count", Integer.toString(event.getGuild().getMemberCount()), true);

                    event.getJDA().getGuildById(Config.MARIAN_SERVER_ID).getTextChannelById("788448343927160852").sendMessage(server.build()).queue();
                });
    }

    public void onGuildLeave(GuildLeaveEvent event) {
        EmbedBuilder server = new EmbedBuilder()
                .setTitle("I got kicked out of " + event.getGuild().getName())
                .setColor(Utilities.getUtils().red)
                .setThumbnail(event.getGuild().getIconUrl())
                .setTimestamp(event.getGuild().getMember(event.getJDA().getSelfUser()).getTimeJoined().toInstant());

        event.getGuild().retrieveOwner().queue(
                owner -> {
                    server.addField("\uD83D\uDC51 │ owner ", owner.getUser().getAsTag(), true)
                            .addField("\uD83C\uDF9F │ guild id ", event.getGuild().getId(), true)
                            .addField("\uD83E\uDDEE │ member count", Integer.toString(event.getGuild().getMemberCount()), true);
                    event.getJDA().getGuildById(Config.MARIAN_SERVER_ID).getTextChannelById("788448343927160852").sendMessage(server.build()).queue();
                }, error -> {
                    server.addField("\uD83D\uDC51 │ owner ", "unknown", true)
                            .addField("\uD83C\uDF9F │ guild id ", event.getGuild().getId(), true)
                            .addField("\uD83E\uDDEE │ member count", Integer.toString(event.getGuild().getMemberCount()), true);
                    event.getJDA().getGuildById(Config.MARIAN_SERVER_ID).getTextChannelById("788448343927160852").sendMessage(server.build()).queue();
                });
    }
}
