package com.github.m5rian.myra.commands.administrator.reactionRoles;

import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.MongoDb;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import static com.github.m5rian.myra.utilities.language.Lang.*;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

public class ReactionRolesAdd implements CommandHandler {
    private final String[] emojis = {
            "\uD83C\uDF81",
            "\uD83E\uDD84",
            "\u2705"
    };

    @CommandEvent(
            name = "reaction roles add",
            aliases = {"reaction role add", "rr add"},
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("reaction roles add")
                    .addUsages(new Usage()
                            .setUsage("reaction roles add <role>")
                            .setEmoji("\uD83D\uDD17")
                            .setDescription(lang(ctx).get("description.reactionRoles.add")))
                    .send();
            return;
        }

        // Get role
        final Role role = Utilities.getRole(ctx.getEvent(), ctx.getArguments()[0], "reaction roles add", "\uD83D\uDD17"); // Get given role
        if (role == null) return;

        // Create reaction roles document
        final Document reactionRolesInfo = new Document()
                .append("role", role.getId()) // Store role id
                .append("message", null) // Add message id key
                .append("emoji", null) // Add emoji key
                .append("type", null); // Add type id

        // Create embed to choose the reaction role type
        EmbedBuilder type = new EmbedBuilder()
                .setAuthor("reaction roles add", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.blue)
                .setDescription(lang(ctx).get("command.reactionRoles.add.instruction.type"))
                .addField("normal", "\uD83C\uDF81 │ " + lang(ctx).get("command.reactionRoles.add.type.normal"), true)
                .addField("unique", "\uD83E\uDD84 │ " + lang(ctx).get("command.reactionRoles.add.type.unique"), true)
                .addField("verify", "\u2705 │ " + lang(ctx).get("command.reactionRoles.add.type.verify"), true);
        ctx.getChannel().sendMessage(type.build()).queue(msg1 -> { // Send message to select the reaction roles type
            // Add reactions
            msg1.addReaction(emojis[0]).queue(); // 🎁
            msg1.addReaction(emojis[1]).queue(); // 🦄
            msg1.addReaction(emojis[2]).queue(); // ✅

            // Event waiter
            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e1 -> !e1.getUser().isBot()
                            && e1.getUser().getIdLong() == ctx.getAuthor().getIdLong()
                            && e1.getMessageId().equals(msg1.getId())
                            && Arrays.asList(emojis).contains(e1.getReactionEmote().getEmoji()))
                    .setAction(e1 -> {
                        final String reaction = e1.getReactionEmote().getEmoji(); // Get reacted emoji
                        // Choose reaction role type
                        if (reaction.equals(emojis[0]))
                            reactionRolesInfo.replace(lang(ctx).get("command.reactionRoles.add.type"), "normal"); // Set reaction roles type to normal
                        if (reaction.equals(emojis[1]))
                            reactionRolesInfo.replace(lang(ctx).get("command.reactionRoles.add.type"), "unique"); // Set reaction roles type to unique
                        if (reaction.equals(emojis[2]))
                            reactionRolesInfo.replace(lang(ctx).get("command.reactionRoles.add.type"), "verify"); // Set reaction roles type to verify

                        msg1.clearReactions().queue(); // Clear reactions
                        // Create embed as a information to choose now the message and reaction emoji
                        EmbedBuilder messageAndEmojiSelection = new EmbedBuilder()
                                .setAuthor("reaction roles add", null, e1.getUser().getEffectiveAvatarUrl())
                                .setColor(Utilities.blue)
                                .setDescription(lang(ctx).get("command.reactionRoles.add.instruction.react"));

                        msg1.editMessage(messageAndEmojiSelection.build()).queue(msg2 -> { // Edit message to select the reaction roles message and emoji

                            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                                    .setCondition(e -> !e.getUser().isBot()
                                            && e.getUser().getIdLong() == ctx.getMember().getIdLong())
                                    .setAction(e -> {
                                        final MessageReaction.ReactionEmote emote = e.getReactionEmote(); // Get reaction emote as a variable

                                        String emoji = null; // Store emoji
                                        if (emote.isEmoji()) emoji = emote.getEmoji(); // Save emoji
                                        else if (emote.isEmoji()) emoji = emote.getEmote().getId(); // Save emote

                                        reactionRolesInfo.replace("message", e.getMessageId()); // Add message id
                                        reactionRolesInfo.replace("emoji", emoji); // Emoji

                                        messageAndEmojiSelection.clear()
                                                .setAuthor("reaction roles add", null, e.getUser().getEffectiveAvatarUrl())
                                                .setColor(Utilities.blue)
                                                .setDescription(lang(ctx).get("command.reactionRoles.add.success"));
                                        e.getChannel().sendMessage(messageAndEmojiSelection.build()).queue(); // Send success message

                                        e.getChannel().retrieveMessageById(reactionRolesInfo.getString("message")).queue(message -> { // Get reaction roles message
                                            message.addReaction(reactionRolesInfo.getString("emoji")).queue(); // Add reaction
                                        });

                                        final MongoGuild guildDocument = MongoGuild.get(e.getGuild());
                                        List<Document> reactionRoles = guildDocument.getList("reactionRoles", Document.class); // Get reaction roles list
                                        reactionRoles.add(reactionRolesInfo); // Add reaction roles info
                                    })
                                    .setTimeout(30L, TimeUnit.SECONDS)
                                    .setTimeoutAction(() -> {
                                        msg1.clearReactions().queue(); // Clear reactions
                                        new Error(ctx.getEvent())
                                                .setCommand("reaction roles add")
                                                .setMessage(lang(ctx).get("error.timeout"))
                                                .send();
                                    })
                                    .load();

                        });
                    })
                    .setTimeout(30L, TimeUnit.SECONDS)
                    .setTimeoutAction(() -> {
                        msg1.clearReactions().queue(); // Clear reactions
                        new Error(ctx.getEvent())
                                .setCommand("reaction roles add")
                                .setMessage(lang(ctx).get("error.timeout"))
                                .send();
                    })
                    .load();
        });

    }
}
