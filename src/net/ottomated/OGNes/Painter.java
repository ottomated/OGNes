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
            img = ImageIO.read(new File("assets/splash.png"));
        } catch (Exception ignored) {
        }
    }

    void draw(int[] buffer) {
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] |= 0xff000000;
        }
        //System.out.println(Arrays.toString(buffer));
        img.setRGB(0, 0, 256, 240, buffer, 0, 256);
        /*canvas.setColor(Color.black);
        canvas.fillRect(0, 0, 256 * Graphics.SCALE, 240 * Graphics.SCALE);
        for (int y = 0; y < 240; y++) {
            for (int x = 0; x < 256; x++) {
                Color c = new Color(buffer[y * 256 + x]);
                canvas.setColor(c);
                if(c.getRed() != 0 || c.getBlue() != 0 || c.getGreen() != 0)
                    //System.out.println(x + ", " + y);
                canvas.fillRect(x * Graphics.SCALE, y * Graphics.SCALE, Graphics.SCALE, Graphics.SCALE);
            }
        }*/
        repaint();
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(img, 0, 0, 256 * Graphics.SCALE, 240 * Graphics.SCALE, null);
        Font f = new Font(Font.MONOSPACED, Font.PLAIN, 20);
        g2.setFont(f);
        g2.drawString(fps, 10, 20);
    }
}
