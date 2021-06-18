package com.github.m5rian.myra.commands.member.leveling;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.database.guild.member.GuildMember;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.ImageEditor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Background implements CommandHandler {
    private final String[] emojis = {
            "\u2705", // Checkmark
            "\uD83D\uDEAB" // Barrier
    };

    @CommandEvent(
            name = "edit rank",
            args = {"url"},
            emoji = "\uD83D\uDDBC",
            description = "description.leveling.edit.rank",
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("edit rank")
                    .addUsages(new Usage()
                            .setUsage("edit rank <url>")
                            .setEmoji("\uD83D\uDDBC")
                            .setDescription(lang(ctx).get("description.leveling.edit.rank")))
                    .send();
            return;
        }

        final MongoGuild db = MongoGuild.get(ctx.getGuild()); // Get database
        // Not enough money
        if (GuildMember.get(ctx.getMember()).getBalance() < 10000) {
            new Error(ctx.getEvent())
                    .setCommand("edit rank")
                    .setEmoji("\uD83D\uDDBC")
                    .setMessage(lang(ctx).get("command.leveling.edit.rank.error.tooExpensive")
                            .replace("{$currency}", db.getNested("economy").getString("currency"))) // Guild currency
                    .send();
            return;
        }
        // Check if argument is an image
        try {
            ImageIO.read(new URL(ctx.getArguments()[0])); // Argument is an image
        }
        // Argument isn't an image
        catch (Exception e) {
            new Error(ctx.getEvent())
                    .setCommand("edit rank")
                    .setEmoji("\uD83D\uDDBC")
                    .setMessage(lang(ctx).get("command.leveling.edit.rank.error.noImage"))
                    .send();
            return;
        }

        final BufferedImage backgroundRaw = ImageIO.read(new URL(ctx.getArguments()[0])); // Get image from Url
        final ImageEditor backgroundEditor = new ImageEditor(backgroundRaw).resizeSmart(Rank.imageWidth, Rank.imageHeight); // Resize background
        final InputStream background = backgroundEditor.getInputStream(); // Get InputStream from only the rank background
        final BufferedImage rankCard = Rank.renderRankCard(ctx.getMember(), backgroundEditor.getBufferedImage()); // Get rank card as input stream

        // Confirmation
        final String rankPreviewFileName = "rank_card_" + ctx.getAuthor().getId() + ".png";
        final EmbedBuilder confirmation = info(ctx)
                .setDescription(lang(ctx).get("command.leveling.edit.rank.message.confirmation")
                        .replace("{$currency}", db.getNested("economy").getString("currency"))) // Guild currency
                .setImage("attachment://" + rankPreviewFileName)
                .getEmbed();

        ctx.getChannel().sendMessage(confirmation.build()).addFile(new ImageEditor(rankCard).getInputStream(), rankPreviewFileName).queue(message -> {
            // Add reactions to message
            message.addReaction(emojis[0]).queue(); // Checkmark
            message.addReaction(emojis[1]).queue(); // Barrier

            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e -> !e.getUser().isBot()
                            && e.getMember() == ctx.getMember()
                            && e.getMessageIdLong() == message.getIdLong()
                            && Arrays.asList(emojis).contains(e.getReactionEmote().getEmoji()))
                    .setAction(e -> {
                        final String reaction = e.getReactionEmote().getEmoji(); // Get reaction emoji

                        // Confirm purchase
                        if (reaction.equals(emojis[0])) {
                            final GuildMember dbMember = GuildMember.get(e.getMember()); // Get member in database
                            dbMember.setBalance(dbMember.getBalance() - 10000); // Update balance

                            // Success
                            final String rankBackgroundFileName = "rank_background_" + ctx.getAuthor().getId() + ".png";
                            final EmbedBuilder success = info(ctx)
                                    .setDescription(lang(ctx).get("command.leveling.edit.rank.message.success"))
                                    .setImage("attachment://" + rankBackgroundFileName)
                                    .getEmbed();

                            ctx.getChannel().sendMessage(success.build()).addFile(background, rankBackgroundFileName).queue(msg -> {
                                dbMember.setRankBackground(msg.getEmbeds().get(0).getImage().getUrl()); // Save new image in database
                            });
                        }

                        // Cancel purchase
                        else if (reaction.equals(emojis[1])) {
                            info(ctx).setDescription(lang(ctx).get("command.leveling.edit.rank.message.canceled")).send();
                            message.clearReactions().queue();
                        }
                    })
                    .setTimeout(30L, TimeUnit.SECONDS)
                    .setTimeoutAction(() -> message.clearReactions().queue())
                    .load();

        });
    }
}
