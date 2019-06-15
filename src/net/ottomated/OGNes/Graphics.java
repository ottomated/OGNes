package net.ottomated.OGNes;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Graphics extends JFrame {

    private Container pane;
    BufferedImage image;
    int[] framebuffer;

    Graphics() {
        super("OGNes");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        image = new BufferedImage(256, 240, BufferedImage.TYPE_4BYTE_ABGR );
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

    public void writeFrame(int [] buffer){
        for(int  i = 0; i < 256*240; i++) framebuffer[i] = 0xFF000000 | buffer[i];
    }

    public void setImageToFrame(){
        image.setRGB(0, 0, 256, 240, framebuffer, 0, 1);
    }

    @Override
    public void paint(java.awt.Graphics g) {
        setImageToFrame();
        super.paint(g);
        g.drawImage(image, 0, 0, 256, 240, null);
    }
}
