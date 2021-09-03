package com.github.m5rian.myra.commands.administrator;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.commandMessages.CommandMessage;
import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.Emoji;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.Optional;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Suggestions implements CommandHandler {

    private final Emoji thumbsUp = Emoji.fromUnicode("\uD83D\uDC4D");
    private final Emoji thumbsDown = Emoji.fromUnicode("\uD83D\uDC4E");

    @CommandEvent(
            name = "suggestions",
            emoji = "\uD83D\uDDF3",
            description = "description.suggestions",
            channel = Channel.GUILD,
            requires = Administrator.class
    )
    public void onHelpCommand(CommandContext ctx) {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Show command usage
        usage(ctx).addUsages(this.getClass())
                .forbidCommands("onHelpCommand")
                .send();
    }

    @CommandEvent(
            name = "suggestions channel",
            emoji = "\uD83D\uDCC1",
            description = "description.suggestions.channel",
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void onChannelCommand(CommandContext ctx) {
        // Command usage
        if (ctx.getArguments().length != 1) {
            usage(ctx).send();
            return;
        }

        final MongoGuild db = MongoGuild.get(ctx.getGuild());
        // Get given channel
        final TextChannel channel = Utilities.getTextChannel(ctx.getEvent(), ctx.getArguments()[0], "suggestions", "\uD83D\uDDF3");
        if (channel == null) return;
        // Success
        final CommandMessage success = info(ctx);
        // Remove suggestions channel
        if (db.getString("suggestionsChannel").equals(channel.getId())) {
            db.setString("suggestionsChannel", "not set"); // Update database
            success.setDescription(lang(ctx).get("command.suggestions.channel.removed")).send(); // Success
        }
        // Add or change suggestions channel
        else {
            db.setString("suggestionsChannel", channel.getId()); // Update database

            if (!Utilities.hasPermsInChannel(ctx, channel)) return;
            success.setDescription(lang(ctx).get("command.suggestions.channel.changed")
                    .replace("{$channel.mention}", channel.getAsMention())).send();
            success.setDescription(lang(ctx).get("command.suggestions.channel.changedActive")).setChannel(channel).send();
        }
    }

    @CommandEvent(
            name = "suggestions toggle",
            emoji = "\uD83D\uDCC1",
            description = "description.suggestions.toggle",
            requires = Administrator.class
    )
    public void onToggleCommand(CommandContext ctx) {
        // Check for no arguments
        if (ctx.getArguments().length == 0) {
            // Toggle feature
            MongoGuild.get(ctx.getGuild()).getListenerManager().toggle("suggestions", "\uD83D\uDDF3", ctx.getEvent());
        }
    }

    @CommandEvent(
            name = "suggestions accept",
            description = "description.suggestions.accept",
            emoji = "\u2705",
            args = {"<message id>", "(note)"},
            requires = Administrator.class
    )
    public void onAcceptCommand(CommandContext ctx) {
        // Command usage
        if (ctx.getArguments().length == 0) {
            usage(ctx).send();
            return;
        }

        final String channelId = MongoGuild.get(ctx.getGuild()).getString("suggestionsChannel"); // Get suggestions channel id
        // channel id is invalid or no channel is set
        if (channelId.equals("not set") || ctx.getGuild().getTextChannelById(channelId) == null) {
            MongoGuild.get(ctx.getGuild()).setString("suggestionsChannel", "not set"); // Reset channel id
            error(ctx).setDescription(lang(ctx).get("command.suggestions.error.noChannel")).send();
            return;
        }

        try {
            final String messageId = ctx.getArguments()[0]; // Get message id
            ctx.getGuild().getTextChannelById(channelId).retrieveMessageById(messageId).queue(
                    message -> {
                        // Message is a suggestion message
                        if (message.getAuthor().getId().equals(Config.MYRA_ID)
                                && message.getEmbeds().size() != 0
                                && !List.of(Utilities.red, Utilities.blue, Utilities.gray, Utilities.green).contains(message.getEmbeds().get(0).getColorRaw())) {
                            // Bot is missing permissions
                            if (!Utilities.hasPermsInChannel(ctx, message.getTextChannel(), Permission.MESSAGE_MANAGE)) return;

                            // Edit suggestion
                            final Optional<MessageReaction> r1 = message.getReactions().stream().filter(reaction -> Emoji.fromReactionEmote(reaction.getReactionEmote()).equals(thumbsUp)).findFirst();
                            int thumbUps = 0;
                            if (r1.isPresent()) thumbUps = r1.get().getCount() + 1;

                            final Optional<MessageReaction> r2 = message.getReactions().stream().filter(reaction -> Emoji.fromReactionEmote(reaction.getReactionEmote()).equals(thumbsDown)).findFirst();
                            int thumbDowns = 0;
                            if (r2.isPresent()) thumbDowns = r2.get().getCount() + 1;

                            final EmbedBuilder accepted = new EmbedBuilder(message.getEmbeds().get(0))
                                    .setColor(Utilities.green)
                                    .setTitle(lang(ctx).get("command.suggestions.accept.info.title"))
                                    .setFooter(String.format("%s %d │ %s %d", thumbsUp.getUnicode(), thumbUps, thumbsDown.getUnicode(), thumbDowns));
                            if (ctx.getArguments().length > 1) {
                                accepted.appendDescription("\n\n" + ctx.getArgumentsRaw().split("\\s+", 2)[1]);
                            }

                            message.editMessageEmbeds(accepted.build()).queue();
                            message.clearReactions().queue(); // Remove reactions

                            // Success message
                            info(ctx).setDescription(lang(ctx).get("command.suggestions.accept.info.success")
                                            .replace("{$message.url}", message.getJumpUrl()))
                                    .send();
                        } else {
                            error(ctx).setDescription(lang(ctx).get("command.suggestions.error.wrongMessage")).send();
                        }
                    }, error -> error(ctx).setDescription(lang(ctx).get("command.suggestions.error.loadingMessage")).send());
        }
        // Mostly thrown if the provided message id isn't a valid snowflake value
        catch (IllegalArgumentException e) {
            error(ctx).setDescription(lang(ctx).get("error.retrieving.message.invalid")).send();
        }

    }

    @CommandEvent(
            name = "suggestions deny",
            description = "description.suggestions.deny",
            emoji = "\uD83D\uDEAB",
            args = {"<message id>", "(note)"},
            requires = Administrator.class
    )
    public void onDenyCommand(CommandContext ctx) {
        // Command usage
        if (ctx.getArguments().length == 0) {
            usage(ctx).send();
            return;
        }

        final String channelId = MongoGuild.get(ctx.getGuild()).getString("suggestionsChannel"); // Get suggestions channel id
        // channel id is invalid or no channel is set
        if (channelId.equals("not set") || ctx.getGuild().getTextChannelById(channelId) == null) {
            MongoGuild.get(ctx.getGuild()).setString("suggestionsChannel", "not set"); // Reset channel id
            error(ctx).setDescription(lang(ctx).get("command.suggestions.error.noChannel")).send();
            return;
        }

        try {
            final String messageId = ctx.getArguments()[0]; // Get message id
            ctx.getGuild().getTextChannelById(channelId).retrieveMessageById(messageId).queue(
                    message -> {
                        // Message is a suggestion message
                        if (message.getAuthor().getId().equals(Config.MYRA_ID)
                                && message.getEmbeds().size() != 0
                                && !List.of(Utilities.red, Utilities.blue, Utilities.gray, Utilities.green).contains(message.getEmbeds().get(0).getColorRaw())) {
                            // Bot is missing permissions
                            if (!Utilities.hasPermsInChannel(ctx, message.getTextChannel(), Permission.MESSAGE_MANAGE)) return;

                            // Edit suggestion
                            final Optional<MessageReaction> r1 = message.getReactions().stream().filter(reaction -> Emoji.fromReactionEmote(reaction.getReactionEmote()).equals(thumbsUp)).findFirst();
                            int thumbUps = 0;
                            if (r1.isPresent()) thumbUps = r1.get().getCount() + 1;

                            final Optional<MessageReaction> r2 = message.getReactions().stream().filter(reaction -> Emoji.fromReactionEmote(reaction.getReactionEmote()).equals(thumbsDown)).findFirst();
                            int thumbDowns = 0;
                            if (r2.isPresent()) thumbDowns = r2.get().getCount() + 1;

                            final EmbedBuilder denied = new EmbedBuilder(message.getEmbeds().get(0))
                                    .setColor(Utilities.red)
                                    .setTitle(lang(ctx).get("command.suggestions.deny.info.title"))
                                    .setFooter(String.format("%s %d │ %s %d", thumbsUp.getUnicode(), thumbUps, thumbsDown.getUnicode(), thumbDowns));
                            if (ctx.getArguments().length > 1) {
                                denied.appendDescription("\n\n" + ctx.getArgumentsRaw().split("\\s+", 2)[1]);
                            }

                            message.editMessageEmbeds(denied.build()).queue();
                            message.clearReactions().queue(); // Remove reactions

                            // Success message
                            info(ctx).setDescription(lang(ctx).get("command.suggestions.deny.info.success")
                                            .replace("{$message.url}", message.getJumpUrl()))
                                    .send();
                        } else {
                            error(ctx).setDescription(lang(ctx).get("command.suggestions.error.wrongMessage")).send();
                        }
                    },
                    error -> error(ctx).setDescription(lang(ctx).get("command.suggestions.error.loadingMessage")).send());
        }
        // Mostly thrown if the provided message id isn't a valid snowflake value
        catch (IllegalArgumentException e) {
            error(ctx).setDescription(lang(ctx).get("error.retrieving.message.invalid")).send();
        }
    }

}
