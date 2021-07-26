package com.github.m5rian.myra.commands.administrator.reactionRoles;

import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import static com.github.m5rian.myra.utilities.language.Lang.*;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import org.bson.Document;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReactionRolesRemove implements CommandHandler {

    @CommandEvent(
            name = "reaction roles remove",
            aliases = {"reaction role", "rr remove"},
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        final Success success = new Success(ctx.getEvent())
                .setCommand("reaction roles remove")
                .setMessage(lang(ctx).get("command.reactionRoles.remove.instruction.removeReaction"));

        ctx.getChannel().sendMessage(success.getEmbed().build()).queue(msg -> {
            ctx.getWaiter().waitForEvent(GuildMessageReactionRemoveEvent.class)
                    .setCondition(e -> !e.getUser().isBot() && e.getUser() == ctx.getAuthor())
                    .setAction(e -> {
                        final MongoGuild db = MongoGuild.get(ctx.getGuild()); // Get database
                        final List<Document> reactionRoles = db.getList("reactionRoles", Document.class); // Get reaction roles

                        final String reactionEmoji = e.getReactionEmote().getEmoji(); // Get emoji of removed reaction
                        final String reactionMessage = e.getMessageId(); // Get message id of reaction message

                        // Reacted message isn't a reaction role
                        if (reactionRoles.stream().noneMatch(reactionRole -> reactionMessage.equals(reactionRole.getString("message")) && reactionEmoji.equals(reactionRole.getString("emoji")))) {
                            new Error(ctx.getEvent())
                                    .setCommand("reaction roles remove")
                                    .setMessage(lang(ctx).get("command.reactionRoles.remove.error.invalid"))
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
                                success.setMessage(lang(ctx).get("command.reactionRoles.remove.success"));
                                success.setChannel(e.getChannel()).send();
                                break;
                            }
                        }
                    })
                    .setTimeout(30L, TimeUnit.SECONDS)
                    .setTimeoutAction(() -> {
                        new Error(ctx.getEvent())
                                .setCommand("reaction roles remove")
                                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                                .setMessage(lang(ctx).get("error.timeout"))
                                .send();
                    })
                    .load();


        });
    }
}