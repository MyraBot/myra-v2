package com.github.m5rian.myra.commands.member.leveling;

import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.guild.member.GuildMember;
import com.github.m5rian.myra.listeners.leveling.Leveling;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Graphic;
import com.github.m5rian.myra.utilities.ImageEditor;
import com.github.m5rian.myra.utilities.Utilities;
import net.dv8tion.jda.api.entities.Member;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Rank implements CommandHandler {
    public static final Integer imageWidth = 500; // Width of rank card
    public static final Integer imageHeight = 150; // Height of rank card

    public static BufferedImage renderRankCard(Member member, BufferedImage background) throws IOException, FontFormatException {
        final GuildMember guildMember = GuildMember.get(member);
        final String level = String.valueOf(guildMember.getLevel());
        final String xp = String.valueOf(guildMember.getXp());
        final String rank = String.valueOf(guildMember.getRank());
        final String messages = String.valueOf(guildMember.getMessages());

        // Background
        final ImageEditor backgroundEditor = new ImageEditor(background);
        final Color accentColour = backgroundEditor.getCommonColour(); // Get most used colour in image

        final int start = 20; // Start of xp bar in pixels
        final int end = 334; // End of xp bars in pixels
        final int length = end - start; // Length of xp bar in pixels

        final long xpOfNextLevel = Leveling.getXpFromLevel(guildMember.getLevel() + 1); // Get xp of the next level
        final long xpCurrent = guildMember.getXp(); // Get current xp of member
        final double percentage = (double) xpCurrent / xpOfNextLevel;
        final double barLength = length * percentage; // Get length of bar

        backgroundEditor.drawStroke(20, 132 - 2, 334, 132 - 2, 6f, Color.WHITE); // Xp bar background
        backgroundEditor.drawStroke(20, 132 - 2, (float) (start + barLength), 132 - 2, 6f, accentColour); // Xp bar value

        backgroundEditor.applyGrayscaleMaskToAlpha(ImageIO.read(Rank.class.getClassLoader().getResourceAsStream("rank/Mask-Background.png"))); // Apply mask
        // Avatar
        final BufferedImage avatar = ImageIO.read(new URL(member.getUser().getEffectiveAvatarUrl()));
        final ImageEditor avatarEditor = new ImageEditor(avatar).resize(75, 75); // Resize to right size
        avatarEditor.applyGrayscaleMaskToAlpha(ImageIO.read(Rank.class.getClassLoader().getResourceAsStream("rank/profile-picture-mask.png"))); // Apply mask

        // Create font
        final Font font = Font.createFont(Font.TRUETYPE_FONT, Rank.class.getClassLoader().getResourceAsStream("fonts/rubik/Rubik-Regular.ttf"));

        // Draw avatar
        backgroundEditor.drawImage(avatarEditor.getBufferedImage(),
                25,
                Graphic.imageCenter('Y', avatarEditor.getBufferedImage(), background));

        backgroundEditor.setFont(font); // Set font
        // Draw titles
        backgroundEditor.setFontSize(15f);
        //backgroundEditor.drawLeftString(member.getEffectiveName(), 26, 45, 250); // User name
        backgroundEditor.drawString("Level", 150, imageHeight / 2, 100); // Level
        backgroundEditor.drawString("Xp", 250, imageHeight / 2, 100); // Xp
        backgroundEditor.drawString("Rank", imageWidth - 77, 45, 100); // Rank
        backgroundEditor.drawString("Messages", imageWidth - 77, imageHeight - 45, 100); // Message count
        // Draw variables
        backgroundEditor.setFontSize(30f);
        backgroundEditor.drawString(level, 150, imageHeight / 2 + 35, 100); // Level
        backgroundEditor.drawString(xp, 250, imageHeight / 2 + 35, 100); // Xp
        backgroundEditor.drawString(rank, imageWidth - 77, 80, 100); // Rank
        backgroundEditor.drawString(messages, imageWidth - 77, imageHeight - 5, 100); // Message count

        return backgroundEditor.getBufferedImage();
    }

    @CommandEvent(
            name = "rank",
            aliases = {"level"},
            args = "(member)",
            emoji = "\uD83C\uDFC5",
            description = "description.leveling.rank"
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length > 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("edit rank")
                    .addUsages(new Usage()
                            .setUsage("rank <member>")
                            .setEmoji("\uD83C\uDFC5")
                            .setDescription(lang(ctx).get("description.leveling.rank")))
                    .send();
            return;
        }

        Member member = ctx.getMember(); // Get self member
        // If user is given
        if (ctx.getArguments().length == 1) {
            member = Utilities.getMember(ctx.getEvent(), ctx.getArguments()[0], "rank", "\uD83C\uDFC5");
            if (member == null) return;
        }

        // Member is bot
        if (member.getUser().isBot()) {
            error(ctx).setDescription(lang(ctx).get("command.leveling.rank.error.bot")).send();
            return;
        }

        final GuildMember getMember = GuildMember.get(ctx.getMember()); // Get member in database
        final String backgroundUrl = getMember.getRankBackground(); // Get current rank background

        final BufferedImage background;
        switch (backgroundUrl) {
            case "default" -> background = ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("rank/Default-Background.png")));
            default -> background = ImageIO.read(new URL(backgroundUrl));
        }

        final String level = String.valueOf(getMember.getLevel()); // Get level
        final BufferedImage rankCard = renderRankCard(member, background); // Get rank card

        // Send rank card
        ctx.getChannel()
                .sendMessage(lang(ctx).get("command.leveling.rank.message.success")
                        .replace("{$member}", member.getAsMention()) // Member
                        .replace("{$level}", level)) // Member level
                .addFile(Graphic.toInputStream(rankCard), member.getUser().getName().toLowerCase() + "_rank.png")
                .queue();
    }

}
