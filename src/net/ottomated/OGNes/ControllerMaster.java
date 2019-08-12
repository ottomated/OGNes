package net.ottomated.OGNes;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

public class ControllerMaster implements KeyListener {

    Controller[] controllers;
    private Graphics frame;

    ControllerMaster(Graphics g, Controller[] cs) {
        this.frame = g;
        controllers = cs;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_R) {
            try {
                if (frame.nes.romFile == null) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(new File(Main.settings.romPath));
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
                    int res = chooser.showDialog(frame, "Load ROM");
                    if (res == JFileChooser.APPROVE_OPTION) {
                        try {
                            frame.nes.loadRom(chooser.getSelectedFile().getPath());
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    frame.nes.loadRom(frame.nes.romFile.getPath());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        if (!frame.nes.ready) return;
        for (Controller c : controllers) {
            int button = c.getButton(e);
            if (button != -1) {
                c.state[button] = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!frame.nes.ready) return;
        for (Controller c : controllers) {
            int button = c.getButton(e);
            if (button != -1) {
                c.state[button] = false;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
