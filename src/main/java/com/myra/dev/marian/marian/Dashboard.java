package com.myra.dev.marian.marian;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.Format;
import com.myra.dev.marian.utilities.Resources;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Marian;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

@CommandSubscribe(
        name = "dashboard",
        aliases = {"dash"},
        requires = Marian.class
)
public class Dashboard implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        int count = ctx.getEvent().getJDA().getGuilds().stream().mapToInt(Guild::getMemberCount).sum();
        final int memberCount = ctx.getEvent().getJDA().getUsers().size();
        final long uptime = System.currentTimeMillis() - Config.startUp;
        Resources resources = new Resources();

        EmbedBuilder dashboard = new EmbedBuilder()
                .setAuthor("dashboard", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .addField("\uD83D\uDDA5 │ Server",
                        "**CPU:** " + resources.getCpuLoad() + "\n" +
                                "**RAM:** " + resources.getRAMUsage() + "mb"
                        , true)
                .addField("\u231A │ Uptime", Format.toTime(uptime), true)
                .addField("Current running threads", resources.getRunningThreads(), true)
                .addField("\uD83D\uDDC2 │ Shards", String.valueOf(ctx.getEvent().getJDA().getShardManager().getShardsTotal()), true)
                .addField("\uD83D\uDDDC │ Guilds", String.valueOf(ctx.getEvent().getJDA().getGuilds().size()), true)
                .addField("\uD83D\uDC65 │Users", String.valueOf(count), true)
                .addField("votes", "NULL", true);
        ctx.getChannel().sendMessage(dashboard.build()).queue();
    }
}
