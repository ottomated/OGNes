package net.ottomated.OGNes;

public class Ppu {
    private static final boolean showSpr0Hit = false;
    private static final boolean clipToTvSize = true;

    private Nes nes;

    static final int STATUS_VRAMWRITE = 4;
    static final int STATUS_SLSPRITECOUNT = 5;
    static final int STATUS_SPRITE0HIT = 6;
    static final int STATUS_VBLANK = 7;

    Ppu(Nes n) {
        nes = n;
        reset();
    }

    public int[] vramMem = null;
    int[] spriteMem = null;
    private int vramAddress;
    private int vramTmpAddress;
    private int vramBufferedReadValue;
    private boolean firstWrite;
    private int sramAddress;
    private Rom.Mirroring currentMirroring;
    boolean requestEndFrame;
    private boolean dummyCycleToggle;
    private boolean validTileData;
    int nmiCounter;
    private boolean scanlineAlreadyRendered;
    private int f_nmiOnVblank;
    private int f_spriteSize;
    private int f_bgPatternTable;
    private int f_spPatternTable;
    private int f_addrInc;
    private int f_nTblAddress;
    private int f_color;
    int f_spVisibility;
    private int f_bgVisibility;
    private int f_spClipping;
    private int f_bgClipping;
    private int f_dispType;
    private int cntFV;
    private int cntV;
    private int cntH;
    private int cntVT;
    private int cntHT;
    private int regFV;
    private int regV;
    private int regH;
    private int regVT;
    private int regHT;
    private int regFH;
    private int regS;
    private int[] attrib;
    private int[] buffer = null;
    private int[] bgbuffer = null;
    private int[] pixrendered;

    private Tile[] scantile;
    int scanline;
    private int lastRenderedScanline;
    int curX;
    private int[] sprX;
    private int[] sprY;
    private int[] sprTile;
    private int[] sprCol;
    private boolean[] vertFlip;
    private boolean[] horiFlip;
    private boolean[] bgPriority;
    int spr0HitX;
    int spr0HitY;
    private boolean hitSpr0;
    private int[] sprPalette = null;
    private int[] imgPalette = null;
    public Tile[] ptTile;
    private int[] ntable1 = null;
    private NameTable[] nameTable = null;
    private int[] vramMirrorTable = null;
    PaletteTable palTable = null;

    public void reset() {
        int i;
        ptTile = new Tile[512];
        nameTable = new NameTable[4];
        scantile = new Tile[32];

        // Memory
        vramMem = new int[0x8000];
        spriteMem = new int[0x100];
        for (i = 0; i < vramMem.length; i++) {
            vramMem[i] = 0;
        }
        for (i = 0; i < spriteMem.length; i++) {
            spriteMem[i] = 0;
        }

        // VRAM I/O:
        //vramAddress = null;
        //vramTmpAddress = null;
        vramBufferedReadValue = 0;
        firstWrite = true; // VRAM/Scroll Hi/Lo latch

        // SPR-RAM I/O:
        sramAddress = 0; // 8-bit only.

        requestEndFrame = false;
        dummyCycleToggle = false;
        validTileData = false;
        nmiCounter = 0;


        // Control Flags Register 1:
        f_nmiOnVblank = 0; // NMI on VBlank. 0=disable, 1=enable
        f_spriteSize = 0; // Sprite size. 0=8x8, 1=8x16
        f_bgPatternTable = 0; // Background Pattern Table address. 0=0x0000,1=0x1000
        f_spPatternTable = 0; // Sprite Pattern Table address. 0=0x0000,1=0x1000
        f_addrInc = 0; // PPU Address Increment. 0=1,1=32
        f_nTblAddress = 0; // Name Table Address. 0=0x2000,1=0x2400,2=0x2800,3=0x2C00

        // Control Flags Register 2:
        f_color = 0; // Background color. 0=black, 1=blue, 2=green, 4=red
        f_spVisibility = 0; // Sprite visibility. 0=not displayed,1=displayed
        f_bgVisibility = 0; // Background visibility. 0=Not Displayed,1=displayed
        f_spClipping = 0; // Sprite clipping. 0=Sprites invisible in left 8-pixel column,1=No clipping
        f_bgClipping = 0; // Background clipping. 0=BG invisible in left 8-pixel column, 1=No clipping
        f_dispType = 0; // Display type. 0=color, 1=monochrome

        // Counters:
        cntFV = 0;
        cntV = 0;
        cntH = 0;
        cntVT = 0;
        cntHT = 0;

        // Registers:
        regFV = 0;
        regV = 0;
        regH = 0;
        regVT = 0;
        regHT = 0;
        regFH = 0;
        regS = 0;

        // These are temporary variables used in rendering and sound procedures.
        // Their states outside of those procedures can be ignored.
        // TODO: the use of this is a bit weird, investigate
        //curNt = null;

        // Variables used when rendering:
        attrib = new int[32];
        buffer = new int[256 * 240];
        bgbuffer = new int[256 * 240];
        pixrendered = new int[256 * 240];

        //validTileData = null;

        //scantile = new int[32];

        // Initialize misc vars:
        scanline = 0;
        lastRenderedScanline = -1;
        curX = 0;

        // Sprite data:
        sprX = new int[64]; // X coordinate
        sprY = new int[64]; // Y coordinate
        sprTile = new int[64]; // Tile Index (into pattern table)
        sprCol = new int[64]; // Upper two bits of color
        vertFlip = new boolean[64]; // Vertical Flip
        horiFlip = new boolean[64]; // Horizontal Flip
        bgPriority = new boolean[64]; // Background priority
        spr0HitX = 0; // Sprite #0 hit X coordinate
        spr0HitY = 0; // Sprite #0 hit Y coordinate
        hitSpr0 = false;

        // Palette data:
        sprPalette = new int[16];
        imgPalette = new int[16];

        // Create pattern table tile buffers:
        //ptTile = new Tile[512];

        for (i = 0; i < 512; i++) {
            ptTile[i] = new Tile();
        }

        // Create nametable buffers:
        // Name table data:
        ntable1 = new int[4];
        //currentMirroring = -1;
        //nameTable = new int[4];
        for (i = 0; i < 4; i++) {
            nameTable[i] = new NameTable(32, 32, "Nt" + i);
        }

        // Initialize mirroring lookup table:
        vramMirrorTable = new int[0x8000];
        for (i = 0; i < 0x8000; i++) {
            vramMirrorTable[i] = i;
        }

        palTable = new PaletteTable();
        palTable.loadDefaultPalette();

        updateControlReg1(0);
        updateControlReg2(0);
    }


