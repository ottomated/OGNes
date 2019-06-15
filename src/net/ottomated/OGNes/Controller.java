package net.ottomated.OGNes;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Controller implements KeyListener {

    public int[] state;

    public class Button {
        static final int A = 0, B = 1, SELECT = 2, START = 3, UP = 4, DOWN = 5, LEFT = 6, RIGHT = 7;
    }

    Controller() {
        state = new int[8];
        for (int i = 0; i < state.length; i++) {
            state[i] = 0x40;
        }
    }

    private int getButton(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                return Button.UP;
            case KeyEvent.VK_A:
                return Button.LEFT;
            case KeyEvent.VK_S:
                return Button.DOWN;
            case KeyEvent.VK_D:
                return Button.RIGHT;
            case KeyEvent.VK_SHIFT:
                return Button.SELECT;
            case KeyEvent.VK_ENTER:
                return Button.START;
            case KeyEvent.VK_J:
                return Button.A;
            case KeyEvent.VK_K:
                return Button.B;
            default:
                return -1;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int button = getButton(e);
        if (button != -1)
            state[button] = 0x41;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int button = getButton(e);
        if (button != -1)
            state[button] = 0x40;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
