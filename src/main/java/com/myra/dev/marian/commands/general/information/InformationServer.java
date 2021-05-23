package com.myra.dev.marian.commands.general.information;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.Utilities;
import static com.myra.dev.marian.utilities.language.Lang.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.concurrent.TimeUnit;

public class InformationServer implements CommandHandler {

    @CommandEvent(
            name = "information server",
            aliases = {"info server", "information guild", "info guild", "information GUILD_NAME", "info GUILD_NAME"},
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        //servers information
        EmbedBuilder server = new EmbedBuilder()
                .setAuthor(ctx.getGuild().getName(), null, ctx.getGuild().getIconUrl())
                .setColor(Utilities.blue)
                .setThumbnail(ctx.getGuild().getIconUrl())
                .addField("\uD83D\uDC51 │ " + lang(ctx).get("command.general.info.server.owner"), ctx.getGuild().getOwner().getAsMention(), true)
                .addField("\uD83C\uDF9F │ " + lang(ctx).get("command.general.info.server.serverId"), ctx.getGuild().getId(), true)
                .addBlankField(true)
                .addField("\uD83D\uDE80 │ " + lang(ctx).get("command.general.info.server.boosts"), "level " + ctx.getGuild().getBoostTier().toString().replace("NONE", "0").replace("TIER_", "") + " (" + ctx.getGuild().getBoostCount() + " boosts)", true)
                .addField("\uD83E\uDDEE │ " + lang(ctx).get("command.general.info.server.memberCount"), Integer.toString(ctx.getGuild().getMemberCount()), true)
                .addBlankField(true)
                .addField("\uD83D\uDCC6 │ " + lang(ctx).get("command.general.info.server.createdAt"), ctx.getGuild().getTimeCreated().getDayOfMonth() + " " + ctx.getGuild().getTimeCreated().getMonth() + " " + ctx.getGuild().getTimeCreated().getYear(), true);
        ctx.getChannel().sendMessage(server.build()).queue(message -> {
            //reactions
            message.addReaction("\uD83D\uDCDC").queue();

            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e -> !e.getUser().isBot()
                            && e.getUser().getIdLong() == ctx.getAuthor().getIdLong()
                            && e.getReactionEmote().getEmoji().equals("\uD83D\uDCDC"))
                    .setAction(e -> {
                        server
                                .addField("\uD83D\uDDFA │ " + lang(ctx).get("command.general.info.server.region"), e.getGuild().getRegionRaw(), false)
                                .addField("\uD83D\uDDD2 │ " + lang(ctx).get("command.general.info.server.details"),
                                        "\uD83D\uDCC1 │ " + lang(ctx).get("command.general.info.server.channels").replace("{$channelCount}", String.valueOf(ctx.getGuild().getChannels().size())) +
                                                "\n\uD83D\uDC65 " + lang(ctx).get("command.general.info.server.roles").replace("{$roleCount}", String.valueOf(ctx.getGuild().getRoles().size())) +
                                                "\n\uD83E\uDD2A " + lang(ctx).get("command.general.info.server.emojis").replace("{$emojiCount}", String.valueOf(ctx.getGuild().getEmotes().size())),
                                        true)
                                .addField("\uD83D\uDDD2 │ " + lang(ctx).get("command.general.info.server.moderation"),
                                        "\u2705 │ " + lang(ctx).get("command.general.info.server.verificationLevel").replace("{$verificationLevel}", e.getGuild().getVerificationLevel().toString().toLowerCase()) +
                                                "\n\uD83D\uDCFA │ " + lang(ctx).get("command.general.info.server.filter").replace("{$contentFilter}", ctx.getGuild().getExplicitContentLevel().toString().toLowerCase()),
                                        true);
                        e.retrieveMessage().queue(msg -> { // Retrieve message
                            message.editMessage(server.build()).queue(); // Edit message
                            message.clearReactions().queue(); // Clear reactions
                        });
                    })
                    .setTimeout(30L, TimeUnit.SECONDS)
                    .setTimeoutAction(() -> message.clearReactions().queue())
                    .load();

        });

    }
}

