package com.github.m5rian.myra.commands.member.general.information;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.utilities.APIs.TopGG;
import com.github.m5rian.myra.utilities.Format;
import com.github.m5rian.myra.utilities.Resources;
import com.github.m5rian.myra.utilities.Utilities;
import static com.github.m5rian.myra.utilities.language.Lang.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

public class InformationBot implements CommandHandler {

    @CommandEvent(
            name = "information bot",
            aliases = {"info bot", "information BOT_NAME", "info BOT_NAME"}
    )
    public void execute(CommandContext ctx) throws Exception {
        ctx.getChannel().sendTyping().queue();

        final JDA jda = ctx.getEvent().getJDA();
        final long uptime = System.currentTimeMillis() - Config.startUp;
        final Resources resources = new Resources();

        final EmbedBuilder dashboard = new EmbedBuilder()
                .setAuthor("dashboard", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.blue)
                .setThumbnail(ctx.getEvent().getJDA().getSelfUser().getAvatarUrl())
                .addField("\uD83D\uDDA5 │ " + lang(ctx).get("command.general.info.bot.server"),
                        "**CPU:** " + resources.getCpuLoad() + "\n" +
                                "**RAM:** " + resources.getRAMUsage() + "mb"
                        , true)
                .addField("\u23F1 │ " + lang(ctx).get("command.general.info.bot.uptime"), Format.toTime(uptime), true)
                .addField("\uD83E\uDDF5 │ " + lang(ctx).get("command.general.info.bot.threads"), resources.getRunningThreads(), true)
                .addField("\uD83D\uDDC2 │ " + lang(ctx).get("command.general.info.bot.shards"), String.valueOf(jda.getShardManager().getShardsTotal()), true)
                .addField("\uD83D\uDDDC │ " + lang(ctx).get("command.general.info.bot.guilds"), String.valueOf(jda.getGuilds().size()), true)
                .addField("\uD83D\uDC65 │ " + lang(ctx).get("command.general.info.bot.members"), String.valueOf(Utilities.getMemberCount(jda)), true)
                .addField("\uD83D\uDC65 │ " + lang(ctx).get("command.general.info.bot.users"), String.valueOf(Utilities.getUserCount(jda)), true)
                .addField("\uD83D\uDDF3 │ " + lang(ctx).get("command.general.info.bot.votes"), TopGG.getInstance().getUpVotes(), true);
        ctx.getChannel().sendMessage(dashboard.build()).queue();
    }
}