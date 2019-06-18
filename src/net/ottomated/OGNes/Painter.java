package net.ottomated.OGNes;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Painter extends JPanel {
    private BufferedImage img;
    String fps = "";

    Painter() {
        super();
        img = new BufferedImage(256, 240, BufferedImage.TYPE_INT_ARGB);
        try {
            img = ImageIO.read(getClass().getResource("/resources/splash.png"));
        } catch (Exception ignored) {
        }
    }

    void draw(int[] buffer) {
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] |= 0xff000000;
        }
        img.setRGB(0, 0, 256, 240, buffer, 0, 256);
        repaint();
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(img, 0, 0, 256 * Main.settings.scale, 240 * Main.settings.scale, null);
        Font f = new Font(Font.MONOSPACED, Font.PLAIN, 20);
        g2.setFont(f);
        g2.drawString(fps, 10, 20);
    }
}
