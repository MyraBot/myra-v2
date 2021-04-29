package com.myra.dev.marian.commands.leveling;

import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.database.guild.member.GuildMember;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.listeners.leveling.Leveling;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Graphic;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Rank implements CommandHandler {
    private final Leveling leveling = new Leveling();

    @SuppressWarnings("ConstantConditions")

@CommandEvent(
        name = "rank"
)
    public void execute(CommandContext ctx) throws Exception {
        Utilities utilities = Utilities.getUtils();
        // Usage
        if (ctx.getArguments().length > 1) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("rank", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "rank <user>`", "\uD83C\uDFC5 │ Shows the rank of a user", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
// Show rank
        // Get self user
        Member member = ctx.getMember();
        // If user is given
        if (ctx.getArguments().length == 1) {
            Member mentionedMember = utilities.getMember(ctx.getEvent(), ctx.getArguments()[0], "rank", "\uD83C\uDFC5");
            if (mentionedMember == null) return;
            member = mentionedMember;
        }
        //if member is bot
        if (member.getUser().isBot()) {
            new Error(ctx.getEvent())
                    .setCommand("rank")
                    .setEmoji("\uD83C\uDFC5")
                    .setMessage("Bots aren't allowed to participate in the ranking competition")
                    .send();
            return;
        }
        final GuildMember getMember = new MongoGuild(member.getGuild()).getMembers().getMember(member); // Get member in database

        String backgroundUrl = getMember.getRankBackground();
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
                .sendMessage("> " + member.getAsMention() + "**, you're level " + level + "**")
                .addFile(Graphic.getInstance().toInputStream(rankCard), member.getUser().getName().toLowerCase() + "_rank.png")
                .queue();
    }

    public BufferedImage rankCard(Member member, BufferedImage background) throws IOException, FontFormatException {
        final GuildMember getMember = new MongoGuild(member.getGuild()).getMembers().getMember(member); // Get member in database

        // Get variables
        String level = String.valueOf(getMember.getLevel());
        int xp = getMember.getXp();
        int requiredXpForNextLevel = leveling.requiredXpForNextLevel(member.getGuild(), member);
        int rank = getMember.getRank();
        // Get rank background
        BufferedImage rankCard = background;

        Graphic graphic = Graphic.getInstance();

        BufferedImage avatar = graphic.getAvatar(member.getUser().getEffectiveAvatarUrl());
        //resize avatar
        avatar = graphic.resizeSquaredImage(avatar, 0.5f);
        //graphics
        Graphics graphics = background.getGraphics();
        Graphics2D graphics2D = (Graphics2D) graphics;
        //enable anti aliasing
        graphic.enableAntiAliasing(graphics);
        //load font
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("default.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
        //draw avatar
        graphics2D.drawImage(
                avatar,
                graphic.imageCenter('X', avatar, background) - 125,
                graphic.imageCenter('Y', avatar, background),
                null);
        //draw circle around avatar
        graphics2D.setColor(Color.white);
        graphics2D.setStroke(new BasicStroke(
                2.5f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));
        graphics2D.drawOval(
                graphic.imageCenter('X', avatar, background) - 125,
                graphic.imageCenter('Y', avatar, background),
                avatar.getWidth(), avatar.getHeight()
        );
// Level
        //adjust font size
        font = font.deriveFont(15f);
        graphics.setFont(font);
        //draw 'level'
        graphics.drawString("level:",
                graphic.textCenter('X', level, font, background) - 50,
                graphic.textCenter('Y', level, font, background) - 15
        );
        //adjust font size
        font = font.deriveFont(50f);
        graphics.setFont(font);
        //draw level
        graphics.drawString(level,
                graphic.textCenter('X', level, font, background) - 40,
                graphic.textCenter('Y', level, font, background) + 50
        );
// Xp
        //adjust font size
        font = font.deriveFont(15f);
        graphics.setFont(font);
        //draw 'xp'
        graphics.drawString("xp:",
                graphic.textCenter('X', "xp:", font, background) + 30,
                graphic.textCenter('Y', "xp:", font, background)
        );
        //draw xp
        graphics.drawString(xp + " / " + requiredXpForNextLevel,
                graphic.textCenter('X', "xp:", font, background) + 75,
                graphic.textCenter('Y', "xp:", font, background)
        );
// Rank
        //adjust font size
        font = font.deriveFont(15f);
        graphics.setFont(font);
        //draw 'rank'
        graphics.drawString("rank:",
                graphic.textCenter('X', "rank:", font, background) + 35,
                graphic.textCenter('Y', "rank:", font, background) + 25
        );
        //draw rank
        graphics.drawString("#" + rank,
                graphic.textCenter('X', "rank:", font, background) + 85,
                graphic.textCenter('Y', "rank:", font, background) + 25
        );
        return background;
    }
}
