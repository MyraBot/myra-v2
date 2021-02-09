package com.myra.dev.marian.utilities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Graphic {
    private static final Graphic GRAPHIC = new Graphic();

    public static Graphic getInstance() {
        return GRAPHIC;
    }


    /**
     * Enable anti aliasing.
     *
     * @param g The Graphics Object.
     */
    public static void enableAntiAliasing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
    }

/*    //enable anti aliasing for Graphics 2D
    public void enableAntiAliasing(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
    }*/

    /**
     * Get a avatar as a BufferedImage.
     *
     * @param avatarUrl The URL of the avatar.
     * @return Returns the avatar as a BufferedImage.
     * @throws IOException
     */
    public static BufferedImage getAvatar(String avatarUrl) throws IOException {
        // Read image from URL
        BufferedImage avatar = ImageIO.read(new URL(avatarUrl));

        int diameter = Math.min(avatar.getWidth(), avatar.getHeight());
        BufferedImage mask = new BufferedImage(avatar.getWidth(), avatar.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = mask.createGraphics();
        applyQualityRenderingHints(g2d);
        g2d.fillOval(0, 0, diameter - 1, diameter - 1);
        g2d.dispose();

        BufferedImage result;
        result = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        g2d = result.createGraphics();
        applyQualityRenderingHints(g2d);
        int x = (diameter - avatar.getWidth()) / 2;
        int y = (diameter - avatar.getHeight()) / 2;
        g2d.drawImage(avatar, x, y, null);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));
        g2d.drawImage(mask, 0, 0, null);
        g2d.dispose();
        // Return Avatar
        return result;
    }

    /**
     * Center an text on a background.
     *
     * @param axis       The axis you want to calculate the middle.
     * @param text       The text you want to draw on the background.
     * @param font       The font you use for the text.
     * @param background background you want to draw the image on.
     * @return Returns the coordinates of the middle of the axis you chose.
     */
    public static int textCenter(char axis, String text, Font font, BufferedImage background) {
        FontRenderContext fontRenderContext = new FontRenderContext(new AffineTransform(), true, true);

        int center = 0;
        // X axis
        if (axis == 'X') {
            int xCenterText = (int) Math.round(font.getStringBounds(text, fontRenderContext).getWidth() / 2);
            int xCenterBackground = background.getWidth() / 2;
            center = xCenterBackground - xCenterText;
        }
        // Y axis
        else if (axis == 'Y') {
            int yCenterText = (int) Math.round(font.getStringBounds(text, fontRenderContext).getHeight() / 2);
            int yCenterBackground = background.getHeight() / 2;
            center = yCenterBackground - yCenterText;
        }
        return center;
    }

    /**
     * Center an image on a background.
     *
     * @param axis       The axis you want to calculate the middle.
     * @param image      The image you want to draw on the background.
     * @param background The background you want to draw the image on.
     * @return Returns the coordinates of the middle of the axis you chose.
     */
    public static int imageCenter(char axis, BufferedImage image, BufferedImage background) {
        int center = 0;
        //x
        if (axis == 'X') {
            int xCenterText = Math.round(image.getWidth() / 2);
            int xCenterBackground = background.getWidth() / 2;
            center = xCenterBackground - xCenterText;
        }
        //y
        else if (axis == 'Y') {
            int yCenterText = Math.round(image.getHeight() / 2);
            int yCenterBackground = background.getHeight() / 2;
            center = yCenterBackground - yCenterText;
        }
        return center;
    }

    /**
     * Resize an squared BufferedImage.
     *
     * @param image  The raw BufferedImage.
     * @param amount How much the image should be scaled.
     * @return Returns a resized BufferedImage.
     */
    public static BufferedImage resizeSquaredImage(BufferedImage image, float amount) {
        int endSize = Math.round(image.getWidth() * amount);

        Image temp = image.getScaledInstance(endSize, endSize, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(endSize, endSize, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(temp, 0, 0, null);
        g2d.dispose();

        return resizedImage;
    }

    /**
     * Resize an image using custom X and Y values.
     *
     * @param image The image, which should be resized.
     * @param x     New width.
     * @param y     New height.
     * @return Returns a resized image as a BufferedImage Object.
     */
    public static BufferedImage resizeImage(BufferedImage image, Integer x, Integer y) {
        Image temp = image.getScaledInstance(x, y, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(temp, 0, 0, null);
        g2d.dispose();

        return resizedImage;
    }

    /**
     * Apply rendering hints
     *
     * @param g2d Graphic2D Object.
     */
    public static void applyQualityRenderingHints(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    /**
     * Convert a Image Object to an BufferedImage Object.
     *
     * @param img The Image you want to convert.
     * @return Returns a BufferedImage.
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public static InputStream toInputStream(BufferedImage image) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        outStream.flush();
        outStream.close();
        ImageIO.write(image, "png", outStream);
        return new ByteArrayInputStream(outStream.toByteArray());
    }
}
