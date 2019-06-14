package net.ottomated.OGNes;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Graphics extends JFrame {

    private Container pane;
    BufferedImage image;

    Graphics() {
        super("OGNes");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        initGUI();
    }

    private void initGUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pane = getContentPane();
        pane.setLayout(null);
        pane.setPreferredSize(new Dimension(256, 240));
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }



    @Override
    public void paint(java.awt.Graphics g) {
        super.paint(g);
        g.drawImage(image, 0, 0, 256, 240, null);
    }
}
