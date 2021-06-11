package com.github.m5rian.myra.commands.member.help;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.utilities.CommandEmbeds;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.language.Lang;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Help implements CommandHandler {
    private final String[] emojis = {
            "\u2709\uFE0F", // ✉️
            "\u26A0\uFE0F" // ⚠️
    };

    @CommandEvent(
            name = "help",
            aliases = {"help me"},
            emoji = "\uD83E\uDDF0",
            description = "description.help.help"
    )
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;

        final Success help = new Success(ctx.getEvent())
                .setCommand("help")
                .setEmoji("\u2709\uFE0F")
                .setThumbnail(ctx.getEvent().getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setMessage(Lang.lang(ctx).get("command.help.help.text").replace("{$prefix}", ctx.getPrefix()))
                // Myra invite
                .addInlineField("\u2709 │ " + Lang.lang(ctx).get("word.invite"), Lang.lang(ctx).get("command.help.help.invite")
                        .replace("{$url}", Utilities.inviteJda(ctx.getEvent().getJDA()))) // Myra invite url
                // Support server
                .addInlineField("\u26A0\uFE0F │ " + Lang.lang(ctx).get("word.support"), Lang.lang(ctx).get("command.help.help.support")
                        .replace("{$url}", Config.MARIANS_DISCORD_INVITE)); // Discord invite url
        ctx.getChannel().sendMessage(help.getEmbed().build()).queue(message -> {
            // Add reactions
            message.addReaction(emojis[0]).queue(); // ✉️
            message.addReaction(emojis[1]).queue(); // ⚠️

            // Event waiter
            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e -> !e.getUser().isBot()
                            && e.getUserIdLong() == ctx.getAuthor().getIdLong()
                            && e.getMessageIdLong() == message.getIdLong()
                            && Arrays.asList(emojis).contains(e.getReactionEmote().getEmoji()))
                    .setAction(e -> {
                        final CommandEmbeds embed = new CommandEmbeds(ctx); // Get embeds
                        final String reaction = e.getReactionEmote().getEmoji(); // Get reacted emoji

                        // Invite bot
                        if (reaction.equals(emojis[0])) { // ✉️
                            message.editMessage(embed.inviteJda().build()).queue(); // Edit message
                        }
                        // Support server
                        if (reaction.equals(emojis[1])) { // ⚠️
                            message.editMessage(embed.supportServer().build()).queue(); // Edit message
                        }

                        message.clearReactions().queue(); // Clear reactions
                    })
                    .setTimeout(30L, TimeUnit.SECONDS)
                    .setTimeoutAction(() -> message.clearReactions().queue())
                    .load();
        });
    }
}

