package com.myra.dev.marian.commands.administrator.reactionRoles;

import com.myra.dev.marian.Myra;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import org.bson.Document;

import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandSubscribe(
        name = "reaction roles remove",
        aliases = {"reaction role", "rr remove"},
        requires = Administrator.class
)
public class ReactionRolesRemove implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        EmbedBuilder usage = new EmbedBuilder()
                .setAuthor("reaction roles remove", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("Remove the reaction of the reaction role");

        ctx.getChannel().sendMessage(usage.build()).queue(msg -> {
            Myra.WAITER.waitForEvent(
                    GuildMessageReactionRemoveEvent.class, // Event to wait for
                    e -> !e.getUser().isBot()
                            && e.getUser() == ctx.getAuthor(),
                    e -> { // Code on event
                        final MongoGuild db = new MongoGuild(ctx.getGuild()); // Get database
                        final List<Document> reactionRoles = db.getList("reactionRoles", Document.class); // Get reaction roles

                        final String reactionEmoji = e.getReactionEmote().getEmoji(); // Get emoji of removed reaction
                        final String reactionMessage = e.getMessageId(); // Get message id of reaction message

                        // Reacted message isn't a reaction role
                        if (reactionRoles.stream().noneMatch(reactionRole -> reactionMessage.equals(reactionRole.getString("message")) && reactionEmoji.equals(reactionRole.getString("emoji")))) {
                            new Error(ctx.getEvent())
                                    .setCommand("reaction roles remove")
                                    .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                                    .setMessage("This is not a reaction role")
                                    .send();
                            return;
                        }

                        for (Document reactionRole : reactionRoles) {
                            // Check every reaction role
                            final String messageId = reactionRole.getString("message"); // Get message id
                            final String emoji = reactionRole.getString("emoji"); // Get reaction emoji

                            // Remove reaction
                            if (reactionMessage.equals(messageId) && reactionEmoji.equals(emoji)) {
                                reactionRoles.remove(reactionRole); // Remove reaction role
                                db.setList("reactionRoles", reactionRoles); // Update database
                                e.retrieveMessage().queue(message -> message.removeReaction(emoji, e.getJDA().getSelfUser()).queue()); // Remove reaction from message

                                // Send success message
                                Success success = new Success(ctx.getEvent())
                                        .setCommand("reaction roles remove")
                                        .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                                        .setMessage("Deleted reaction role");
                                success.setChannel(e.getChannel()).send();
                                break;
                            }
                        }
                    },
                    30L, TimeUnit.SECONDS, // Timeout
                    () -> { // Code on timeout
                        new Error(ctx.getEvent())
                                .setCommand("reaction roles remove")
                                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                                .setMessage("You didn't remove a reaction")
                                .send();
                    }
            );
        });
    }
}