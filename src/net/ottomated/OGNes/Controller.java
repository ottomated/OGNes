package net.ottomated.OGNes;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Controller {

    public int[] state;
    int[] keymap;

    class Button {
        static final int A = 0, B = 1, SELECT = 2, START = 3, UP = 4, DOWN = 5, LEFT = 6, RIGHT = 7;
    }

    Controller(int[] k) {
        this.keymap = k;
        state = new int[8];
        for (int i = 0; i < state.length; i++) {
            state[i] = 0x40;
        }
    }

    int getButton(KeyEvent e) {
        int c = e.getKeyCode();
        if (c == keymap[0])
            return Button.UP;
        if (c == keymap[1])
            return Button.LEFT;
        if (c == keymap[2])
            return Button.DOWN;
        if (c == keymap[3])
            return Button.RIGHT;
        if (c == keymap[4])
            return Button.SELECT;
        if (c == keymap[5])
            return Button.START;
        if (c == keymap[6])
            return Button.A;
        if (c == keymap[7])
            return Button.B;
        return -1;
    }
}