    void setMirroring(Rom.Mirroring mirroring) {
        if (mirroring == currentMirroring) {
            return;
        }

        currentMirroring = mirroring;
        triggerRendering();

        // Remove mirroring:
        if (vramMirrorTable == null) {
            vramMirrorTable = new int[0x8000];
        }
        for (int i = 0; i < 0x8000; i++) {
            vramMirrorTable[i] = i;
        }

        // Palette mirroring:
        defineMirrorRegion(0x3f20, 0x3f00, 0x20);
        defineMirrorRegion(0x3f40, 0x3f00, 0x20);
        defineMirrorRegion(0x3f80, 0x3f00, 0x20);
        defineMirrorRegion(0x3fc0, 0x3f00, 0x20);

        // Additional mirroring:
        defineMirrorRegion(0x3000, 0x2000, 0xf00);
        defineMirrorRegion(0x4000, 0x0000, 0x4000);

        if (mirroring == Rom.Mirroring.HORIZONTAL) {
            // Horizontal mirroring.

            ntable1[0] = 0;
            ntable1[1] = 0;
            ntable1[2] = 1;
            ntable1[3] = 1;

            defineMirrorRegion(0x2400, 0x2000, 0x400);
            defineMirrorRegion(0x2c00, 0x2800, 0x400);
        } else if (mirroring == Rom.Mirroring.VERTICAL) {
            // Vertical mirroring.

            ntable1[0] = 0;
            ntable1[1] = 1;
            ntable1[2] = 0;
            ntable1[3] = 1;

            defineMirrorRegion(0x2800, 0x2000, 0x400);
            defineMirrorRegion(0x2c00, 0x2400, 0x400);
        } else if (mirroring == Rom.Mirroring.SINGLESCREEN) {
            // Single Screen mirroring

            ntable1[0] = 0;
            ntable1[1] = 0;
            ntable1[2] = 0;
            ntable1[3] = 0;

            defineMirrorRegion(0x2400, 0x2000, 0x400);
            defineMirrorRegion(0x2800, 0x2000, 0x400);
            defineMirrorRegion(0x2c00, 0x2000, 0x400);
        } else if (mirroring == Rom.Mirroring.SINGLESCREEN2) {
            ntable1[0] = 1;
            ntable1[1] = 1;
            ntable1[2] = 1;
            ntable1[3] = 1;

            defineMirrorRegion(0x2400, 0x2400, 0x400);
            defineMirrorRegion(0x2800, 0x2400, 0x400);
            defineMirrorRegion(0x2c00, 0x2400, 0x400);
        } else {
            // Assume Four-screen mirroring.

            ntable1[0] = 0;
            ntable1[1] = 1;
            ntable1[2] = 2;
            ntable1[3] = 3;
        }
    }

    // Define a mirrored area in the address lookup table.
    // Assumes the regions don't overlap.
    // The 'to' region is the region that is physically in memory.
    private void defineMirrorRegion(int fromStart, int toStart, int size) {
        for (int i = 0; i < size; i++) {
            vramMirrorTable[fromStart + i] = toStart + i;
        }
    }

    void startVBlank() {
        // Do NMI:
        nes.cpu.requestIrq(Cpu.Interrupt.NMI);

        // Make sure everything is rendered:
        if (lastRenderedScanline < 239) {
            renderFramePartially(
                    lastRenderedScanline + 1,
                    240 - lastRenderedScanline
            );
        }

        // End frame:
        endFrame();

        // Reset scanline counter:
        lastRenderedScanline = -1;
    }

