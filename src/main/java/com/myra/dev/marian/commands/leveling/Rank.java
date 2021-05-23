package com.myra.dev.marian.commands.leveling;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.database.guild.member.GuildMember;
import com.myra.dev.marian.listeners.leveling.Leveling;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Graphic;
import com.myra.dev.marian.utilities.Utilities;
import static com.myra.dev.marian.utilities.language.Lang.*;
import net.dv8tion.jda.api.entities.Member;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Rank implements CommandHandler {
    @CommandEvent(
            name = "rank"
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
            new Error(ctx.getEvent())
                    .setCommand("rank")
                    .setEmoji("\uD83C\uDFC5")
                    .setMessage(lang(ctx).get("command.leveling.rank.error.bot"))
                    .send();
            return;
        }

        final GuildMember getMember = new MongoGuild(member.getGuild()).getMembers().getMember(member); // Get member in database
        final String backgroundUrl = getMember.getRankBackground();
        BufferedImage background;
        // No background set
        if (backgroundUrl.equals("default")) {
            background = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("defaultRank.png"));
        }
        // Custom background
        else {
            background = ImageIO.read(new URL(backgroundUrl));
        }

        final String level = String.valueOf(getMember.getLevel()); // Get level
        final BufferedImage rankCard = rankCard(member, background); // Get rank card

        // Send rank card
        ctx.getChannel()
                .sendMessage(lang(ctx).get("command.leveling.rank.message.success")
                        .replace("{$member}", member.getAsMention()) // Member
                        .replace("{$level}", level)) // Member level
                .addFile(Graphic.toInputStream(rankCard), member.getUser().getName().toLowerCase() + "_rank.png")
                .queue();
    }

    public BufferedImage rankCard(Member member, BufferedImage background) throws IOException, FontFormatException {
        final GuildMember getMember = new MongoGuild(member.getGuild()).getMembers().getMember(member); // Get member in database

        // Get variables
        String level = String.valueOf(getMember.getLevel());
        long xp = getMember.getXp();
        int requiredXpForNextLevel = Leveling.requiredXpForNextLevel(member.getGuild(), member);
        int rank = getMember.getRank();

        BufferedImage avatar = Graphic.getAvatar(member.getUser().getEffectiveAvatarUrl()); // Get rank background
        avatar = Graphic.resizeSquaredImage(avatar, 0.5f); // Resize avatar
        //graphics
        Graphics graphics = background.getGraphics();
        Graphics2D graphics2D = (Graphics2D) graphics;

        Graphic.enableAntiAliasing(graphics);// Enable anti aliasing
        //load font
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("default.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
        //draw avatar
        graphics2D.drawImage(
                avatar,
                Graphic.imageCenter('X', avatar, background) - 125,
                Graphic.imageCenter('Y', avatar, background),
                null);
        //draw circle around avatar
        graphics2D.setColor(Color.white);
        graphics2D.setStroke(new BasicStroke(
                2.5f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));
        graphics2D.drawOval(
                Graphic.imageCenter('X', avatar, background) - 125,
                Graphic.imageCenter('Y', avatar, background),
                avatar.getWidth(), avatar.getHeight()
        );
// Level
        //adjust font size
        font = font.deriveFont(15f);
        graphics.setFont(font);
        //draw 'level'
        graphics.drawString("level:",
                Graphic.textCenter('X', level, font, background) - 50,
                Graphic.textCenter('Y', level, font, background) - 15
        );
        //adjust font size
        font = font.deriveFont(50f);
        graphics.setFont(font);
        //draw level
        graphics.drawString(level,
                Graphic.textCenter('X', level, font, background) - 40,
                Graphic.textCenter('Y', level, font, background) + 50
        );
// Xp
        //adjust font size
        font = font.deriveFont(15f);
        graphics.setFont(font);
        //draw 'xp'
        graphics.drawString("xp:",
                Graphic.textCenter('X', "xp:", font, background) + 30,
                Graphic.textCenter('Y', "xp:", font, background)
        );
        //draw xp
        graphics.drawString(xp + " / " + requiredXpForNextLevel,
                Graphic.textCenter('X', "xp:", font, background) + 75,
                Graphic.textCenter('Y', "xp:", font, background)
        );
// Rank
        //adjust font size
        font = font.deriveFont(15f);
        graphics.setFont(font);
        //draw 'rank'
        graphics.drawString("rank:",
                Graphic.textCenter('X', "rank:", font, background) + 35,
                Graphic.textCenter('Y', "rank:", font, background) + 25
        );
        //draw rank
        graphics.drawString("#" + rank,
                Graphic.textCenter('X', "rank:", font, background) + 85,
                Graphic.textCenter('Y', "rank:", font, background) + 25
        );
        return background;
    }
}
