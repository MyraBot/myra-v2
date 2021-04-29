package com.myra.dev.marian.commands.help;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.CommandEmbeds;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
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
            aliases = {"help me"}
    )
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;

        final Utilities utilities = Utilities.getUtils();
        final String name = ctx.getEvent().getJDA().getSelfUser().getName(); // Get name of bot
        //embed
        EmbedBuilder help = new EmbedBuilder()
                .setAuthor("help", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setThumbnail(ctx.getEvent().getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setDescription("Hey there!" +
                        "\nI'm a multi-purpose bot featuring a lot of customizable commands!" +
                        "\nIf you found a bug, make sure to report it using the `" + ctx.getPrefix() + "report` command. You have a cool idea? Great! We're open to suggestions, suggest it using the command `" + ctx.getPrefix() + "feature`" +
                        "\n" +
                        "\nIn order to use the administrator commands, you need `Administrator` permissions. A moderator needs `View Audit logs` permissions." +
                        "\nTo see all availible commands, type in `" + ctx.getPrefix() + "commands`")
                .addField("**\u2709\uFE0F │ invite**", utilities.hyperlink("Invite ", "https://discord.gg/nG4uKuB") + ctx.getEvent().getJDA().getSelfUser().getName() + " to your server", true)
                .addField("**\u26A0\uFE0F │ support**", utilities.hyperlink("Report ", "https://discord.gg/nG4uKuB") + " bugs and get " + utilities.hyperlink("help ", "https://discord.gg/nG4uKuB"), true);
        ctx.getChannel().sendMessage(help.build()).queue(message -> {
            // Add reactions
            message.addReaction(emojis[0]).queue(); // ✉️
            message.addReaction(emojis[1]).queue(); // ⚠️

            // Event waiter
            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e -> !e.getUser().isBot()
                            && e.getUser() == ctx.getAuthor()
                            && e.getMessageId().equals(message.getId())
                            && Arrays.stream(emojis).anyMatch(e.getReactionEmote().getEmoji()::equals))
                    .setAction(e -> {
                        final CommandEmbeds embed = new CommandEmbeds(e.getGuild(), e.getJDA(), e.getUser(), new MongoGuild(e.getGuild()).getString("prefix")); // Get embeds
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

