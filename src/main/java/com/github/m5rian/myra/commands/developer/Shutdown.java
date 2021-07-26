package com.github.m5rian.myra.commands.developer;

import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.DiscordBot;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.permissions.Marian;
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
                .setColor(Utilities.blue)
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
                        System.exit(-2); // Shut down program gracefully and run the shutdown hooks
                    })
                    .setTimeout(30L, TimeUnit.SECONDS)
                    .setTimeoutAction(() -> message.clearReactions().queue())
                    .load();

        });
    }
}
