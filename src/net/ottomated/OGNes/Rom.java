package net.ottomated.OGNes;

import net.ottomated.OGNes.mappers.Mapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class Rom {
    public int romCount;
    public int vromCount;
    private boolean mirroring;
    public boolean batteryRam;
    public boolean trainer;
    private boolean fourScreen;
    Mapper mapper;
    public int[][] rom;
    public int[][] vrom;
    public Tile[][] vromTile;


    Rom(File f) throws IOException {
        int i, j;

        byte[] b = Files.readAllBytes(f.toPath());
        assert (b[0] == 0x4E && b[1] == 0x45 && b[2] == 0x53 && b[3] == 0x1A);
        romCount = b[4];
        vromCount = b[5] * 2;
        mirroring = (b[6] & 1) != 0;
        batteryRam = (b[6] & 2) != 0;
        trainer = (b[6] & 4) != 0;
        fourScreen = (b[6] & 8) != 0;
        int mapperId = (b[6] >> 4) | (b[7] & 0xf0);
        //System.out.println(romCount + " " + vromCount + " " + mirroring + " " + batteryRam + " " + trainer + " " + fourScreen);

        boolean foundError = false;
        for (i = 8; i < 16; i++) {
            if (b[i] != 0) {
                foundError = true;
                break;
            }
        }
        if (foundError) {
            mapperId &= 0xf;
        }
        mapper = Mapper.fromID(mapperId);
        System.out.println("Mapper " + mapperId);
        rom = new int[romCount][16384];

        int offset = 16;
        for (i = 0; i < romCount; i++) {
            for (j = 0; j < 16384; j++) {
                if (offset + j >= b.length) {
                    break;
                }
                rom[i][j] = b[offset + j] & 0xff;
            }
            offset += 16384;
        }

        vrom = new int[vromCount][4096];
        for (i = 0; i < vromCount; i++) {
            for (j = 0; j < 4096; j++) {
                if (offset + j >= b.length) {
                    break;
                }
                vrom[i][j] = b[offset + j] & 0xff;
            }
            offset += 4096;
        }

        vromTile = new Tile[vromCount][256];
        for (i = 0; i < this.vromCount; i++) {
            for (j = 0; j < 256; j++) {
                this.vromTile[i][j] = new Tile();
            }
        }

        int tileIndex;
        int leftOver;
        for (int v = 0; v < vromCount; v++) {
            for (i = 0; i < 4096; i++) {
                tileIndex = i >> 4;
                leftOver = i % 16;
                if (leftOver < 8) {
                    vromTile[v][tileIndex].setScanline(leftOver, vrom[v][i], vrom[v][i + 8]);
                } else {
                    vromTile[v][tileIndex].setScanline(leftOver - 8, vrom[v][i - 8], vrom[v][i]);
                }
            }
        }
    }

    Mirroring getMirroring() {
        if (fourScreen)
            return Mirroring.FOURSCREEN;
        if (mirroring)
            return Mirroring.VERTICAL;
        return Mirroring.HORIZONTAL;
    }

    public enum Mirroring {NOTLOADED, FOURSCREEN, HORIZONTAL, VERTICAL, SINGLESCREEN, SINGLESCREEN2}
}
