package com.github.m5rian.myra.utilities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageEditor {
    private BufferedImage image;
    private Graphics2D graphics;
    private Font font;
    private float fontSize;

    public ImageEditor(BufferedImage image) {
        this.image = image;
        this.graphics = (Graphics2D) image.getGraphics();
        Graphic.enableAntiAliasing(graphics); // Enable anti aliasing
    }

    public ImageEditor resize(Integer x, Integer y) {
        Image temp = this.image.getScaledInstance(x, y, Image.SCALE_FAST);
        BufferedImage resizedImage = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(temp, 0, 0, null);
        g2d.dispose();

        this.image = resizedImage;
        this.graphics = (Graphics2D) resizedImage.getGraphics();
        return this;
    }

    public ImageEditor resizeSmart(Integer width, Integer height) {
        // Image is too wide
        final float divisor = (float) this.image.getWidth() / width; // Get divisor
        final int newWidth = width;
        final int newHeight = (int) (this.image.getHeight() / divisor); // Calculate new height to remain aspect ratio


        final Image temp = this.image.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST); // Resize image, but keep aspect radio
        final BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB); // Create image with new dimensions

        resizedImage.getGraphics().drawImage(temp, 0, 0, null); // Draw resized image
        resizedImage.getGraphics().dispose();


        final int xStart = (newWidth - width) / 2;
        final int yStart = (newHeight - height) / 2;

        final BufferedImage croppedImage = resizedImage.getSubimage(xStart, yStart, width, height);

        this.image = croppedImage;
        this.graphics = (Graphics2D) croppedImage.getGraphics();
        return this;
    }

    public ImageEditor applyGrayscaleMaskToAlpha(BufferedImage mask) {
        int width = this.image.getWidth();
        int height = this.image.getHeight();

        int[] imagePixels = this.image.getRGB(0, 0, width, height, null, 0, width);
        int[] maskPixels = mask.getRGB(0, 0, width, height, null, 0, width);

        for (int i = 0; i < imagePixels.length; i++) {
            int color = imagePixels[i] & 0x00ffffff; // Mask preexisting alpha
            int alpha = maskPixels[i] << 24; // Shift blue to alpha
            imagePixels[i] = color | alpha;
        }

        this.image.setRGB(0, 0, width, height, imagePixels, 0, width);
        return this;
    }

    public ImageEditor setFont(Font font) {
        this.font = font;
        return this;
    }

    public ImageEditor setFontSize(float size) {
        this.fontSize = size;
        return this;
    }

    public ImageEditor drawString(String text, int x, int y) {
        graphics.setFont(this.font.deriveFont(fontSize));
        final FontMetrics metrics = graphics.getFontMetrics(this.font.deriveFont(this.fontSize));

        graphics.drawString(text,
                Math.round(x - metrics.getStringBounds(text, graphics).getWidth() / 2),
                Math.round(y - metrics.getStringBounds(text, graphics).getHeight() / 2));
        return this;
    }

    public ImageEditor drawString(String text, int x, int y, int maxWidth) {
        // Adjust font size
        Font tempFont = this.font.deriveFont(this.fontSize); // Create font, where I can change the font size without chaning the original font
        float fontSize = this.fontSize; // Font size which can be adjusted

        while (this.graphics.getFontMetrics(tempFont).getStringBounds(text, this.graphics).getWidth() > maxWidth) {
            fontSize = fontSize - 1.0F; // Make font size smaller
            tempFont = tempFont.deriveFont(fontSize); // Update font
        }

        // Draw string
        final FontMetrics metrics = this.graphics.getFontMetrics(tempFont);
        graphics.setFont(tempFont); // Apply temporary font
        graphics.drawString(text,
                Math.round(x - metrics.getStringBounds(text, graphics).getWidth() / 2),
                Math.round(y - metrics.getStringBounds(text, graphics).getHeight() / 2));
        graphics.setFont(this.font); // Go back to original font

        return this;
    }

    public ImageEditor drawLeftString(String text, int x, int y, int maxWidth) {
        // Adjust font size
        Font tempFont = this.font.deriveFont(this.fontSize); // Create font, where I can change the font size without chaning the original font
        float fontSize = this.fontSize; // Font size which can be adjusted

        while (this.graphics.getFontMetrics(tempFont).getStringBounds(text, this.graphics).getWidth() > maxWidth) {
            fontSize = fontSize - 1.0F; // Make font size smaller
            tempFont = tempFont.deriveFont(fontSize); // Update font
        }

        // Draw string
        final FontMetrics metrics = this.graphics.getFontMetrics(tempFont);
        graphics.setFont(tempFont); // Apply temporary font
        graphics.drawString(text,
                Math.round(x),
                Math.round(y - metrics.getStringBounds(text, graphics).getHeight() / 2));
        graphics.setFont(this.font); // Go back to original font

        return this;
    }

    public void drawImage(BufferedImage image, int x, int y) {
        graphics.drawImage(image, x, y, null);
    }

    public void drawStroke(float startX, float startY, float endX, float endY, float width, Color colour) {
        final BasicStroke stroke = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        graphics.setStroke(stroke); // Set stroke type
        graphics.setColor(colour); // Set colour
        graphics.draw(new Line2D.Float(startX, startY, endX, endY)); // Draw stroke
        graphics.setColor(Color.WHITE); // Reset colour
    }

    public Color getCommonColour() {
        Image temp = this.image.getScaledInstance(1, 1, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(temp, 0, 0, null);
        g2d.dispose();

        final int rgb = resizedImage.getRGB(0, 0);
        return new Color(rgb, true);
    }

    public InputStream getInputStream() {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            outStream.flush();
            outStream.close();
            ImageIO.write(this.image, "png", outStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(outStream.toByteArray());
    }

    public BufferedImage getBufferedImage() {
        return this.image;
    }
}
