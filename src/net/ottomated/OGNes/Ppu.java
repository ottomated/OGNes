package net.ottomated.OGNes;

import java.util.ArrayList;

public class Ppu {

    private static final int[] palette = new int[]{
            /* 0x00 */ 0xff757575,
            /* 0x01 */ 0xff8f1b27,
            /* 0x02 */ 0xffab0000,
            /* 0x03 */ 0xff9f0047,
            /* 0x04 */ 0xff77008f,
            /* 0x05 */ 0xff1300ab,
            /* 0x06 */ 0xff0000a7,
            /* 0x07 */ 0xff000b7f,
            /* 0x08 */ 0xff002f43,
            /* 0x09 */ 0xff004700,
            /* 0x0a */ 0xff005100,
            /* 0x0b */ 0xff173f00,
            /* 0x0c */ 0xff5f3f1b,
            /* 0x0d */ 0xff000000,
            /* 0x0e */ 0xff000000,
            /* 0x0f */ 0xff000000,
            /* 0x10 */ 0xffbcbcbc,
            /* 0x11 */ 0xffef7300,
            /* 0x12 */ 0xffef3b23,
            /* 0x13 */ 0xfff30083,
            /* 0x14 */ 0xffbf00bf,
            /* 0x15 */ 0xff5b00e7,
            /* 0x16 */ 0xff002bdb,
            /* 0x17 */ 0xff0f4fcb,
            /* 0x18 */ 0xff00738b,
            /* 0x19 */ 0xff009700,
            /* 0x1a */ 0xff00ab00,
            /* 0x1b */ 0xff3b9300,
            /* 0x1c */ 0xff8b8300,
            /* 0x1d */ 0xff000000,
            /* 0x1e */ 0xff000000,
            /* 0x1f */ 0xff000000,
            /* 0x20 */ 0xffffffff,
            /* 0x21 */ 0xffffbf3f,
            /* 0x22 */ 0xffff975f,
            /* 0x23 */ 0xfffd8ba7,
            /* 0x24 */ 0xffff7bf7,
            /* 0x25 */ 0xffb777ff,
            /* 0x26 */ 0xff6377ff,
            /* 0x27 */ 0xff3b9bff,
            /* 0x28 */ 0xff3fbff3,
            /* 0x29 */ 0xff13d383,
            /* 0x2a */ 0xff4bdf4f,
            /* 0x2b */ 0xff98f858,
            /* 0x2c */ 0xffdbeb00,
            /* 0x2d */ 0xff000000,
            /* 0x2e */ 0xff000000,
            /* 0x2f */ 0xff000000,
            /* 0x30 */ 0xffffffff,
            /* 0x31 */ 0xffffe7ab,
            /* 0x32 */ 0xffffd7c7,
            /* 0x33 */ 0xffffcbd7,
            /* 0x34 */ 0xffffc7ff,
            /* 0x35 */ 0xffdbc7ff,
            /* 0x36 */ 0xffb3bfff,
            /* 0x37 */ 0xffabdbff,
            /* 0x38 */ 0xffa3e7ff,
            /* 0x39 */ 0xffa3ffe3,
            /* 0x3a */ 0xffbff3ab,
            /* 0x3b */ 0xffcfffb3,
            /* 0x3c */ 0xfff3ff9f,
            /* 0x3d */ 0xff000000,
            /* 0x3e */ 0xff000000,
            /* 0x3f */ 0xff000000
    };
    Nes nes;

    Ppu(Nes n) {
        nes = n;
    }

    private int[] vRAM = new int[0x10000];
    private int[] oam = new int[0x100];
    private int[] oam2 = new int[0x20];

    public boolean registerFirstStore;

    public int readBuffer;
    public int fineXScroll;

    public int scanLine = 0;
    public int cycle = 0;
    public int frame = 0;

    public int nameTableLatch = 0;
    public int attributeTableLowLatch = 0;
    public int attributeTableHighLatch = 0;
    public int patternTableLowLatch = 0;
    public int patternTableHighLatch = 0;

    public Register nameTableRegister;
    public Register attributeTableLowRegister;
    public Register attributeTableHighRegister;
    public Register patternTableLowRegister;
    public Register patternTableHighRegister;

