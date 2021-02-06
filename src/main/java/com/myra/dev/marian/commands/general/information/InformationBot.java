package com.myra.dev.marian.commands.general.information;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@CommandSubscribe(
        name = "information bot",
        aliases = {"info bot", "information BOT_NAME", "info BOT_NAME"}
)
public class InformationBot implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Get JDA
        final JDA jda = ctx.getEvent().getJDA();

        EmbedBuilder bot = new EmbedBuilder()
                .setAuthor(jda.getSelfUser().getName(), null, jda.getSelfUser().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setThumbnail(jda.getSelfUser().getEffectiveAvatarUrl())
                .addField("\uD83D\uDD0C │ name", jda.getSelfUser().getName(), true)
                .addField("\uD83D\uDC51 │ owner", jda.getUserById("639544573114187797").getAsTag(), true)
                .addBlankField(true)
                .addField("\uD83C\uDF10 │ servers", Integer.toString(jda.getGuilds().size()), true)
                .addField("\uD83D\uDCBB │ language", "Java", true)
                .addField("\uD83D\uDCC5 │ joined server", ctx.getGuild().getSelfMember().getTimeJoined().atZoneSameInstant(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy , hh:mm")), false)
                .setFooter("\uD83D\uDCC6 │ created at " + jda.getSelfUser().getTimeCreated().atZoneSameInstant(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy , hh:mm")));
        ctx.getChannel().sendMessage(bot.build()).queue();
    }
}