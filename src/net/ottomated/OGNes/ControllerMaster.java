package net.ottomated.OGNes;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ControllerMaster implements KeyListener {

    Controller[] controllers;
    private Graphics frame;

    ControllerMaster(Graphics g, Controller[] cs) {
        this.frame = g;
        controllers = cs;
    }

    @Override
    public void keyPressed(KeyEvent e) {
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
        if ((e.getModifiers() & KeyEvent.CTRL_MASK) == KeyEvent.CTRL_MASK) {
            try {
                frame.nes.loadRom(frame.nes.romFile.getPath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