    void endScanline() {
        switch (scanline) {
            case 19:
                // Dummy scanline.
                // May be variable length:
                if (dummyCycleToggle) {
                    // Remove dead cycle at end of scanline,
                    // for next scanline:
                    curX = 1;
                    dummyCycleToggle = false;
                }
                break;

            case 20:
                // Clear VBlank flag:
                setStatusFlag(Ppu.STATUS_VBLANK, false);

                // Clear Sprite #0 hit flag:
                setStatusFlag(STATUS_SPRITE0HIT, false);
                hitSpr0 = false;
                spr0HitX = -1;
                spr0HitY = -1;

                if (f_bgVisibility == 1 || f_spVisibility == 1) {
                    // Update counters:
                    cntFV = regFV;
                    cntV = regV;
                    cntH = regH;
                    cntVT = regVT;
                    cntHT = regHT;

                    if (f_bgVisibility == 1) {
                        // Render dummy scanline:
                        renderBgScanline(false, 0);
                    }
                }

                if (f_bgVisibility == 1 && f_spVisibility == 1) {
                    // Check sprite 0 hit for first scanline:
                    checkSprite0(0);
                }

                if (f_bgVisibility == 1 || f_spVisibility == 1) {
                    // Clock mapper IRQ Counter:
                    nes.mapper.clockIrqCounter();
                }
                break;

            case 261:
                // Dead scanline, no rendering.
                // Set VINT:
                setStatusFlag(STATUS_VBLANK, true);
                requestEndFrame = true;
                nmiCounter = 9;

                // Wrap around:
                scanline = -1; // will be incremented to 0

                break;

            default:
                if (scanline >= 21 && scanline <= 260) {
                    // Render normally:
                    if (f_bgVisibility == 1) {
                        if (!scanlineAlreadyRendered) {
                            // update scroll:
                            cntHT = regHT;
                            cntH = regH;
                            renderBgScanline(true, scanline + 1 - 21);
                        }
                        scanlineAlreadyRendered = false;

                        // Check for sprite 0 (next scanline):
                        if (!hitSpr0 && f_spVisibility == 1) {
                            if (
                                    sprX[0] >= -7 &&
                                            sprX[0] < 256 &&
                                            sprY[0] + 1 <= scanline - 20 &&
                                            sprY[0] + 1 + (f_spriteSize == 0 ? 8 : 16) >=
                                                    scanline - 20
                            ) {
                                if (checkSprite0(scanline - 20)) {
                                    hitSpr0 = true;
                                }
                            }
                        }
                    }

                    if (f_bgVisibility == 1 || f_spVisibility == 1) {
                        // Clock mapper IRQ Counter:
                        nes.mapper.clockIrqCounter();
                    }
                }
        }

        scanline++;
        regsToAddress();
        cntsToAddress();
    }

    void startFrame() {
        // Set background color:
        int bgColor;

        if (f_dispType == 0) {
            // Color display.
            // f_color determines color emphasis.
            // Use first entry of image palette as BG color.
            bgColor = imgPalette[0];
        } else {
            // Monochrome display.
            // f_color determines the bg color.
            switch (f_color) {
                case 0:
                    // Black
                    bgColor = 0x00000;
                    break;
                case 1:
                    // Green
                    bgColor = 0x00ff00;
                    break;
                case 2:
                    // Blue
                    bgColor = 0xff0000;
                    break;
                case 3:
                    // Invalid. Use black.
                    bgColor = 0x000000;
                    break;
                case 4:
                    // Red
                    bgColor = 0x0000ff;
                    break;
                default:
                    // Invalid. Use black.
                    bgColor = 0x0;
            }
        }

        int i;
        for (i = 0; i < 256 * 240; i++) {
            buffer[i] = bgColor;
        }
        for (i = 0; i < pixrendered.length; i++) {
            pixrendered[i] = 65;
        }
    }

    private void endFrame() {
        int i, x, y;

        // Draw spr#0 hit coordinates:
        if (showSpr0Hit) {
            // Spr 0 position:
            if (
                    sprX[0] >= 0 &&
                            sprX[0] < 256 &&
                            sprY[0] >= 0 &&
                            sprY[0] < 240
            ) {
                for (i = 0; i < 256; i++) {
                    buffer[(sprY[0] << 8) + i] = 0xff5555;
                }
                for (i = 0; i < 240; i++) {
                    buffer[(i << 8) + sprX[0]] = 0xff5555;
                }
            }
            // Hit position:
            if (
                    spr0HitX >= 0 &&
                            spr0HitX < 256 &&
                            spr0HitY >= 0 &&
                            spr0HitY < 240
            ) {
                for (i = 0; i < 256; i++) {
                    buffer[(spr0HitY << 8) + i] = 0x55ff55;
                }
                for (i = 0; i < 240; i++) {
                    buffer[(i << 8) + spr0HitX] = 0x55ff55;
                }
            }
        }

        // This is a bit lazy..
        // if either the sprites or the background should be clipped,
        // both are clipped after rendering is finished.
        if (
                clipToTvSize ||
                        f_bgClipping == 0 ||
                        f_spClipping == 0
        ) {
            // Clip left 8-pixels column:
            for (y = 0; y < 240; y++) {
                for (x = 0; x < 8; x++) {
                    buffer[(y << 8) + x] = 0;
                }
            }
        }

        if (clipToTvSize) {
            // Clip right 8-pixels column too:
            for (y = 0; y < 240; y++) {
                for (x = 0; x < 8; x++) {
                    buffer[(y << 8) + 255 - x] = 0;
                }
            }
        }

        // Clip top and bottom 8 pixels:
        if (clipToTvSize) {
            for (y = 0; y < 8; y++) {
                for (x = 0; x < 256; x++) {
                    buffer[(y << 8) + x] = 0;
                    buffer[((239 - y) << 8) + x] = 0;
                }
            }
        }

        nes.graphics.writeFrame(buffer);

    }

