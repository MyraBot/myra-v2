package com.github.m5rian.myra.commands.member.leveling;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.database.guild.member.GuildMember;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Img;
import com.github.m5rian.myra.utilities.Utilities;
import static com.github.m5rian.myra.utilities.language.Lang.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Background implements CommandHandler {
    private final String[] emojis = {
            "\u2705", // Checkmark
            "\uD83D\uDEAB" // Barrier
    };


    @CommandEvent(
            name = "edit rank",
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

        final MongoGuild db = new MongoGuild(ctx.getGuild()); // Get database
        // Not enough money
        if (db.getMembers().getMember(ctx.getMember()).getBalance() < 10000) {
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

        final BufferedImage rankBackground = new Img(backgroundRaw).resize(350, 100).getBufferedImage(); // Resize background
        final Img rank = new Img(new Rank().rankCard(ctx.getMember(), rankBackground)); // Get rank card

        final Img background = new Img(backgroundRaw).resize(350, 100); // Resize background

        ctx.getChannel().sendFile(background.getInputStream(), "background.png").queue();
        ctx.getChannel().sendFile(rank.getInputStream(), "rank.png").queue();
        ctx.getChannel().sendFile(background.getInputStream(), "background.png").queue();

        // Confirmation
        Success confirmation = new Success(ctx.getEvent())
                .setCommand("edit rank")
                .setMessage(lang(ctx).get("command.leveling.edit.rank.message.confirmation")
                        .replace("{$currency}", db.getNested("economy").getString("currency"))) // Guild currency
                .setImage("attachment://rank.png");
        ctx.getChannel().sendFile(rank.getInputStream(), "rank.png").embed(confirmation.getEmbed().build()).queue(message -> {
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

                        // Checkmark
                        if (reaction.equals(emojis[0])) {
                            final GuildMember dbMember = db.getMembers().getMember(e.getMember()); // Get member in database
                            dbMember.setBalance(dbMember.getBalance() - 10000); // Update balance
                            // Send success
                            EmbedBuilder success = new EmbedBuilder()
                                    .setAuthor("edit rank", null, ctx.getAuthor().getEffectiveAvatarUrl())
                                    .setColor(Utilities.blue)
                                    .setDescription(lang(ctx).get("command.leveling.edit.rank.message.success"))
                                    .setImage("attachment://background.png");
                            try {
                                e.getChannel().sendFile(background.getInputStream(), "background.png").embed(success.build()).queue(msg -> {
                                    dbMember.setRankBackground(msg.getEmbeds().get(0).getImage().getUrl()); // Save new image in database
                                });
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }

                        // Barrier
                        else if (reaction.equals(emojis[1])) {
                            // Send cancel success
                            new Success(ctx.getEvent())
                                    .setCommand("edit rank")
                                    .setEmoji("\uD83D\uDDBC")
                                    .setAvatar(e.getUser().getEffectiveAvatarUrl())
                                    .setMessage(lang(ctx).get("command.leveling.edit.rank.message.canceled"))
                                    .send();
                        }
                    })
                    .setTimeout(30L, TimeUnit.SECONDS)
                    .setTimeoutAction(() -> message.clearReactions().queue())
                    .load();

        });
    }
}