    public int ctrl;
    public int mask;
    public int stat;
    public int oamAddr;
    public int oamData;
    public int scrl;
    public int addr;
    public int tempAddr;
    public int data;

    public int oamRead() {
        return oam[oamAddr];
    }

    public void oamWrite() {
        oam[oamAddr] = oamData;
        oamAddr++;
    }

    public int vramRead() {
        return vRAM[addr];
    }

    public void vramWrite() {
        vRAM[addr] = data;
        addr += getVRAMIncrement() ? 32 : 1;
    }

    public void oamDMAWrite(int highByte) {
        for (int i = 0; i < 256; i++) {
            oam[i] = nes.cpu.memory[highByte * 256 + i];
        }
    }

    public ArrayList<Sprite> initSprites(int[] memory) {
        ArrayList<Sprite> res = new ArrayList<Sprite>();
        for (int i = 0, len = memory.length / 4; i < len; i++) {
            res.add(new Sprite(i, i, memory));
        }
        return res;
    }

    public int[] initArray(int len, int initalValue) {
        int[] res = new int[len];
        for (int i = 0; i < len; i++) {
            res[i] = initalValue;
        }
        return res;
    }

    public ArrayList<Sprite> sprites = initSprites(oam);
    public ArrayList<Sprite> sprites2 = initSprites(oam2);

    public int[] spritePixels = initArray(256, -1);
    public int[] spriteIds = initArray(256, -1);
    public int[] spritePriorities = initArray(256, -1);

    public void setNMI(boolean b) {
        ctrl = setStatusAt(b, 7, ctrl);
    }

    public void setMasterSlave(boolean b) {
        ctrl = setStatusAt(b, 6, ctrl);
    }

    public void setSpriteSize(boolean b) {
        ctrl = setStatusAt(b, 5, ctrl);
    }

    public void setBackgroundTile(boolean b) {
        ctrl = setStatusAt(b, 4, ctrl);
    }

    public void setSpritePattern(boolean b) {
        ctrl = setStatusAt(b, 3, ctrl);
    }

    public void setVRAMIncrement(boolean b) {
        ctrl = setStatusAt(b, 2, ctrl);
    }

    public void setNametableBig(boolean b) {
        ctrl = setStatusAt(b, 1, ctrl);
    }

    public void setNametableSmall(boolean b) {
        ctrl = setStatusAt(b, 0, ctrl);
    }

    public boolean getNMI() {
        return getStatusAt(7, ctrl);
    }

    public boolean getMasterSlave() {
        return getStatusAt(6, ctrl);
    }

    public boolean getSpriteSize() {
        return getStatusAt(5, ctrl);
    }

    public boolean getBackgroundTile() {
        return getStatusAt(4, ctrl);
    }

    public boolean getSpritePattern() {
        return getStatusAt(3, ctrl);
    }

    public boolean getVRAMIncrement() {
        return getStatusAt(2, ctrl);
    }

    public boolean getNametableBig() {
        return getStatusAt(1, ctrl);
    }

    public boolean getNametableSmall() {
        return getStatusAt(0, ctrl);
    }


    public void setClrEmphB(boolean b) {
        mask = setStatusAt(b, 7, mask);
    }

    public void setClrEmphG(boolean b) {
        mask = setStatusAt(b, 6, mask);
    }

    public void setClrEmphR(boolean b) {
        mask = setStatusAt(b, 5, mask);
    }

    public void setSpriteEnable(boolean b) {
        mask = setStatusAt(b, 4, mask);
    }

    public void setBackgroundEnable(boolean b) {
        mask = setStatusAt(b, 3, mask);
    }

    public void setSpriteLeftEnable(boolean b) {
        mask = setStatusAt(b, 2, mask);
    }

    public void setBackgroundLeftEnable(boolean b) {
        mask = setStatusAt(b, 1, mask);
    }

    public void setGray(boolean b) {
        mask = setStatusAt(b, 0, mask);
    }

    public boolean getClrEmphB() {
        return getStatusAt(7, mask);
    }

    public boolean getClrEmphG() {
        return getStatusAt(6, mask);
    }

    public boolean getClrEmphR() {
        return getStatusAt(5, mask);
    }

    public boolean getSpriteEnable() {
        return getStatusAt(4, mask);
    }

    public boolean getBackgroundEnable() {
        return getStatusAt(3, mask);
    }

