package com.github.m5rian.myra.commands.member.general;

import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.CommandUtils;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Suggest implements CommandHandler {
    @CommandEvent(
            name = "suggest",
            args = {"<suggestion>"},
            emoji = "\uD83D\uDDF3",
            description = "description.suggest"
    )
    public void execute(CommandContext ctx) throws Exception {
        final MongoGuild db = MongoGuild.get(ctx.getGuild()); // Get database
        if (!db.getListenerManager().check("suggestions")) return; // Feature is disabled

        // Command usage
        if (ctx.getArguments().length == 0) {
            usage(ctx).send();
            return;
        }

        // No suggestions channel has been set up yet
        if (db.getString("suggestionsChannel").equals("not set")) {
            error(ctx).setDescription(lang(ctx).get("command.suggest.error.noChannel")).send();
            return;
        }

        final TextChannel channel = ctx.getGuild().getTextChannelById(db.getString("suggestionsChannel"));
        if (!Utilities.hasPermsInChannel(ctx, channel, Permission.MESSAGE_ADD_REACTION)) return;

        sendSuggestion(ctx, channel);
        CommandUtils.infoFactory.invoke(ctx).setDescription(lang(ctx).get("command.suggest.info.success")).send();
    }

    public static void sendSuggestion(CommandContext ctx, TextChannel channel) {
        final EmbedBuilder suggestion = new EmbedBuilder()
                .setAuthor(ctx.getAuthor().getAsTag(), ctx.getEvent().getMessage().getJumpUrl(), ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(ctx.getMember().getColor())
                .setThumbnail(ctx.getAuthor().getEffectiveAvatarUrl())
                .setDescription(ctx.getArgumentsRaw());
        // Attachment is given
        if (!ctx.getEvent().getMessage().getAttachments().isEmpty()) {
            suggestion.setImage(ctx.getEvent().getMessage().getAttachments().get(0).getUrl());
        }

        // Send suggestion
        channel.sendMessageEmbeds(suggestion.build()).queue((message) -> {
            message.addReaction("\uD83D\uDC4D").queue(); // Thumbs up
            message.addReaction("\uD83D\uDC4E").queue(); // Thumbs down
        });
    }

}
