package com.myra.dev.marian.commands.leveling;

import com.myra.dev.marian.Myra;
import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.database.allMethods.GetMember;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Img;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@CommandSubscribe(
        name = "edit rank"
)
public class Background implements Command {
    private final String[] emojis = {
            "\u2705", // Checkmark
            "\uD83D\uDEAB" // Barrier
    };

    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("edit rank", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "edit rank <url>`", "\uD83D\uDDBC │ Set a custom rank background", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
        Database db = new Database(ctx.getGuild()); // Get database
        // Not enough money
        if (db.getMembers().getMember(ctx.getMember()).getBalance() < 10000) {
            new Error(ctx.getEvent())
                    .setCommand("edit rank")
                    .setEmoji("\uD83D\uDDBC")
                    .setMessage(String.format("You don't have enough money. You need 10 000%n", db.getNested("economy").getString("currency")))
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
                    .setMessage("Invalid image")
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
        EmbedBuilder confirmation = new EmbedBuilder()
                .setAuthor("edit rank", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("Do you want to buy this background for 10000" + db.getNested("economy").getString("currency"))
                .setImage("attachment://rank.png");
        ctx.getChannel().sendFile(rank.getInputStream(), "rank.png").embed(confirmation.build()).queue(message -> {
            // Add reactions to message
            message.addReaction(emojis[0]).queue(); // Checkmark
            message.addReaction(emojis[1]).queue(); // Barrier

            Myra.WAITER.waitForEvent(
                    GuildMessageReactionAddEvent.class,
                    e -> !e.getUser().isBot()
                            && e.getMember() == ctx.getMember()
                            && e.getMessageIdLong() == message.getIdLong()
                            && Arrays.stream(emojis).anyMatch(e.getReactionEmote().getEmoji()::equals),
                    e -> { // On event
                        final String reaction = e.getReactionEmote().getEmoji(); // Get reaction emoji

                        // Checkmark
                        if (reaction.equals(emojis[0])) {
                            final GetMember dbMember = db.getMembers().getMember(e.getMember()); // Get member in database
                            dbMember.setBalance(dbMember.getBalance() - 10000); // Update balance
                            // Send success
                            EmbedBuilder success = new EmbedBuilder()
                                    .setAuthor("edit rank", null, ctx.getAuthor().getEffectiveAvatarUrl())
                                    .setColor(Utilities.getUtils().blue)
                                    .setDescription("You bought a new rank background:")
                                    .setImage("attachment://background.png");
                            try {
                                e.getChannel().sendFile(background.getInputStream(), "background.png").embed(success.build()).queue(msg -> {
                                    dbMember.setString("rankBackground", msg.getEmbeds().get(0).getImage().getUrl()); // Save new image in database
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
                                    .setMessage("Your purchase has been canceled")
                                    .send();
                        }
                    },
                    30L, TimeUnit.SECONDS, // Timeout
                    () -> { // On timeout
                        message.clearReactions().queue(); // Clear reactions
                    }
            );
        });
    }
}