    public void updateControlReg1(int value) {
        triggerRendering();

        f_nmiOnVblank = (value >> 7) & 1;
        f_spriteSize = (value >> 5) & 1;
        f_bgPatternTable = (value >> 4) & 1;
        f_spPatternTable = (value >> 3) & 1;
        f_addrInc = (value >> 2) & 1;
        f_nTblAddress = value & 3;

        regV = (value >> 1) & 1;
        regH = value & 1;
        regS = (value >> 4) & 1;
    }

    public void updateControlReg2(int value) {
        triggerRendering();

        f_color = (value >> 5) & 7;
        f_spVisibility = (value >> 4) & 1;
        f_bgVisibility = (value >> 3) & 1;
        f_spClipping = (value >> 2) & 1;
        f_bgClipping = (value >> 1) & 1;
        f_dispType = value & 1;

        if (f_dispType == 0) {
            palTable.setEmphasis(f_color);
        }
        updatePalettes();
    }

    void setStatusFlag(int flag, boolean value) {
        int n = 1 << flag;
        nes.cpu.memory[0x2002] =
                (nes.cpu.memory[0x2002] & (255 - n)) | (value ? n : 0);
    }

    // CPU Register $2002:
    // Read the Status Register.
    public int readStatusRegister() {
        int tmp = nes.cpu.memory[0x2002];

        // Reset scroll & VRAM Address toggle:
        firstWrite = true;

        // Clear VBlank flag:
        setStatusFlag(Ppu.STATUS_VBLANK, false);

        // Fetch status data:
        return tmp;
    }

    // CPU Register $2003:
    // Write the SPR-RAM address that is used for sramWrite (Register 0x2004 in CPU memory map)
    public void writeSRAMAddress(int address) {
        sramAddress = address;
    }

    // CPU Register $2004 (R):
    // Read from SPR-RAM (Sprite RAM).
    // The address should be set first.
    public int sramLoad() {
    /*short tmp = sprMem.load(sramAddress);
        sramAddress++; // Increment address
        sramAddress%=0x100;
        return tmp;*/
        return spriteMem[sramAddress];
    }

    // CPU Register $2004 (W):
    // Write to SPR-RAM (Sprite RAM).
    // The address should be set first.
    public void sramWrite(int value) {
        spriteMem[sramAddress] = value;
        spriteRamWriteUpdate(sramAddress, value);
        sramAddress++; // Increment address
        sramAddress %= 0x100;
    }

    // CPU Register $2005:
    // Write to scroll registers.
    // The first write is the vertical offset, the second is the
    // horizontal offset:
    public void scrollWrite(int value) {
        triggerRendering();

        if (firstWrite) {
            // First write, horizontal scroll:
            regHT = (value >> 3) & 31;
            regFH = value & 7;
        } else {
            // Second write, vertical scroll:
            regFV = value & 7;
            regVT = (value >> 3) & 31;
        }
        firstWrite = !firstWrite;
    }

    // CPU Register $2006:
    // Sets the adress used when reading/writing from/to VRAM.
    // The first write sets the high byte, the second the low byte.
    public void writeVRAMAddress(int address) {
        if (firstWrite) {
            regFV = (address >> 4) & 3;
            regV = (address >> 3) & 1;
            regH = (address >> 2) & 1;
            regVT = (regVT & 7) | ((address & 3) << 3);
        } else {
            triggerRendering();

            regVT = (regVT & 24) | ((address >> 5) & 7);
            regHT = address & 31;

            cntFV = regFV;
            cntV = regV;
            cntH = regH;
            cntVT = regVT;
            cntHT = regHT;

            checkSprite0(scanline - 20);
        }

        firstWrite = !firstWrite;

        // Invoke mapper latch:
        cntsToAddress();
        if (vramAddress < 0x2000) {
            nes.mapper.latchAccess(vramAddress);
        }
    }

