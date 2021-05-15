package com.myra.dev.marian.commands.general.information;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.Utilities;
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
                .setColor(Utilities.getUtils().blue)
                .setThumbnail(ctx.getGuild().getIconUrl())
                .addField("\uD83D\uDC51 │ owner", ctx.getGuild().getOwner().getAsMention(), true)
                .addField("\uD83C\uDF9F │ server id", ctx.getGuild().getId(), true)
                .addBlankField(true)
                .addField("\uD83D\uDE80 │ boosts", "level " + ctx.getGuild().getBoostTier().toString().replace("NONE", "0").replace("TIER_", "") + " (" + ctx.getGuild().getBoostCount() + " boosts)", true)
                .addField("\uD83E\uDDEE │ member count", Integer.toString(ctx.getGuild().getMemberCount()), true)
                .addBlankField(true)
                .addField("\uD83D\uDCC6 │ created at", ctx.getGuild().getTimeCreated().getDayOfMonth() + " " + ctx.getGuild().getTimeCreated().getMonth() + " " + ctx.getGuild().getTimeCreated().getYear(), true);
        ctx.getChannel().sendMessage(server.build()).queue(message -> {
            //reactions
            message.addReaction("\uD83D\uDCDC").queue();

            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e -> !e.getUser().isBot()
                            && e.getUser().getIdLong() == ctx.getAuthor().getIdLong()
                            && e.getReactionEmote().getEmoji().equals("\uD83D\uDCDC"))
                    .setAction(e -> {
                        server
                                .addField("\uD83D\uDDFA │ region", e.getGuild().getRegionRaw(), false)
                                .addField("\uD83D\uDDD2 │ details",
                                        "\uD83D\uDCC1 │ channels: `" + e.getGuild().getChannels().size() + "`" +
                                                "\n\uD83D\uDC65 │ roles: `" + e.getGuild().getRoles().size() + "`" +
                                                "\n\uD83E\uDD2A │ emojis: `" + e.getGuild().getRoles().size() + "`",
                                        true)
                                .addField("\uD83D\uDDD2 │ moderation",
                                        "\u2705 │ verification level: `" + e.getGuild().getVerificationLevel().toString().toLowerCase() + "`" +
                                                "\n\uD83D\uDCFA │ media content filter: `" + e.getGuild().getExplicitContentLevel().toString().toLowerCase() + "`",
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

