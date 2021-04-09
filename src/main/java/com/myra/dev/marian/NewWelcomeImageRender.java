package com.myra.dev.marian;

import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.Graphic;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class NewWelcomeImageRender {

    private final String customText = "custom text here";

    public InputStream render(Guild guild, User user) throws IOException, FontFormatException {
        final var db = new MongoGuild(guild); // Get database
        final var backgroundUrl = db.getNested("welcome").getString("welcomeImageBackground"); // Get welcome image background url

        BufferedImage image;
        // Bo background is set
        if (backgroundUrl.equals("not set")) {
            // Get transparent background
            image = ImageIO.read(this.getClass().getResourceAsStream("welcomeImage.png"));
        }
        // Custom background
        else {
            // Try reading custom background url
            try {
                image = ImageIO.read(new URL(backgroundUrl));
            }
            // If url fails use transparent background instead
            catch (MalformedURLException e) {
                // Get transparent background
                image = ImageIO.read(this.getClass().getResourceAsStream("welcomeImage.png"));
            }
        }

        // Get font
        final var fontName = db.getNested("welcome").getString("welcomeImageFont"); // Get font name
        final var fontFile = this.getClass().getClassLoader().getResourceAsStream(String.format("%s.ttf", fontName));
        var font = Font.createFont(Font.TRUETYPE_FONT, fontFile); // Create font from file

        final var graphics = image.getGraphics();
        final var graphics2D = (Graphics2D) graphics;
        final var fontRenderContext = new FontRenderContext(new AffineTransform(), true, true);


        // Portrait format
        if (image.getHeight() > image.getWidth()) {
            // Get avatar
            final var avatarRaw = Graphic.getAvatar(user.getEffectiveAvatarUrl()); // Get avatar as an image
            final var avatarSize = image.getWidth() / 2; // Get size for avatar
            final var avatar = Graphic.resizeImage(avatarRaw, avatarSize, avatarSize); // Resize avatar
            // Draw avatar
            graphics.drawImage(avatar,
                    Graphic.imageCenter('X', avatar, image),
                    Graphic.imageCenter('Y', avatar, image) - image.getHeight() / 4,
                    null);
        }
        // Landscape format
        else {
            // Get avatar
            final var avatarRaw = Graphic.getAvatar(user.getEffectiveAvatarUrl()); // Get avatar as an image
            final var avatarSize = image.getWidth() / 5; // Get size for avatar
            final var avatar = Graphic.resizeImage(avatarRaw, avatarSize, avatarSize); // Resize avatar
            // Draw avatar
            graphics.drawImage(avatar,
                    Graphic.imageCenter('X', avatar, image),
                    Graphic.imageCenter('Y', avatar, image) - image.getHeight() / 4,
                    null);

            // Draw circle around avatar
            graphics2D.setColor(Color.white);
            graphics2D.setStroke(new BasicStroke(
                    image.getHeight() / 200f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            graphics2D.drawOval(
                    Graphic.imageCenter('X', avatar, image),
                    Graphic.imageCenter('Y', avatar, image) - image.getHeight() / 4,
                    avatar.getWidth(), avatar.getHeight());

            // Draw welcome
            font = font.deriveFont(image.getWidth() / 12.5f);
            graphics.setFont(font);
            graphics.drawString("welcome",
                    Graphic.textCenter('X', "welcome", font, image),
                    Graphic.textCenter('Y', "welcome", font, image) + image.getHeight() / 6
            );

            // Draw name
            float size = image.getWidth() / 7.5f; // Set default font size
            font = font.deriveFont(size); // Apply font size
            // Make font smile smaller if text is to big
            while (Math.round(font.getStringBounds(user.getName(), fontRenderContext).getWidth()) > image.getWidth()) {
                size = size - 1.0F; // Make font size smaller
                font = font.deriveFont(size); // Update font
            }

            graphics.setFont(font);
            graphics.drawString(user.getName(),
                    Graphic.textCenter('X', user.getName(), font, image),
                    Graphic.textCenter('Y', user.getName(), font, image) + image.getHeight() / 7 * 3
            );

            // Draw custom text
            size = image.getWidth() / 15f; // Set default font size
            font = font.deriveFont(size); // Apply font size
            // Make font smile smaller if text is to big
            while (Math.round(font.getStringBounds(customText, fontRenderContext).getWidth()) > image.getWidth()) {
                size = size - 1.0F; // Make font size smaller
                font = font.deriveFont(size); // Update font
            }

            graphics.setFont(font);
            graphics.drawString(customText,
                    Graphic.textCenter('X', customText, font, image),
                    image.getHeight() - image.getHeight() / 50 * 5
            );
        }
        return Graphic.toInputStream(image);
    }
}