    // CPU Register $2007(R):
    // Read from PPU memory. The address should be set first.
    public int vramLoad() {
        int tmp;

        cntsToAddress();
        regsToAddress();

        // If address is in range 0x0000-0x3EFF, return buffered values:
        if (vramAddress <= 0x3eff) {
            tmp = vramBufferedReadValue;

            // Update buffered value:
            if (vramAddress < 0x2000) {
                vramBufferedReadValue = vramMem[vramAddress];
            } else {
                vramBufferedReadValue = mirroredLoad(vramAddress);
            }

            // Mapper latch access:
            if (vramAddress < 0x2000) {
                nes.mapper.latchAccess(vramAddress);
            }

            // Increment by either 1 or 32, depending on d2 of Control Register 1:
            vramAddress += f_addrInc == 1 ? 32 : 1;

            cntsFromAddress();
            regsFromAddress();

            return tmp; // Return the previous buffered value.
        }

        // No buffering in this mem range. Read normally.
        tmp = mirroredLoad(vramAddress);

        // Increment by either 1 or 32, depending on d2 of Control Register 1:
        vramAddress += f_addrInc == 1 ? 32 : 1;

        cntsFromAddress();
        regsFromAddress();

        return tmp;
    }

    // CPU Register $2007(W):
    // Write to PPU memory. The address should be set first.
    public void vramWrite(int value) {
        triggerRendering();
        cntsToAddress();
        regsToAddress();

        if (vramAddress >= 0x2000) {
            // Mirroring is used.
            mirroredWrite(vramAddress, value);
        } else {
            // Write normally.
            writeMem(vramAddress, value);

            // Invoke mapper latch:
            nes.mapper.latchAccess(vramAddress);
        }

        // Increment by either 1 or 32, depending on d2 of Control Register 1:
        vramAddress += f_addrInc == 1 ? 32 : 1;
        regsFromAddress();
        cntsFromAddress();
    }

    // CPU Register $4014:
    // Write 256 bytes of main memory
    // into Sprite RAM.
    public void sramDMA(int value) {
        int baseAddress = value * 0x100;
        int data;
        for (int i = sramAddress; i < 256; i++) {
            data = nes.cpu.memory[baseAddress + i];
            spriteMem[i] = data;
            spriteRamWriteUpdate(i, data);
        }

        //nes.cpu.haltCycles(513);
    }

    private void regsFromAddress() {
        int address = (vramTmpAddress >> 8) & 0xff;
        regFV = (address >> 4) & 7;
        regV = (address >> 3) & 1;
        regH = (address >> 2) & 1;
        regVT = (regVT & 7) | ((address & 3) << 3);

        address = vramTmpAddress & 0xff;
        regVT = (regVT & 24) | ((address >> 5) & 7);
        regHT = address & 31;
    }

    // Updates the scroll registers from a new VRAM address.
    private void cntsFromAddress() {
        int address = (vramAddress >> 8) & 0xff;
        cntFV = (address >> 4) & 3;
        cntV = (address >> 3) & 1;
        cntH = (address >> 2) & 1;
        cntVT = (cntVT & 7) | ((address & 3) << 3);

        address = vramAddress & 0xff;
        cntVT = (cntVT & 24) | ((address >> 5) & 7);
        cntHT = address & 31;
    }

    private void regsToAddress() {
        int b1 = (regFV & 7) << 4;
        b1 |= (regV & 1) << 3;
        b1 |= (regH & 1) << 2;
        b1 |= (regVT >> 3) & 3;

        int b2 = (regVT & 7) << 5;
        b2 |= regHT & 31;

        vramTmpAddress = ((b1 << 8) | b2) & 0x7fff;
    }

    private void cntsToAddress() {
        int b1 = (cntFV & 7) << 4;
        b1 |= (cntV & 1) << 3;
        b1 |= (cntH & 1) << 2;
        b1 |= (cntVT >> 3) & 3;

        int b2 = (cntVT & 7) << 5;
        b2 |= cntHT & 31;

        vramAddress = ((b1 << 8) | b2) & 0x7fff;
    }

    // Reads from memory, taking into account
    // mirroring/mapping of address ranges.
    private int mirroredLoad(int address) {
        return vramMem[vramMirrorTable[address]];
    }

    // Writes to memory, taking into account
    // mirroring/mapping of address ranges.
    private void mirroredWrite(int address, int value) {
        if (address >= 0x3f00 && address < 0x3f20) {
            // Palette write mirroring.
            if (address == 0x3f00 || address == 0x3f10) {
                writeMem(0x3f00, value);
                writeMem(0x3f10, value);
            } else if (address == 0x3f04 || address == 0x3f14) {
                writeMem(0x3f04, value);
                writeMem(0x3f14, value);
            } else if (address == 0x3f08 || address == 0x3f18) {
                writeMem(0x3f08, value);
                writeMem(0x3f18, value);
            } else if (address == 0x3f0c || address == 0x3f1c) {
                writeMem(0x3f0c, value);
                writeMem(0x3f1c, value);
            } else {
                writeMem(address, value);
            }
        } else {
            // Use lookup table for mirrored address:
            if (address < vramMirrorTable.length) {
                writeMem(vramMirrorTable[address], value);
            } else {
                throw new Error("Invalid VRAM address: " + Integer.toHexString(address));
            }
        }
    }

