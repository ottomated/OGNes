package net.ottomated.OGNes;

public class Tile {
    int[] pixels;
    boolean[] opaque;
    boolean init;
    int tileIndex;
    int x;

    Tile() {
        pixels = new int[64];
        opaque = new boolean[8];
        init = false;
    }

    void setScanline(int sline, int b1, int b2) {
        init = true;
        tileIndex = sline << 3;
        for (x = 0; x < 8; x++) {
            pixels[tileIndex + x] = ((b1 >> (7 - x)) & 1) + (((b2 >> (7 - x)) & 1) << 1);
            if (pixels[tileIndex + x] == 0) {
                opaque[sline] = false;
            }
        }
    }
}