    public boolean getSpriteLeftEnable() {
        return getStatusAt(2, mask);
    }

    public boolean getBackgroundLeftEnable() {
        return getStatusAt(1, mask);
    }

    public boolean getGray(boolean b) {
        return getStatusAt(0, mask);
    }


    public void setVBlank(boolean b) {
        stat = setStatusAt(b, 7, stat);
    }

    public void setSpriteHit(boolean b) {
        stat = setStatusAt(b, 6, stat);
    }

    public void setSpriteOverflow(boolean b) {
        stat = setStatusAt(b, 5, stat);
    }

    public boolean getVBlank() {
        return getStatusAt(7, stat);
    }

    public boolean getSpriteHit() {
        return getStatusAt(6, stat);
    }

    public boolean getSpriteOverflow() {
        return getStatusAt(5, stat);
    }


    private int setStatusAt(boolean b, int i, int register) {
        return b ? register | (1 << i)
                : register & ~(1 << i);
    }

    private boolean getStatusAt(int i, int register) {
        return ((register >> i) & 1) == 1;
    }

    public void reset() {
        vRAM = new int[0x10000];
        oam = new int[0x100];

        ctrl = 0;
        mask = 0;
        scrl = 0;

        int i;

        // RAM
        for (i = 0; i <= 0x2000; i++) {
            vRAM[i] = 0xFF;
        }

        // Clear vRAM
        for (i = 0x2001; i < vRAM.length; i++) {
            vRAM[i] = 0;
        }

    }

    public int peek(int loc) {
        return vRAM[loc];
    }

    public void set(int loc, int b) {
        vRAM[loc] = b;
    }

    public void writeLowerByte(Register register, int value) {
        int lowerByte = register.value & 0xff00;
        int highByte = value & 0xff;
        register.value = lowerByte | highByte;
    }

    public int getNameTableAddressWithMirroring(int address) {
        address = address & 0x2FFF;
        int baseAddress = 0;

        switch (nes.rom.getMirroring()) {

            case HORIZONTAL:
                if (address >= 0x2000 && address < 0x2400)
                    baseAddress = 0x2000;
                else if (address >= 0x2400 && address < 0x2800)
                    baseAddress = 0x2000;
                else if (address >= 0x2800 && address < 0x2C00)
                    baseAddress = 0x2400;
                else
                    baseAddress = 0x2400;

                break;

            case VERTICAL:
                if (address >= 0x2000 && address < 0x2400)
                    baseAddress = 0x2000;
                else if (address >= 0x2400 && address < 0x2800)
                    baseAddress = 0x2400;
                else if (address >= 0x2800 && address < 0x2C00)
                    baseAddress = 0x2000;
                else
                    baseAddress = 0x2400;

                break;

            case FOURSCREEN:
                if (address >= 0x2000 && address < 0x2400)
                    baseAddress = 0x2000;
                else if (address >= 0x2400 && address < 0x2800)
                    baseAddress = 0x2400;
                else if (address >= 0x2800 && address < 0x2C00)
                    baseAddress = 0x2800;
                else
                    baseAddress = 0x2C00;

                break;
        }
        return baseAddress | (address & 0x3FF);
    }

    public int readRegisters(int address) {
        if (address == 0x2002) {
            int value = stat;
            setVBlank(false);
            registerFirstStore = true;
            return stat;
        }

        if (address == 0x2004) {
            return oamData;
        }

        if (address == 0x2007) {
            int value;

            if ((addr & 0x3FFF) >= 0 &&
                    (addr & 0x3FFF) < 0x3F00) {
                value = getVRAMIncrement() ? 1 : 0;
                readBuffer = readMemory(addr);
            } else {
                value = readMemory(addr);
                readBuffer = value;
            }
            return value;
        }
        return 0;
    }

