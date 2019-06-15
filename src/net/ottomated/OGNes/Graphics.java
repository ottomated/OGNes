package net.ottomated.OGNes;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Graphics extends JFrame {
    public static final int SCALE = 3;

    private Container pane;
    Painter painter;
    int[] framebuffer;

    Graphics() {
        super("OGNes");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        framebuffer = new int[256 * 240];
        initGUI();
    }

    private void initGUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pane = getContentPane();
        painter = new Painter();
        pane.add(painter);
        pane.setPreferredSize(new Dimension(256 * SCALE, 240 * SCALE));
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    public void writeFrame(int[] buffer) {
        if (painter != null)
            painter.draw(buffer);
    }
}
