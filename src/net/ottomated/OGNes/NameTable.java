package net.ottomated.OGNes;

public class NameTable {
    int width;
    int height;
    String name;
    int [] tile;
    int [] attrib;

    public NameTable(int width, int height, String name) {
        this.width = width;
        this.height = height;
        this.name = name;

        this.tile = new int [width * height];
        this.attrib = new int [width * height];
        for (int i = 0; i < width * height; i++) {
            this.tile[i] = 0;
            this.attrib[i] = 0;
        }
    }

    public int getTileIndex(int x, int y) {
        return this.tile[y * this.width + x];
    }

    public int getAttrib(int x, int y) {
        return this.attrib[y * this.width + x];
    }

    public void writeAttrib(int index, int value) {
        int basex = (index % 8) * 4;
        int basey = (int) Math.floor(index / 8) * 4;
        int add;
        int tx, ty;
        int attindex;

        for (int sqy = 0; sqy < 2; sqy++) {
            for (int sqx = 0; sqx < 2; sqx++) {
                add = (value >> (2 * (sqy * 2 + sqx))) & 3;
                for (int y = 0; y < 2; y++) {
                    for (int x = 0; x < 2; x++) {
                        tx = basex + sqx * 2 + x;
                        ty = basey + sqy * 2 + y;
                        attindex = ty * this.width + tx;
                        this.attrib[attindex] = (add << 2) & 12;
                    }
                }
            }
        }
    }
}
