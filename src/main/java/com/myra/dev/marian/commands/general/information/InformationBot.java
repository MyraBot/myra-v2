package com.myra.dev.marian.commands.general.information;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.Config;
import com.myra.dev.marian.utilities.APIs.TopGG;
import com.myra.dev.marian.utilities.Format;
import com.myra.dev.marian.utilities.Resources;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

public class InformationBot implements CommandHandler {

    @CommandEvent(
            name = "information bot",
            aliases = {"info bot", "information BOT_NAME", "info BOT_NAME"}
    )
    public void execute(CommandContext ctx) throws Exception {
        new Thread(() -> {
            try {
                ctx.getChannel().sendTyping().queue();

                final JDA jda = ctx.getEvent().getJDA();
                final long uptime = System.currentTimeMillis() - Config.startUp;
                final Resources resources = new Resources();

                final EmbedBuilder dashboard = new EmbedBuilder()
                        .setAuthor("dashboard", null, ctx.getAuthor().getEffectiveAvatarUrl())
                        .setColor(Utilities.getUtils().blue)
                        .setThumbnail(ctx.getEvent().getJDA().getSelfUser().getAvatarUrl())
                        .addField("\uD83D\uDDA5 │ Server",
                                "**CPU:** " + resources.getCpuLoad() + "\n" +
                                        "**RAM:** " + resources.getRAMUsage() + "mb"
                                , true)
                        .addField("\u23F1 │ Uptime", Format.toTime(uptime), true)
                        .addField("\uD83E\uDDF5 │ Threads", resources.getRunningThreads(), true)
                        .addField("\uD83D\uDDC2 │ Shards", String.valueOf(jda.getShardManager().getShardsTotal()), true)
                        .addField("\uD83D\uDDDC │ Guilds", String.valueOf(jda.getGuilds().size()), true)
                        .addField("\uD83D\uDC65 │ Members", String.valueOf(Utilities.getMemberCount(jda)), true)
                        .addField("\uD83D\uDC65 │ Users", String.valueOf(Utilities.getUserCount(jda)), true)
                        .addField("\uD83D\uDDF3 │ votes", TopGG.getInstance().getUpVotes(), true);
                ctx.getChannel().sendMessage(dashboard.build()).queue();
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }
}