    public void writeRegisters(int address, int value) {
        if (address == 0x2000) {
            ctrl = value;
            tempAddr &= ~0xC00;
            tempAddr |= (value & 0x3) << 10;
        }
        if (address == 0x2001) {
            mask = value;
        }
        if (address == 0x2003) {
            oamAddr = value;
        }
        if (address == 0x2004) {
            oamData = value;
            oam[oamAddr] = value & 0xff;
            oamAddr++;
        }
        if (address == 0x2005) {
            scrl = value;

            if (registerFirstStore) {
                fineXScroll = value & 0x7;
                tempAddr &= ~0x1F;
                tempAddr |= (value >> 3) & 0x1F;
            } else {
                tempAddr &= ~0x73E0;
                tempAddr |= (value & 0xF8) << 2;
                tempAddr |= (value & 0x7) << 12;
            }
            registerFirstStore = !registerFirstStore;
        }
        if (address == 0x2006) {
            if (registerFirstStore) {
                tempAddr &= ~0x7F00;
                tempAddr |= (value & 0x3F) << 8;
            } else {
                addr = value;
                tempAddr &= ~0xFF;
                tempAddr |= (value & 0xFF);
                addr = tempAddr;
            }
            registerFirstStore = !registerFirstStore;
        }
        if (address == 0x2007) {
            data = value;
            writeMemory(addr, value);
            addr += getVRAMIncrement() ? 32 : 1;
            addr &= 0x7FFF;
            addr = addr & 0xFF;
        }
        if (address == 0x4014) {
            oamDMAWrite(value);
        }
    }

    public int readMemory(int address) {
        address = address & 0x3FFF;
        /*
        if(address < 0x2000 && this.rom.header.chr_num !== 0) {
            var mappedAddr = this.mapper.chrMap(address, this.rom.header);
            return this.rom.chr[mappedAddr];
        }
         */

        if (address >= 0x2000 && address < 0x3F00)
            return vRAM[getNameTableAddressWithMirroring(address & 0x2FFF)];
        if (address >= 0x3F00 && address < 0x4000)
            address = address & 0x3F1F;
        if (address == 0x3F10)
            address = 0x3F00;
        if (address == 0x3F14)
            address = 0x3F04;
        if (address == 0x3F18)
            address = 0x3F08;
        if (address == 0x3F1C)
            address = 0x3F0C;
        return vRAM[address];
    }

    public void writeMemory(int address, int value) {
        address = address & 0x3FFF;
        /*
        if(address < 0x2000 && this.rom.header.chr_num !== 0) {
            var mappedAddr = this.mapper.chrMap(address, this.rom.header);
            this.rom.chr[mappedAddr] = value;
            return;
        }
        */

        if (address >= 0x2000 && address < 0x3F00) {
            vRAM[getNameTableAddressWithMirroring(address & 0x2FFF)] = value;
            return;
        }
        if (address >= 0x3F00 && address < 0x4000)
            address = address & 0x3F1F;
        if (address == 0x3F10)
            address = 0x3F00;
        if (address == 0x3F14)
            address = 0x3F04;
        if (address == 0x3F18)
            address = 0x3F08;
        if (address == 0x3F1C)
            address = 0x3F0C;

        vRAM[address] = value;
    }

    public int loadBit(Register register, int index) {
        return (register.value >> index) & 1;
    }

    public int shift(Register register, int value) {
        value = value & 1;  // just in case
        int carry = loadBit(register, register.reg.length - 1);
        register.value = ((register.value << 1) & 0xffff) | value;
        return carry;
    }

    public int getBackgroundPixel() {

        int offset = 15 - fineXScroll;

        int lsb = (loadBit(patternTableHighRegister, offset) << 1) |
                loadBit(patternTableLowRegister, offset);

        int msb = (loadBit(attributeTableHighRegister, offset) << 1) |
                loadBit(attributeTableLowRegister, offset);

        int index = (msb << 2) | lsb;

        //if(this.getPPUSTATUS('g') == 1)
        //    index = index & 0x30;

        int hexIndex = this.readMemory(0x3F00 + index);

        return this.palette[hexIndex];
    }

