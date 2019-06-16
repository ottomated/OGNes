package net.ottomated.OGNes;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ControllerMaster implements KeyListener {

    Controller[] controllers;

    ControllerMaster(Controller[] cs) {
        controllers = cs;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        for (Controller c : controllers) {
            int button = c.getButton(e);
            if (button != -1) {
                c.state[button] = 0x41;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        for (Controller c : controllers) {
            int button = c.getButton(e);
            if (button != -1) {
                c.state[button] = 0x40;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