    public void triggerRendering() {
        if (scanline >= 21 && scanline <= 260) {
            // Render sprites, and combine:
            renderFramePartially(
                    lastRenderedScanline + 1,
                    scanline - 21 - lastRenderedScanline
            );

            // Set last rendered scanline:
            lastRenderedScanline = scanline - 21;
        }
    }

    private void renderFramePartially(int startScan, int scanCount) {
        if (f_spVisibility == 1) {
            renderSpritesPartially(startScan, scanCount, true);
        }

        if (f_bgVisibility == 1) {
            int si = startScan << 8;
            int ei = (startScan + scanCount) << 8;
            if (ei > 0xf000) {
                ei = 0xf000;
            }
            for (int destIndex = si; destIndex < ei; destIndex++) {
                if (pixrendered[destIndex] > 0xff) {
                    buffer[destIndex] = bgbuffer[destIndex];
                }
            }
        }

        if (f_spVisibility == 1) {
            renderSpritesPartially(startScan, scanCount, false);
        }

        validTileData = false;
    }

    private void renderBgScanline(boolean bgbuffer, int scan) {
        int baseTile = regS == 0 ? 0 : 256;
        int destIndex = (scan << 8) - regFH;

        int curNt;

        cntHT = regHT;
        cntH = regH;
        curNt = ntable1[cntV + cntV + cntH];

        if (scan < 240 && scan - cntFV >= 0) {
            int tscanoffset = cntFV << 3;
            int[] targetBuffer = bgbuffer ? this.bgbuffer : buffer;

            Tile t;
            int[] tpix;
            int att, col;

            for (int tile = 0; tile < 32; tile++) {
                if (scan >= 0) {
                    // Fetch tile & attrib data:
                    if (validTileData) {
                        // Get data from array:
                        t = scantile[tile];
                        if (t == null) {
                            continue;
                        }
                        tpix = t.pixels;
                        att = attrib[tile];
                    } else {
                        // Fetch data:
                        t = ptTile[baseTile + nameTable[curNt].getTileIndex(cntHT, cntVT)];
                        if (t == null) {
                            continue;
                        }
                        tpix = t.pixels;
                        att = nameTable[curNt].getAttrib(cntHT, cntVT);
                        scantile[tile] = t;
                        attrib[tile] = att;
                    }

                    // Render tile scanline:
                    int sx = 0;
                    int x = (tile << 3) - regFH;

                    if (x > -8) {
                        if (x < 0) {
                            destIndex -= x;
                            sx = -x;
                        }
                        if (t.opaque[cntFV]) {
                            for (; sx < 8; sx++) {
                                targetBuffer[destIndex] =
                                        imgPalette[tpix[tscanoffset + sx] + att];
                                pixrendered[destIndex] |= 256;
                                destIndex++;
                            }
                        } else {
                            for (; sx < 8; sx++) {
                                col = tpix[tscanoffset + sx];
                                if (col != 0) {
                                    targetBuffer[destIndex] = imgPalette[col + att];
                                    pixrendered[destIndex] |= 256;
                                }
                                destIndex++;
                            }
                        }
                    }
                }

                // Increase Horizontal Tile Counter:
                if (++cntHT == 32) {
                    cntHT = 0;
                    cntH++;
                    cntH %= 2;
                    curNt = ntable1[(cntV << 1) + cntH];
                }
            }

            // Tile data for one row should now have been fetched,
            // so the data in the array is valid.
            validTileData = true;
        }

        // update vertical scroll:
        cntFV++;
        if (cntFV == 8) {
            cntFV = 0;
            cntVT++;
            if (cntVT == 30) {
                cntVT = 0;
                cntV++;
                cntV %= 2;
            } else if (cntVT == 32) {
                cntVT = 0;
            }

            // Invalidate fetched data:
            validTileData = false;
        }
    }

