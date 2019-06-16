package net.ottomated.OGNes;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.io.IOException;

public class Main {
    static Nes nes;
    static volatile int fps = 60;
    public static void main(String[] args) throws IOException, InterruptedException, LineUnavailableException {
        nes = new Nes();
        Main.startFrameLoop();
        /*
        while (true) {
            long now = System.currentTimeMillis();
            long target = (long) (now + 1000.0 / 60.0);
            nes.frame();
            long diff = target - System.currentTimeMillis();
            if (diff > 0) {
                Thread.sleep(diff);
            } else {
                System.out.println(diff);
            }
        }*/
    }
    public static void startFrameLoop() {
        long now = System.currentTimeMillis();

        while (true) {
            if (!nes.ready) continue;
            nes.graphics.setFps(System.currentTimeMillis() - now);
            now = System.currentTimeMillis();
            long target = (long) (now + 1000.0 / fps);
            try {
                nes.frame();
            } catch(Exception e) {
                JOptionPane.showMessageDialog(nes.graphics, e.toString(), "Emulation Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                nes.ready = false;
            }
            long diff = target - System.currentTimeMillis();
            if (diff > 0) {
                try {
                    Thread.sleep(diff);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
