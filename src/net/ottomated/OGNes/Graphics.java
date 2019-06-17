package net.ottomated.OGNes;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Graphics extends JFrame {
    private Painter painter;
    private int[] fpsHistory;
    private int fpsCursor;
    volatile boolean showFps;


    Graphics(Nes nes) {
        super("OGNes");
        fpsHistory = new int[10];
        fpsCursor = 0;
        setJMenuBar(new OGMenuBar(this, nes));
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        initGUI();
    }

    private void initGUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container pane = getContentPane();
        painter = new Painter();
        pane.add(painter);
        pane.setPreferredSize(new Dimension(256 * Main.settings.scale, 240 * Main.settings.scale));
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    void writeFrame(int[] buffer) {
        if (painter != null)
            painter.draw(buffer);
    }
    void setFps(long between) {
        if (!showFps) return;
        fpsHistory[fpsCursor] = (int) (1000 / between);
        fpsCursor ++;
        if (fpsCursor > 9) fpsCursor = 0;
        int sum = 0;
        for (int f : fpsHistory) {
            sum += f;
        }
        painter.fps = (sum / fpsHistory.length) + " fps";
    }
}
