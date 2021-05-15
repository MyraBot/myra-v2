package com.myra.dev.marian.marian;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.DiscordBot;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Marian;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.concurrent.TimeUnit;


public class Shutdown implements CommandHandler {
    private final String emoji = "\u2705"; // ✅

    @CommandEvent(
            name = "shutdown",
            requires = Marian.class
    )
    public void execute(CommandContext ctx) throws Exception {
        EmbedBuilder shutdown = new EmbedBuilder()
                .setAuthor("shutdown", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("Wait what!? You want me to take a break? Are you sure?");
        ctx.getChannel().sendMessage(shutdown.build()).queue(message -> { // Send shutdown request
            message.addReaction(emoji).queue(); // Add reaction

            // Event waiter
            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e ->
                            !e.getUser().isBot()
                                    && e.getUser() == ctx.getAuthor()
                                    && e.getMessageId().equals(message.getId()))
                    .setAction(e -> {
                        message.clearReactions().queue(); // Clear reactions
                        DiscordBot.shardManager.setStatus(OnlineStatus.OFFLINE); // Set status to offline
                        e.getJDA().shutdown(); // Shutdown JDA
                        System.exit(0);  // Shutdown whole program
                    })
                    .setTimeout(30L, TimeUnit.SECONDS)
                    .setTimeoutAction(() -> message.clearReactions().queue())
                    .load();

        });
    }
}