    public void processSpritePixels() {
        int ay = scanLine - 1;

        for (int i = 0, il = spritePixels.length; i < il; i++) {
            spritePixels[i] = -1;
            spriteIds[i] = -1;
            spritePriorities[i] = -1;
        }

        int height = getSpriteSize() ? 16 : 8;
        int n = 0;

        for (int i = 0, len = sprites2.size(); i < len; i++) {
            Sprite s = sprites2.get(i);

            if (s.isEmpty())
                break;

            int bx = s.getXPosition();
            int by = s.getYPosition();
            int j = ay - by;
            int cy = s.flipHorizontally() ? height - j - 1 : j;
            boolean horizontal = s.flipHorizontally();
            int ptIndex = (height == 8) ? s.getTileIndex() : s.getTileIndexForSize16();
            int msb = s.getPallet();

            for (int k = 0; k < 8; k++) {
                int cx = horizontal ? 7 - k : k;
                int x = bx + k;

                if (x >= 256)
                    break;

                int lsb = getPatternTableElement(ptIndex, cx, cy, height);

                if (lsb != 0) {
                    int pIndex = (msb << 2) | lsb;

                    if (spritePixels[x] == -1) {
                        spritePixels[x] = this.palette[this.readMemory(0x3F10 + pIndex)];
                        spriteIds[x] = s.getId();
                        spritePriorities[x] = s.getPriority();
                    }
                }
            }
        }
    }

    public void evaluateSprites() {
        if (scanLine >= 240)
            return;

        if (cycle == 0) {
            processSpritePixels();

            for (int i = 0; i < 32; i++)
                oam2[i] = 0xFF;

        } else if (cycle == 65) {
            int height = getSpriteSize() ? 16 : 8;
            int n = 0;

            for (int i = 0, len = sprites.size(); i < len; i++) {
                Sprite sprite = sprites.get(i);

                if (sprite.on(scanLine, height) == true) {
                    if (n < 8) {
                        sprites2.get(n++).copy(sprite);
                    } else {
                        setSpriteOverflow(true);
                        break;
                    }
                }
            }
        }
    }

    public void updateShiftRegisters() {
        if (scanLine >= 240 && scanLine <= 260)
            return;

        if ((cycle >= 1 && cycle <= 256) ||
                (cycle >= 329 && cycle <= 336)) {
            shift(patternTableLowRegister, 0);
            shift(patternTableHighRegister, 0);
            shift(attributeTableLowRegister, 0);
            shift(attributeTableHighRegister, 0);
        }
    }

    public void updateFlags() {
        if (cycle == 1) {
            if (scanLine == 241) {
                setVBlank(true);
                nes.graphics.repaint();

            } else if (scanLine == 261) {
                setVBlank(false);
                setSpriteHit(false);
                setSpriteOverflow(false);
            }
        }

        if (cycle == 10) {
            if (scanLine == 241) {
                if (getNMI())
                    nes.cpu.requestIrq(Cpu.Interrupt.NMI);
            }
        }

    }

    public int getPatternTableElement(int index, int x, int y, int ySize) {
        int ax = x % 8;
        int a, b;

        if (ySize == 8) {
            int ay = y % 8;
            int offset = getSpritePattern() ? 0x1000 : 0;
            a = readMemory(offset + index * 0x10 + ay);
            b = readMemory(offset + index * 0x10 + 0x8 + ay);
        } else {
            int ay = y % 8;
            ay += (y >> 3) * 0x10;
            a = readMemory(index + ay);
            b = readMemory(index + ay + 0x8);
        }

        return ((a >> (7 - ax)) & 1) | (((b >> (7 - ax)) & 1) << 1);
    }


    public void fetchNameTable() {
        nameTableLatch = readMemory(0x2000 | (addr & 0x0FFF));
    }

    public void fetchAttributeTable() {
        int v = addr;
        int address = 0x23C0 | (v & 0x0C00) | ((v >> 4) & 0x38) | ((v >> 2) & 0x07);

        int b = this.readMemory(address);

        int coarseX = v & 0x1F;
        int coarseY = (v >> 5) & 0x1F;

        int topbottom = (coarseY % 4) >= 2 ? 1 : 0; // bottom, top
        int rightleft = (coarseX % 4) >= 2 ? 1 : 0; // right, left

        int position = (topbottom << 1) | rightleft; // bottomright, bottomleft,
        // topright, topleft

        int value = (b >> (position << 1)) & 0x3;
        int highBit = value >> 1;
        int lowBit = value & 1;

        attributeTableHighLatch = highBit == 1 ? 0xff : 0;
        attributeTableLowLatch = lowBit == 1 ? 0xff : 0;

    }

    public void fetchPatternTableLow() {
        int fineY = (addr >> 12) & 0x7;
        int index = nameTableRegister.value * 0x10 + fineY;
        if (getBackgroundTile())
            index += 0x1000;
        patternTableLowLatch = readMemory(index);
    }

