package net.ottomated.OGNes;

public class Sprite {
    int[] memory;
    int index;
    int id;
    boolean isSprite;

    public Sprite(int index, int id, int[] memory) {
        this.memory = memory;
        this.index = index;
        this.id = id;
        this.isSprite = true;

    }

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public int getByte0() {
        return this.memory[this.index * 4 + 0];
    }


    public int getByte1() {
        return this.memory[this.index * 4 + 1];
    }


    public int getByte2() {
        return this.memory[this.index * 4 + 2];
    }


    public int getByte3() {
        return this.memory[this.index * 4 + 3];
    }


    public void setByte0(int value) {
        this.memory[this.index * 4 + 0] = value;
    }


    public void setByte1(int value) {
        this.memory[this.index * 4 + 1] = value;
    }


    public void setByte2(int value) {
        this.memory[this.index * 4 + 2] = value;
    }


    public void setByte3(int value) {
        this.memory[this.index * 4 + 3] = value;
    }


    public void copy(Sprite sprite) {
        setId(sprite.getId());
        setByte0(sprite.getByte0());
        setByte1(sprite.getByte1());
        setByte2(sprite.getByte2());
        setByte3(sprite.getByte3());
    }


    public boolean isEmpty() {
        return getByte0() == 0xFF && getByte1() == 0xFF && getByte2() == 0xFF && getByte3() == 0xFF;
    }


    public boolean isVisible() {
        return getByte0() < 0xEF;
    }


    public int getYPosition() {
        return getByte0() - 1;

    }

    public int getXPosition() {
        return getByte3();
    }


    public int getTileIndex() {
        return getByte1();
    }


    public int getTileIndexForSize16() {
        return ((getByte1() & 1) * 0x1000) + (getByte1() >> 1) * 0x20;
    }


    public int getPallet() {
        return getByte2() & 0x3;
    }


    public int getPriority() {
        return (getByte2() >> 5) & 1;
    }


    public boolean flipHorizontally() {
        return ((getByte2() >> 6) & 1) == 1;
    }


    public boolean flipVertically() {
        return ((getByte2() >> 7) & 1) == 1;
    }


    public boolean on(int y, int length) {
        return (y >= this.getYPosition()) && (y < this.getYPosition() + length);
    }


}

