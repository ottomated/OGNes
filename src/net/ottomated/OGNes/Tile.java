package net.ottomated.OGNes;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Tile {
    int[] pixels;
    boolean[] opaque;
    boolean init;
    int tileIndex;
    int x;
    int fbIndex;
    int tIndex;
    int y;
    int w;
    int h;
    int incX;
    int incY;
    int palIndex;
    int tpri;
    int c;

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

    public void render(
            int[] buffer,
            int srcx1,
            int srcy1,
            int srcx2,
            int srcy2,
            int dx,
            int dy,
            int palAdd,
            int[] palette,
            boolean flipHorizontal,
            boolean flipVertical,
            int pri,
            int [] priTable
            ) {
        if (dx < -7 || dx >= 256 || dy < -7 || dy >= 240) {
            return;
        }

        this.w = srcx2 - srcx1;
        this.h = srcy2 - srcy1;

        if (dx < 0) {
            srcx1 -= dx;
        }
        if (dx + srcx2 >= 256) {
            srcx2 = 256 - dx;
        }

        if (dy < 0) {
            srcy1 -= dy;
        }
        if (dy + srcy2 >= 240) {
            srcy2 = 240 - dy;
        }

        if (!flipHorizontal && !flipVertical) {
            this.fbIndex = (dy << 8) + dx;
            this.tIndex = 0;
            for (this.y = 0; this.y < 8; this.y++) {
                for (this.x = 0; this.x < 8; this.x++) {
                    if (
                            this.x >= srcx1 &&
                                    this.x < srcx2 &&
                                    this.y >= srcy1 &&
                                    this.y < srcy2
                    ) {
                        this.palIndex = this.pixels[this.tIndex];
                        this.tpri = priTable[this.fbIndex];
                        if (this.palIndex != 0 && pri <= (this.tpri & 0xff)) {
                            //console.log("Rendering upright tile to buffer");
                            buffer[this.fbIndex] = palette[this.palIndex + palAdd];
                            this.tpri = (this.tpri & 0xf00) | pri;
                            priTable[this.fbIndex] = this.tpri;
                        }
                    }
                    this.fbIndex++;
                    this.tIndex++;
                }
                this.fbIndex -= 8;
                this.fbIndex += 256;
            }
        } else if (flipHorizontal && !flipVertical) {
            this.fbIndex = (dy << 8) + dx;
            this.tIndex = 7;
            for (this.y = 0; this.y < 8; this.y++) {
                for (this.x = 0; this.x < 8; this.x++) {
                    if (
                            this.x >= srcx1 &&
                                    this.x < srcx2 &&
                                    this.y >= srcy1 &&
                                    this.y < srcy2
                    ) {
                        this.palIndex = this.pixels[this.tIndex];
                        this.tpri = priTable[this.fbIndex];
                        if (this.palIndex != 0 && pri <= (this.tpri & 0xff)) {
                            buffer[this.fbIndex] = palette[this.palIndex + palAdd];
                            this.tpri = (this.tpri & 0xf00) | pri;
                            priTable[this.fbIndex] = this.tpri;
                        }
                    }
                    this.fbIndex++;
                    this.tIndex--;
                }
                this.fbIndex -= 8;
                this.fbIndex += 256;
                this.tIndex += 16;
            }
        } else if (flipVertical && !flipHorizontal) {
            this.fbIndex = (dy << 8) + dx;
            this.tIndex = 56;
            for (this.y = 0; this.y < 8; this.y++) {
                for (this.x = 0; this.x < 8; this.x++) {
                    if (
                            this.x >= srcx1 &&
                                    this.x < srcx2 &&
                                    this.y >= srcy1 &&
                                    this.y < srcy2
                    ) {
                        this.palIndex = this.pixels[this.tIndex];
                        this.tpri = priTable[this.fbIndex];
                        if (this.palIndex != 0 && pri <= (this.tpri & 0xff)) {
                            buffer[this.fbIndex] = palette[this.palIndex + palAdd];
                            this.tpri = (this.tpri & 0xf00) | pri;
                            priTable[this.fbIndex] = this.tpri;
                        }
                    }
                    this.fbIndex++;
                    this.tIndex++;
                }
                this.fbIndex -= 8;
                this.fbIndex += 256;
                this.tIndex -= 16;
            }
        } else {
            this.fbIndex = (dy << 8) + dx;
            this.tIndex = 63;
            for (this.y = 0; this.y < 8; this.y++) {
                for (this.x = 0; this.x < 8; this.x++) {
                    if (
                            this.x >= srcx1 &&
                                    this.x < srcx2 &&
                                    this.y >= srcy1 &&
                                    this.y < srcy2
                    ) {
                        this.palIndex = this.pixels[this.tIndex];
                        this.tpri = priTable[this.fbIndex];
                        if (this.palIndex != 0 && pri <= (this.tpri & 0xff)) {
                            buffer[this.fbIndex] = palette[this.palIndex + palAdd];
                            this.tpri = (this.tpri & 0xf00) | pri;
                            priTable[this.fbIndex] = this.tpri;
                        }
                    }
                    this.fbIndex++;
                    this.tIndex--;
                }
                this.fbIndex -= 8;
                this.fbIndex += 256;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\n");
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                sb.append(pixels[x * 8 + y]);
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