    public void fetchPatternTableHigh() {
        int fineY = (addr >> 12) & 0x7;
        int index = nameTableRegister.value * 0x10 + fineY;
        if (getBackgroundTile())
            index += 0x1000;
        patternTableHighLatch = readMemory(index);
    }

    public void fetch() {
        if (scanLine >= 240 && scanLine <= 260)
            return;

        if (cycle == 0)
            return;

        if ((cycle >= 257 && cycle <= 320) || cycle >= 337)
            return;

        switch ((cycle - 1) % 8) {
            case 0:
                fetchNameTable();
                break;

            case 2:
                fetchAttributeTable();
                break;

            case 4:
                fetchPatternTableLow();
                break;

            case 6:
                fetchPatternTableHigh();
                break;

            default:
                break;
        }

        if (cycle % 8 == 1) {
            nameTableRegister.value = nameTableLatch;
            writeLowerByte(attributeTableLowRegister, attributeTableLowLatch);
            writeLowerByte(attributeTableHighRegister, attributeTableHighLatch);
            writeLowerByte(patternTableLowRegister, patternTableLowLatch);
            writeLowerByte(patternTableHighRegister, patternTableHighLatch);
        }
    }

    public void updateScrollCounters() {
        if (getClrEmphB() == false && getSpriteEnable() == false)
            return;
        if (scanLine >= 240 && scanLine <= 260)
            return;
        if (scanLine == 261) {
            if (cycle >= 280 && cycle <= 304) {
                addr &= ~0x7BE0;
                addr |= (tempAddr & 0x7BE0);
            }
        }

        if (cycle == 0 || (cycle >= 258 && cycle <= 320))
            return;

        if ((cycle % 8) == 0) {
            int v = addr;

            if ((v & 0x1F) == 31) {
                v &= ~0x1F;
                v ^= 0x400;
            } else {
                v++;
            }

            addr = v;
        }

        if (cycle == 256) {
            int v = addr;

            if ((v & 0x7000) != 0x7000) {
                v += 0x1000;
            } else {
                v &= ~0x7000;
                int y = (v & 0x3E0) >> 5;

                if (y == 29) {
                    y = 0;
                    v ^= 0x800;
                } else if (y == 31) {
                    y = 0;
                } else {
                    y++;
                }

                v = (v & ~0x3E0) | (y << 5);
            }

            addr = v;
        }

        if (cycle == 257) {
            addr &= ~0x41F;
            addr |= (tempAddr & 0x41F);
        }
    }

    public void updateCycle() {
        cycle++;

        if (cycle > 340) {
            cycle = 0;
            scanLine++;

            if (scanLine > 261) {
                scanLine = 0;
                frame++;
            }
        }
    }

    public void render() {
        if (cycle >= 257 || scanLine >= 240 || cycle == 0)
            return;

        int x = cycle - 1;
        int y = scanLine;

        boolean backgroundVisible = getBackgroundEnable();
        boolean spritesVisible = getSpriteEnable();

        int backgroundPixel = getBackgroundPixel();
        int spritePixel = spritePixels[x];
        int spriteId = spriteIds[x];
        int spritePriority = spritePriorities[x];

        int c = this.palette[this.readMemory(0x3F00)];


        // TODO: fix me

        if (backgroundVisible && spritesVisible) {
            if (spritePixel == -1) {
                c = backgroundPixel;
            } else {
                if (backgroundPixel == c)
                    c = spritePixel;
                else
                    c = spritePriority == 0 ? spritePixel : backgroundPixel;
            }
        } else if (backgroundVisible && !spritesVisible) {
            c = backgroundPixel;
        } else if (!backgroundVisible && spritesVisible) {
            if (spritePixel != -1)
                c = spritePixel;
        }

        // TODO: fix me

        if (getClrEmphR())
            c = c | 0x00FF0000;
        if (getClrEmphG())
            c = c | 0x0000FF00;
        if (getClrEmphB())
            c = c | 0x000000FF;

        // TODO: fix me

        if (backgroundVisible && spritesVisible &&
                spriteId == 0 && spritePixel != 0 && backgroundPixel != 0)
            setSpriteHit(true);

        //if (c != 0xff000000 && c != 0xff757575)
        //   dup[c] = c;

        nes.graphics.image.setRGB(x, y, c);
    }
}
