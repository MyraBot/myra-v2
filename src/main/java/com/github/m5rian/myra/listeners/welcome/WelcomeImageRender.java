package com.github.m5rian.myra.listeners.welcome;

import com.github.m5rian.myra.utilities.Graphic;
import com.github.m5rian.myra.database.guild.MongoGuild;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class WelcomeImageRender {

    /**
     * @param user  The user who is greeted.
     * @param guild The guild the new user joined.
     */
    public InputStream render(Guild guild, User user) throws Exception {
        //database
        MongoGuild db = MongoGuild.get(guild);
        //get welcome image background
        BufferedImage background;
        //if no background is set
        if (db.getNested("welcome").getString("welcomeImageBackground").equals("not set")) {
            background = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("welcomeImage.png"));
        }
        //if guild has a custom background
        else {
            // Url is available
            try {
                background = ImageIO.read(new URL(db.getNested("welcome").getString("welcomeImageBackground").toString()));
            }
            // Invalid link
            catch (IOException e) {
                background = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("welcomeImage.png"));
            }
        }
        // Get font
        final String fontName = db.getNested("welcome").getString("welcomeImageFont");
        final InputStream font = this.getClass().getClassLoader().getResourceAsStream(fontName + ".ttf"); // Get as input stream
        // Get graphics
        final Graphics graphics = background.getGraphics();
        final Graphics2D graphics2D = (Graphics2D) graphics;
        //enable anti aliasing
        Graphic.enableAntiAliasing(graphics2D);
        //choose format
        if (background.getHeight() > background.getWidth()) {
            portrait(background, user, graphics, graphics2D, font);
        } else {
            landscape(background, user, graphics, graphics2D, font);
        }

        // Return graphic as inputStream
        return Graphic.toInputStream(background);
    }

    private void landscape(BufferedImage background, User user, Graphics graphics, Graphics2D graphics2D, InputStream inputStream) throws Exception {
        //resize avatar
        BufferedImage avatar = Graphic.getAvatar(user.getEffectiveAvatarUrl());
        avatar = Graphic.resizeImage(avatar, background.getWidth() / 5, background.getWidth() / 5);
        //load font
        Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
//draw avatar
        graphics2D.drawImage(
                avatar,
                Graphic.imageCenter('X', avatar, background),
                Graphic.imageCenter('Y', avatar, background) - background.getHeight() / 4,
                null);
        // Draw circle around avatar
        graphics2D.setColor(Color.white);
        graphics2D.setStroke(new BasicStroke(
                background.getHeight() / 200f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));
        graphics2D.drawOval(
                Graphic.imageCenter('X', avatar, background),
                Graphic.imageCenter('Y', avatar, background) - background.getHeight() / 4,
                avatar.getWidth(), avatar.getHeight()
        );
// Draw 'welcome'
        // Set font
        font = font.deriveFont(background.getWidth() / 12.5f);
        graphics.setFont(font);
        //draw 'welcome'
        graphics.drawString("welcome",
                Graphic.textCenter('X', "welcome", font, background),
                Graphic.textCenter('Y', "welcome", font, background) + background.getHeight() / 6
        );
// Draw user name
        final String name = user.getName(); // Get username
        FontRenderContext fontRenderContext = new FontRenderContext(new AffineTransform(), true, true);
        float size = background.getWidth() / 7.5f; // Set default font size
        font = font.deriveFont(size); // Set font size

        // Make font smile smaller if text is to big
        while (Math.round(font.getStringBounds(name, fontRenderContext).getWidth()) > background.getWidth()) {
            size = size - 1.0F; // Make font size smaller
            font = font.deriveFont(size); // Update font
        }

        //set font
        graphics.setFont(font);
        //draw user name
        graphics.drawString(name,
                Graphic.textCenter('X', name, font, background),
                (int) (Graphic.textCenter('Y', name, font, background) + background.getHeight() / 2.25)
        );
    }

    private void portrait(BufferedImage background, User user, Graphics graphics, Graphics2D graphics2D, InputStream inputStream) throws Exception {
        //resize avatar
        BufferedImage avatar = Graphic.getAvatar(user.getEffectiveAvatarUrl());
        avatar = Graphic.resizeImage(avatar, background.getWidth() / 2, background.getWidth() / 2);
        //load font
        Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
//draw avatar
        graphics2D.drawImage(
                avatar,
                Graphic.imageCenter('X', avatar, background),
                Graphic.imageCenter('Y', avatar, background) - background.getHeight() / 4,
                null);
        // Draw circle around avatar
        graphics2D.setColor(Color.white);
        graphics2D.setStroke(new BasicStroke(
                background.getHeight() / 200f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));
        graphics2D.drawOval(
                Graphic.imageCenter('X', avatar, background),
                Graphic.imageCenter('Y', avatar, background) - background.getHeight() / 4,
                avatar.getWidth(), avatar.getHeight()
        );
// Draw 'welcome'
        // Set font
        font = font.deriveFont(background.getWidth() / 5f);
        graphics.setFont(font);
        //draw 'welcome'
        graphics.drawString("welcome",
                Graphic.textCenter('X', "welcome", font, background),
                Graphic.textCenter('Y', "welcome", font, background) + background.getHeight() / 6
        );
// Draw user name
        final String name = user.getName(); // Get username
        FontRenderContext fontRenderContext = new FontRenderContext(new AffineTransform(), true, true);
        float size = background.getWidth() / 4f; // Set default font size
        font = font.deriveFont(size); // Set font size

        // Make font smile smaller if text is to big
        while (Math.round(font.getStringBounds(name, fontRenderContext).getWidth()) > background.getWidth()) {
            size = size - 1.0F; // Make font size smaller
            font = font.deriveFont(size); // Update font
        }

        //set font
        graphics.setFont(font);
        //draw user name
        graphics.drawString(name,
                Graphic.textCenter('X', name, font, background),
                (Graphic.textCenter('Y', name, font, background) + background.getHeight() / 3)
        );
    }
}
