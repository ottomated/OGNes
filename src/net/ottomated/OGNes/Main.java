package net.ottomated.OGNes;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.*;

public class Main {
    static Nes nes;
    static volatile int fps = 60;
    static Settings settings;
    static File settingsFile;

    static String getDir() throws URISyntaxException {
            return new File(Main.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getParentFile().getPath();
    }

    public static void main(String[] args) throws Exception {
        settingsFile = new File(Paths.get(getDir(), "settings.json").toString());
        if (settingsFile.exists()) {
            try {
                JSONObject json = new JSONObject(new String(Files.readAllBytes(settingsFile.toPath())));
                settings = new Settings(json);
            } catch (JSONException e) {
                settings = new Settings();
                settings.save(settingsFile);
            }
        } else {
            settings = new Settings();
            settings.save(settingsFile);
        }
        fps = new int[]{6, 30, 60, 60000}[settings.speed.ordinal()];
        nes = new Nes();
        startFrameLoop();
    }

    private static void startFrameLoop() {
        long now = System.currentTimeMillis();

        while (true) {
            if (!nes.ready) continue;
            nes.graphics.setFps(System.currentTimeMillis() - now);
            now = System.currentTimeMillis();
            long target = (long) (now + 1000.0 / fps);
            try {
                nes.frame();
            } catch (Exception e) {
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
