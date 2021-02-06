package com.myra.dev.marian.commands.help;

import com.myra.dev.marian.Myra;
import com.myra.dev.marian.database.allMethods.Database;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.CommandEmbeds;
import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@CommandSubscribe(
        name = "help",
        aliases = {"help me"}
)
public class Help implements Command {
    private final String[] emojis = {
            "\u2709\uFE0F", // ✉️
            "\u26A0\uFE0F" // ⚠️
    };

    @Override
    public void execute(CommandContext ctx) throws Exception {
        //check for no arguments
        if (ctx.getArguments().length != 0) return;
        Utilities utilities = Utilities.getUtils();
        //embed
        EmbedBuilder help = new EmbedBuilder()
                .setAuthor("help", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setThumbnail(ctx.getEvent().getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setDescription(ctx.getEvent().getJDA().getSelfUser().getName() + " is a multi-purpose bot featuring moderation, music, welcoming and much more!\n" +
                        "If you found a bug please report it in " + utilities.hyperlink("my Discord server", "https://discord.gg/nG4uKuB") + " or write me (" + ctx.getEvent().getJDA().getUserById(Config.marian).getAsTag() + ") a direct message. For suggestions join the server as well!\n" +
                        "A moderator role must have `View Audit Log` permission to use the moderation commands. To see all available commands type in `" + ctx.getPrefix() + "commands`")
                .addField("**\u2709\uFE0F │ invite**", utilities.hyperlink("Invite ", "https://discord.gg/nG4uKuB") + ctx.getEvent().getJDA().getSelfUser().getName() + " to your server", true)
                .addField("**\u26A0\uFE0F │ support**", utilities.hyperlink("Report ", "https://discord.gg/nG4uKuB") + " bugs and get " + utilities.hyperlink("help ", "https://discord.gg/nG4uKuB"), true);
        ctx.getChannel().sendMessage(help.build()).queue(message -> {
            // Add reactions
            message.addReaction(emojis[0]).queue(); // ✉️
            message.addReaction(emojis[1]).queue(); // ⚠️

            // Event waiter
            Myra.WAITER.waitForEvent(
                    GuildMessageReactionAddEvent.class, // Event to wait for
                    e -> // Condition
                            !e.getUser().isBot()
                                    && e.getUser() == ctx.getAuthor()
                                    && e.getMessageId().equals(message.getId())
                                    && Arrays.stream(emojis).anyMatch(e.getReactionEmote().getEmoji()::equals),
                    e -> {
                        final CommandEmbeds embed = new CommandEmbeds(e.getGuild(), e.getJDA(), e.getUser(), new Database(e.getGuild()).getString("prefix")); // Get embeds
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
                    },
                    30L, TimeUnit.SECONDS, // Timeout
                    () -> message.clearReactions().queue()
            );
        });
    }
}

