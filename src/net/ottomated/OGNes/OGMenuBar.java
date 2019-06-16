package net.ottomated.OGNes;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;

class OGMenuBar extends JMenuBar {

    private JFrame frame;
    private Nes nes;

    OGMenuBar(JFrame f, Nes n) {
        super();
        frame = f;
        nes = n;
        initFileMenu();
        initEmulateMenu();
        initViewMenu();
    }



    private void initFileMenu() {

        JMenu menu = new JMenu("File");
        JMenuItem rom = new JMenuItem("Load Rom...");
        rom.addActionListener(actionEvent -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            chooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if (file.isDirectory()) return true;
                    String p = file.getPath();
                    int i = p.lastIndexOf(".");
                    if (i == -1) return false;
                    else return p.substring(i).equalsIgnoreCase(".nes");
                }

                @Override
                public String getDescription() {
                    return ".NES files";
                }
            });
            int res = chooser.showDialog(frame, "Load");
            if (res == JFileChooser.APPROVE_OPTION) {
                try {
                    nes.loadRom(chooser.getSelectedFile().getPath());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        JMenuItem tas = new JMenuItem("Load TAS Movie...");
        menu.add(rom);
        menu.add(tas);
        add(menu);
    }

    private void initEmulateMenu() {

        JMenu menu = new JMenu("Emulate");
        JMenuItem hard = new JMenuItem("Reset");
        hard.addActionListener(actionEvent -> {
            try {
                nes.loadRom(nes.romFile.getPath());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        JMenu speed = new JMenu("Speed");
        ButtonGroup fps = new ButtonGroup();
        JMenuItem fps6 = new JRadioButtonMenuItem("6 fps (0.1x)");
        JMenuItem fps30 = new JRadioButtonMenuItem("30 fps (0.5x)");
        JMenuItem fps60 = new JRadioButtonMenuItem("60 fps (1.0x)", true);
        JMenuItem fpsMax = new JRadioButtonMenuItem("MAX");
        fps.add(fps6);
        fps.add(fps30);
        fps.add(fps60);
        fps.add(fpsMax);
        for (AbstractButton b : Collections.list(fps.getElements())) {
            speed.add(b);
            b.addActionListener(actionEvent -> {
                switch(b.getText()) {
                    case "6 fps (0.1x)":
                        Main.fps = 6;
                        break;
                    case "30 fps (0.5x)":
                        Main.fps = 30;
                        break;
                    case "60 fps (1.0x)":
                        Main.fps = 60;
                        break;
                    case "MAX":
                        Main.fps = 60000;
                        break;
                }
            });
        }
        JMenuItem sound = new JCheckBoxMenuItem("Sound (SLOW)", true);
        sound.addActionListener(actionEvent -> nes.sound = sound.isSelected());
        menu.add(hard);
        menu.add(speed);
        menu.add(sound);
        add(menu);
    }

    private void initViewMenu() {

        JMenu menu = new JMenu("View");
        JMenuItem fps = new JCheckBoxMenuItem("Show FPS");
        fps.addActionListener(actionEvent -> nes.graphics.showFps = fps.isSelected());
        JMenu palMenu = new JMenu("Palette");
        ButtonGroup pal = new ButtonGroup();
        JMenuItem pDef = new JRadioButtonMenuItem("Default", true);
        JMenuItem pPal = new JRadioButtonMenuItem("PAL");
        JMenuItem pNtsc = new JRadioButtonMenuItem("NTSC");
        pal.add(pDef);
        pal.add(pPal);
        pal.add(pNtsc);
        for (AbstractButton b : Collections.list(pal.getElements())) {
            palMenu.add(b);
            b.addActionListener(actionEvent -> {
                switch(b.getText()) {
                    case "Default":
                        nes.ppu.palTable.loadDefaultPalette();
                        break;
                    case "PAL":
                        nes.ppu.palTable.loadPALPalette();
                        break;
                    case "NTSC":
                        nes.ppu.palTable.loadNTSCPalette();
                        break;
                }
            });
        }
        menu.add(fps);
        menu.add(palMenu);
        add(menu);
    }

}