    private void renderSpritesPartially(int startscan, int scancount, boolean bgPri) {
        if (f_spVisibility == 1) {
            for (int i = 0; i < 64; i++) {
                if (
                        bgPriority[i] == bgPri &&
                                sprX[i] >= 0 &&
                                sprX[i] < 256 &&
                                sprY[i] + 8 >= startscan &&
                                sprY[i] < startscan + scancount
                ) {
                    // Show sprite.
                    if (f_spriteSize == 0) {
                        // 8x8 sprites

                        int srcy1 = 0;
                        int srcy2 = 8;

                        if (sprY[i] < startscan) {
                            srcy1 = startscan - sprY[i] - 1;
                        }

                        if (sprY[i] + 8 > startscan + scancount) {
                            srcy2 = startscan + scancount - sprY[i] + 1;
                        }

                        if (f_spPatternTable == 0) {
                            ptTile[sprTile[i]].render(
                                    buffer,
                                    0,
                                    srcy1,
                                    8,
                                    srcy2,
                                    sprX[i],
                                    sprY[i] + 1,
                                    sprCol[i],
                                    sprPalette,
                                    horiFlip[i],
                                    vertFlip[i],
                                    i,
                                    pixrendered
                            );
                        } else {
                            ptTile[sprTile[i] + 256].render(
                                    buffer,
                                    0,
                                    srcy1,
                                    8,
                                    srcy2,
                                    sprX[i],
                                    sprY[i] + 1,
                                    sprCol[i],
                                    sprPalette,
                                    horiFlip[i],
                                    vertFlip[i],
                                    i,
                                    pixrendered
                            );
                        }
                    } else {
                        // 8x16 sprites
                        int top = sprTile[i];
                        if ((top & 1) != 0) {
                            top = sprTile[i] - 1 + 256;
                        }

                        int srcy1 = 0;
                        int srcy2 = 8;

                        if (sprY[i] < startscan) {
                            srcy1 = startscan - sprY[i] - 1;
                        }

                        if (sprY[i] + 8 > startscan + scancount) {
                            srcy2 = startscan + scancount - sprY[i];
                        }

                        ptTile[top + (vertFlip[i] ? 1 : 0)].render(
                                buffer,
                                0,
                                srcy1,
                                8,
                                srcy2,
                                sprX[i],
                                sprY[i] + 1,
                                sprCol[i],
                                sprPalette,
                                horiFlip[i],
                                vertFlip[i],
                                i,
                                pixrendered
                        );

                        srcy1 = 0;
                        srcy2 = 8;

                        if (sprY[i] + 8 < startscan) {
                            srcy1 = startscan - (sprY[i] + 8 + 1);
                        }

                        if (sprY[i] + 16 > startscan + scancount) {
                            srcy2 = startscan + scancount - (sprY[i] + 8);
                        }

                        ptTile[top + (vertFlip[i] ? 0 : 1)].render(
                                buffer,
                                0,
                                srcy1,
                                8,
                                srcy2,
                                sprX[i],
                                sprY[i] + 1 + 8,
                                sprCol[i],
                                sprPalette,
                                horiFlip[i],
                                vertFlip[i],
                                i,
                                pixrendered
                        );
                    }
                }
            }
        }
    }

    private boolean checkSprite0(int scan) {
        spr0HitX = -1;
        spr0HitY = -1;

        int toffset;
        int tIndexAdd = f_spPatternTable == 0 ? 0 : 256;
        int x, y, i;
        Tile t;
        int bufferIndex;

        x = sprX[0];
        y = sprY[0] + 1;

        if (f_spriteSize == 0) {
            // 8x8 sprites.

            // Check range:
            if (y <= scan && y + 8 > scan && x >= -7 && x < 256) {
                // Sprite is in range.
                // Draw scanline:
                t = ptTile[sprTile[0] + tIndexAdd];

                if (vertFlip[0]) {
                    toffset = 7 - (scan - y);
                } else {
                    toffset = scan - y;
                }
                toffset *= 8;

                bufferIndex = scan * 256 + x;
                if (horiFlip[0]) {
                    for (i = 7; i >= 0; i--) {
                        if (x >= 0 && x < 256) {
                            if (
                                    bufferIndex >= 0 &&
                                            bufferIndex < 61440 &&
                                            pixrendered[bufferIndex] != 0
                            ) {
                                if (t.pixels[toffset + i] != 0) {
                                    spr0HitX = bufferIndex % 256;
                                    spr0HitY = scan;
                                    return true;
                                }
                            }
                        }
                        x++;
                        bufferIndex++;
                    }
                } else {
                    for (i = 0; i < 8; i++) {
                        if (x >= 0 && x < 256) {
                            if (
                                    bufferIndex >= 0 &&
                                            bufferIndex < 61440 &&
                                            pixrendered[bufferIndex] != 0
                            ) {
                                if (t.pixels[toffset + i] != 0) {
                                    spr0HitX = bufferIndex % 256;
                                    spr0HitY = scan;
                                    return true;
                                }
                            }
                        }
                        x++;
                        bufferIndex++;
                    }
                }
            }
        } else {
            // 8x16 sprites:

            // Check range:
            if (y <= scan && y + 16 > scan && x >= -7 && x < 256) {
                // Sprite is in range.
                // Draw scanline:

                if (vertFlip[0]) {
                    toffset = 15 - (scan - y);
                } else {
                    toffset = scan - y;
                }

                if (toffset < 8) {
                    // first half of sprite.
                    t = ptTile[
                            sprTile[0] +
                                    (vertFlip[0] ? 1 : 0) +
                                    ((sprTile[0] & 1) != 0 ? 255 : 0)
                            ];
                } else {
                    // second half of sprite.
                    t = ptTile[
                            sprTile[0] +
                                    (vertFlip[0] ? 0 : 1) +
                                    ((sprTile[0] & 1) != 0 ? 255 : 0)
                            ];
                    if (vertFlip[0]) {
                        toffset = 15 - toffset;
                    } else {
                        toffset -= 8;
                    }
                }
                toffset *= 8;

                bufferIndex = scan * 256 + x;
                if (horiFlip[0]) {
                    for (i = 7; i >= 0; i--) {
                        if (x >= 0 && x < 256) {
                            if (
                                    bufferIndex >= 0 &&
                                            bufferIndex < 61440 &&
                                            pixrendered[bufferIndex] != 0
                            ) {
                                if (t.pixels[toffset + i] != 0) {
                                    spr0HitX = bufferIndex % 256;
                                    spr0HitY = scan;
                                    return true;
                                }
                            }
                        }
                        x++;
                        bufferIndex++;
                    }
                } else {
                    for (i = 0; i < 8; i++) {
                        if (x >= 0 && x < 256) {
                            if (
                                    bufferIndex >= 0 &&
                                            bufferIndex < 61440 &&
                                            pixrendered[bufferIndex] != 0
                            ) {
                                if (t.pixels[toffset + i] != 0) {
                                    spr0HitX = bufferIndex % 256;
                                    spr0HitY = scan;
                                    return true;
                                }
                            }
                        }
                        x++;
                        bufferIndex++;
                    }
                }
            }
        }

        return false;
    }

