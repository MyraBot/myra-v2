package com.github.m5rian.myra.utilities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Img {
    private BufferedImage image;

    public Img(BufferedImage image) {
        this.image = image;
    }

    public Img resize(Integer x, Integer y) {
        Image temp = this.image.getScaledInstance(x, y, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(temp, 0, 0, null);
        g2d.dispose();

        this.image = resizedImage;
        return this;
    }

    public InputStream getInputStream() throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        outStream.flush();
        outStream.close();
        ImageIO.write(this.image, "png", outStream);
        return new ByteArrayInputStream(outStream.toByteArray());
    }

    public BufferedImage getBufferedImage() {
        return this.image;
    }
}
