package net.ottomated.OGNes;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Controller {

    public boolean[] state;
    private int[] keymap;
    private int index;
    private int strobe;

    class Button {
        static final int A = 0, B = 1, SELECT = 2, START = 3, UP = 4, DOWN = 5, LEFT = 6, RIGHT = 7;
    }

    Controller(int[] k) {
        this.keymap = k;
        state = new boolean[8];
        for (int i = 0; i < state.length; i++) {
            state[i] = false;
        }
    }

    public int read() {
        int value = 0;
        if (index < 8 && state[index]) {
            value = 1;
        }
        index++;
        if ((strobe & 1) == 1) {
            index = 0;
        }
        return value;
    }

    public void write(int value) {
        strobe = value;
        if ((strobe & 1) == 1) {
            index = 0;
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
