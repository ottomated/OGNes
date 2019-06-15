package net.ottomated.OGNes;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Painter extends JPanel {
    private BufferedImage img;
    private Graphics2D canvas;

    Painter() {
        super();
        img = new BufferedImage(256 * Graphics.SCALE, 240 * Graphics.SCALE, BufferedImage.TYPE_INT_ARGB);
        canvas = img.createGraphics();
        canvas.setBackground(Color.black);
        canvas.fillRect(0, 0, 256 * Graphics.SCALE, 240 * Graphics.SCALE);
    }

    void draw(int[] buffer) {
        for (int y = 0; y < 240; y++) {
            for (int x = 0; x < 256; x++) {
                Color c = new Color(buffer[y * 256 + x]);
                canvas.setColor(c);
                if(c.getRed() != 0 || c.getBlue() != 0 || c.getGreen() != 0)
                    //System.out.println(x + ", " + y);
                canvas.fillRect(x * Graphics.SCALE, y * Graphics.SCALE, Graphics.SCALE, Graphics.SCALE);
            }
        }
        //canvas.setColor(Color.black);
        //canvas.fillRect(0, 0, 10, 10);
        repaint();
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(img, 0, 0, null);
    }
}