    private void writeMem(int address, int value) {
        vramMem[address] = value;

        // Update internally buffered data:
        if (address < 0x2000) {
            vramMem[address] = value;
            patternWrite(address, value);
        } else if (address < 0x23c0) {
            nameTableWrite(ntable1[0], address - 0x2000, value);
        } else if (address < 0x2400) {
            attribTableWrite(ntable1[0], address - 0x23c0, value);
        } else if (address < 0x27c0) {
            nameTableWrite(ntable1[1], address - 0x2400, value);
        } else if (address < 0x2800) {
            attribTableWrite(ntable1[1], address - 0x27c0, value);
        } else if (address < 0x2bc0) {
            nameTableWrite(ntable1[2], address - 0x2800, value);
        } else if (address < 0x2c00) {
            attribTableWrite(ntable1[2], address - 0x2bc0, value);
        } else if (address < 0x2fc0) {
            nameTableWrite(ntable1[3], address - 0x2c00, value);
        } else if (address < 0x3000) {
            attribTableWrite(ntable1[3], address - 0x2fc0, value);
        } else if (address >= 0x3f00 && address < 0x3f20) {
            updatePalettes();
        }
    }

    // Reads data from $3f00 to $f20
    // into the two buffered palettes.
    private void updatePalettes() {
        int i;

        for (i = 0; i < 16; i++) {
            if (f_dispType == 0) {
                imgPalette[i] = palTable.getEntry(
                        vramMem[0x3f00 + i] & 63
                );
            } else {
                imgPalette[i] = palTable.getEntry(
                        vramMem[0x3f00 + i] & 32
                );
            }
        }
        for (i = 0; i < 16; i++) {
            if (f_dispType == 0) {
                sprPalette[i] = palTable.getEntry(
                        vramMem[0x3f10 + i] & 63
                );
            } else {
                sprPalette[i] = palTable.getEntry(
                        vramMem[0x3f10 + i] & 32
                );
            }
        }
    }

    // Updates the internal pattern
    // table buffers with this new byte.
    // In vNES, there is a version of this with 4 arguments which isn't used.
    private void patternWrite(int address, int value) {
        int tileIndex = address / 16;
        int leftOver = address % 16;
        if (leftOver < 8) {
            ptTile[tileIndex].setScanline(
                    leftOver,
                    value,
                    vramMem[address + 8]
            );
        } else {
            ptTile[tileIndex].setScanline(
                    leftOver - 8,
                    vramMem[address - 8],
                    value
            );
        }
    }

    // Updates the internal name table buffers
    // with this new byte.
    private void nameTableWrite(int index, int address, int value) {
        nameTable[index].tile[address] = value;

        // Update Sprite #0 hit:
        //updateSpr0Hit();
        checkSprite0(scanline - 20);
    }

    // Updates the internal pattern
    // table buffers with this new attribute
    // table byte.
    private void attribTableWrite(int index, int address, int value) {
        nameTable[index].writeAttrib(address, value);
    }

    // Updates the internally buffered sprite
    // data with this new byte of info.
    private void spriteRamWriteUpdate(int address, int value) {
        int tIndex = address / 4;

        if (tIndex == 0) {
            //updateSpr0Hit();
            checkSprite0(scanline - 20);
        }

        if (address % 4 == 0) {
            // Y coordinate
            sprY[tIndex] = value;
        } else if (address % 4 == 1) {
            // Tile index
            sprTile[tIndex] = value;
        } else if (address % 4 == 2) {
            // Attributes
            vertFlip[tIndex] = (value & 0x80) != 0;
            horiFlip[tIndex] = (value & 0x40) != 0;
            bgPriority[tIndex] = (value & 0x20) != 0;
            sprCol[tIndex] = (value & 3) << 2;
        } else if (address % 4 == 3) {
            // X coordinate
            sprX[tIndex] = value;
        }
    }

    public boolean isPixelWhite(int x, int y) {
        triggerRendering();
        return nes.ppu.buffer[(y << 8) + x] == 0xffffff;
    }

}
