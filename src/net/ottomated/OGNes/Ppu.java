package net.ottomated.OGNes;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Ppu {
    Nes nes;
    Rom rom = nes.rom;
    Cpu cpu = nes.cpu;

    Ppu(Nes n) {
        nes = n;
    }

    int [] vramMem = null;
    int [] spriteMem = null;
    int vramAddress;
    int vramTmpAddress;
    int vramBufferedReadValue;
    boolean firstWrite;
    int sramAddress;
    Rom.Mirroring currentMirroring;
    boolean requestEndFrame;
    boolean nmiOk;
    boolean dummyCycleToggle ;
    boolean validTileData;
    int nmiCounter;
    boolean scanlineAlreadyRendered;
    int f_nmiOnVblank;
    int f_spriteSize;
    int f_bgPatternTable;
    int f_spPatternTable;
    int f_addrInc;
    int f_nTblAddress;
    int f_color;
    int f_spVisibility;
    int f_bgVisibility;
    int f_spClipping;
    int f_bgClipping;
    int f_dispType;
    int cntFV;
    int cntV;
    int cntH;
    int cntVT;
    int cntHT;
    int regFV;
    int regV;
    int regH;
    int regVT;
    int regHT;
    int regFH;
    int regS;
    int curNt;
    int [] attrib;
    int [] buffer = null;
    int [] bgbuffer = null;
    int [] pixrendered;

    ArrayList<Tile> scantile;
    int scanline;
    int lastRenderedScanline;
    int curX;
    int[] sprX;
    int[] sprY;
    int [] sprTile;
    int [] sprCol;
    boolean [] vertFlip;
    boolean [] horiFlip;
    boolean [] bgPriority;
    int spr0HitX;
    int spr0HitY;
    boolean hitSpr0;
    int [] sprPalette = null;
    int [] imgPalette = null;
    ArrayList<Tile> ptTile;
    int [] ntable1 = null;
    ArrayList<NameTable> nameTable = null;
    int [] vramMirrorTable = null;
    PaletteTable palTable = null;
        // Rendering Options:
    boolean showSpr0Hit = false;
    boolean clipToTvSize = true;

    public void reset() {
        int i;

        // Memory
        this.vramMem = new int[0x8000];
        this.spriteMem = new int[0x100];
        for (i = 0; i < this.vramMem.length; i++) {
            this.vramMem[i] = 0;
        }
        for (i = 0; i < this.spriteMem.length; i++) {
            this.spriteMem[i] = 0;
        }

        // VRAM I/O:
        //this.vramAddress = null;
        //this.vramTmpAddress = null;
        this.vramBufferedReadValue = 0;
        this.firstWrite = true; // VRAM/Scroll Hi/Lo latch

        // SPR-RAM I/O:
        this.sramAddress = 0; // 8-bit only.

        int currentMirroring = -1;
        this.requestEndFrame = false;
        this.nmiOk = false;
        this.dummyCycleToggle = false;
        this.validTileData = false;
        this.nmiCounter = 0;


        // Control Flags Register 1:
        this.f_nmiOnVblank = 0; // NMI on VBlank. 0=disable, 1=enable
        this.f_spriteSize = 0; // Sprite size. 0=8x8, 1=8x16
        this.f_bgPatternTable = 0; // Background Pattern Table address. 0=0x0000,1=0x1000
        this.f_spPatternTable = 0; // Sprite Pattern Table address. 0=0x0000,1=0x1000
        this.f_addrInc = 0; // PPU Address Increment. 0=1,1=32
        this.f_nTblAddress = 0; // Name Table Address. 0=0x2000,1=0x2400,2=0x2800,3=0x2C00

        // Control Flags Register 2:
        this.f_color = 0; // Background color. 0=black, 1=blue, 2=green, 4=red
        this.f_spVisibility = 0; // Sprite visibility. 0=not displayed,1=displayed
        this.f_bgVisibility = 0; // Background visibility. 0=Not Displayed,1=displayed
        this.f_spClipping = 0; // Sprite clipping. 0=Sprites invisible in left 8-pixel column,1=No clipping
        this.f_bgClipping = 0; // Background clipping. 0=BG invisible in left 8-pixel column, 1=No clipping
        this.f_dispType = 0; // Display type. 0=color, 1=monochrome

        // Counters:
        this.cntFV = 0;
        this.cntV = 0;
        this.cntH = 0;
        this.cntVT = 0;
        this.cntHT = 0;

        // Registers:
        this.regFV = 0;
        this.regV = 0;
        this.regH = 0;
        this.regVT = 0;
        this.regHT = 0;
        this.regFH = 0;
        this.regS = 0;

        // These are temporary variables used in rendering and sound procedures.
        // Their states outside of those procedures can be ignored.
        // TODO: the use of this is a bit weird, investigate
        //this.curNt = null;

        // Variables used when rendering:
        this.attrib = new int [32];
        this.buffer = new int[256 * 240];
        this.bgbuffer = new int[256 * 240];
        this.pixrendered = new int[256 * 240];

        //this.validTileData = null;

        //this.scantile = new int[32];

        // Initialize misc vars:
        this.scanline = 0;
        this.lastRenderedScanline = -1;
        this.curX = 0;

        // Sprite data:
        this.sprX = new int[64]; // X coordinate
        this.sprY = new int[64]; // Y coordinate
        this.sprTile = new int[64]; // Tile Index (into pattern table)
        this.sprCol = new int[64]; // Upper two bits of color
        this.vertFlip = new boolean[64]; // Vertical Flip
        this.horiFlip = new boolean[64]; // Horizontal Flip
        this.bgPriority = new boolean[64]; // Background priority
        this.spr0HitX = 0; // Sprite #0 hit X coordinate
        this.spr0HitY = 0; // Sprite #0 hit Y coordinate
        this.hitSpr0 = false;

        // Palette data:
        this.sprPalette = new int[16];
        this.imgPalette = new int[16];

        // Create pattern table tile buffers:
        //this.ptTile = new Tile[512];

        for (i = 0; i < 512; i++) {
            this.ptTile.add( new Tile());
        }

        // Create nametable buffers:
        // Name table data:
        this.ntable1 = new int[4];
        //this.currentMirroring = -1;
        //this.nameTable = new int[4];
        for (i = 0; i < 4; i++) {
            this.nameTable.add(new NameTable(32, 32, "Nt" + i));
        }

        // Initialize mirroring lookup table:
        this.vramMirrorTable = new int[0x8000];
        for (i = 0; i < 0x8000; i++) {
            this.vramMirrorTable[i] = i;
        }

        this.palTable = new PaletteTable();
        this.palTable.loadNTSCPalette();
        //this.palTable.loadDefaultPalette();

        this.updateControlReg1(0);
        this.updateControlReg2(0);
    }


    public void setMirroring(Rom.Mirroring mirroring) {
        if (mirroring == this.currentMirroring) {
            return;
        }

        this.currentMirroring = mirroring;
        this.triggerRendering();

        // Remove mirroring:
        if (this.vramMirrorTable == null) {
            this.vramMirrorTable = new int [0x8000];
        }
        for (int i = 0; i < 0x8000; i++) {
            this.vramMirrorTable[i] = i;
        }

        // Palette mirroring:
        this.defineMirrorRegion(0x3f20, 0x3f00, 0x20);
        this.defineMirrorRegion(0x3f40, 0x3f00, 0x20);
        this.defineMirrorRegion(0x3f80, 0x3f00, 0x20);
        this.defineMirrorRegion(0x3fc0, 0x3f00, 0x20);

        // Additional mirroring:
        this.defineMirrorRegion(0x3000, 0x2000, 0xf00);
        this.defineMirrorRegion(0x4000, 0x0000, 0x4000);

        if (mirroring == Rom.Mirroring.HORIZONTAL) {
            // Horizontal mirroring.

            this.ntable1[0] = 0;
            this.ntable1[1] = 0;
            this.ntable1[2] = 1;
            this.ntable1[3] = 1;

            this.defineMirrorRegion(0x2400, 0x2000, 0x400);
            this.defineMirrorRegion(0x2c00, 0x2800, 0x400);
        } else if (mirroring == Rom.Mirroring.VERTICAL) {
            // Vertical mirroring.

            this.ntable1[0] = 0;
            this.ntable1[1] = 1;
            this.ntable1[2] = 0;
            this.ntable1[3] = 1;

            this.defineMirrorRegion(0x2800, 0x2000, 0x400);
            this.defineMirrorRegion(0x2c00, 0x2400, 0x400);
        } else if (mirroring == Rom.Mirroring.SINGLESCREEN) {
            // Single Screen mirroring

            this.ntable1[0] = 0;
            this.ntable1[1] = 0;
            this.ntable1[2] = 0;
            this.ntable1[3] = 0;

            this.defineMirrorRegion(0x2400, 0x2000, 0x400);
            this.defineMirrorRegion(0x2800, 0x2000, 0x400);
            this.defineMirrorRegion(0x2c00, 0x2000, 0x400);
        } else if (mirroring == Rom.Mirroring.SINGLESCREEN2) {
            this.ntable1[0] = 1;
            this.ntable1[1] = 1;
            this.ntable1[2] = 1;
            this.ntable1[3] = 1;

            this.defineMirrorRegion(0x2400, 0x2400, 0x400);
            this.defineMirrorRegion(0x2800, 0x2400, 0x400);
            this.defineMirrorRegion(0x2c00, 0x2400, 0x400);
        } else {
            // Assume Four-screen mirroring.

            this.ntable1[0] = 0;
            this.ntable1[1] = 1;
            this.ntable1[2] = 2;
            this.ntable1[3] = 3;
        }
    }

    // Define a mirrored area in the address lookup table.
    // Assumes the regions don't overlap.
    // The 'to' region is the region that is physically in memory.
    public void defineMirrorRegion(int fromStart, int toStart, int size) {
        for (int i = 0; i < size; i++) {
            this.vramMirrorTable[fromStart + i] = toStart + i;
        }
    }

    public void startVBlank() {
        // Do NMI:
        this.nes.cpu.requestIrq(Cpu.Interrupt.NMI);

        // Make sure everything is rendered:
        if (this.lastRenderedScanline < 239) {
            this.renderFramePartially(
                    this.lastRenderedScanline + 1,
                    240 - this.lastRenderedScanline
            );
        }

        // End frame:
        this.endFrame();

        // Reset scanline counter:
        this.lastRenderedScanline = -1;
    }

    public void endScanline() {
        switch (this.scanline) {
            case 19:
                // Dummy scanline.
                // May be variable length:
                if (this.dummyCycleToggle) {
                    // Remove dead cycle at end of scanline,
                    // for next scanline:
                    this.curX = 1;
                    this.dummyCycleToggle = !this.dummyCycleToggle;
                }
                break;

            case 20:
                // Clear VBlank flag:
                this.setStatusFlag(this.STATUS_VBLANK, false);

                // Clear Sprite #0 hit flag:
                this.setStatusFlag(this.STATUS_SPRITE0HIT, false);
                this.hitSpr0 = false;
                this.spr0HitX = -1;
                this.spr0HitY = -1;

                if (this.f_bgVisibility == 1 || this.f_spVisibility == 1) {
                    // Update counters:
                    this.cntFV = this.regFV;
                    this.cntV = this.regV;
                    this.cntH = this.regH;
                    this.cntVT = this.regVT;
                    this.cntHT = this.regHT;

                    if (this.f_bgVisibility == 1) {
                        // Render dummy scanline:
                        this.renderBgScanline(false, 0);
                    }
                }

                if (this.f_bgVisibility == 1 && this.f_spVisibility == 1) {
                    // Check sprite 0 hit for first scanline:
                    this.checkSprite0(0);
                }

                if (this.f_bgVisibility == 1 || this.f_spVisibility == 1) {
                    // Clock mapper IRQ Counter:
                    this.nes.mapper.clockIrqCounter();
                }
                break;

            case 261:
                // Dead scanline, no rendering.
                // Set VINT:
                this.setStatusFlag(this.STATUS_VBLANK, true);
                this.requestEndFrame = true;
                this.nmiCounter = 9;

                // Wrap around:
                this.scanline = -1; // will be incremented to 0

                break;

            default:
                if (this.scanline >= 21 && this.scanline <= 260) {
                    // Render normally:
                    if (this.f_bgVisibility == 1) {
                        if (!this.scanlineAlreadyRendered) {
                            // update scroll:
                            this.cntHT = this.regHT;
                            this.cntH = this.regH;
                            this.renderBgScanline(true, this.scanline + 1 - 21);
                        }
                        this.scanlineAlreadyRendered = false;

                        // Check for sprite 0 (next scanline):
                        if (!this.hitSpr0 && this.f_spVisibility == 1) {
                            if (
                                    this.sprX[0] >= -7 &&
                                            this.sprX[0] < 256 &&
                                            this.sprY[0] + 1 <= this.scanline - 20 &&
                                            this.sprY[0] + 1 + (this.f_spriteSize == 0 ? 8 : 16) >=
                                                    this.scanline - 20
                            ) {
                                if (this.checkSprite0(this.scanline - 20)) {
                                    this.hitSpr0 = true;
                                }
                            }
                        }
                    }

                    if (this.f_bgVisibility == 1 || this.f_spVisibility == 1) {
                        // Clock mapper IRQ Counter:
                        this.nes.mapper.clockIrqCounter();
                    }
                }
        }

        this.scanline++;
        this.regsToAddress();
        this.cntsToAddress();
    }

    public void startFrame() {
        // Set background color:
        int bgColor = 0;

        if (this.f_dispType == 0) {
            // Color display.
            // f_color determines color emphasis.
            // Use first entry of image palette as BG color.
            bgColor = this.imgPalette[0];
        } else {
            // Monochrome display.
            // f_color determines the bg color.
            switch (this.f_color) {
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

        int[] buffer = this.buffer;
        int i;
        for (i = 0; i < 256 * 240; i++) {
            buffer[i] = bgColor;
        }
        int[] pixrendered = this.pixrendered;
        for (i = 0; i < pixrendered.length; i++) {
            pixrendered[i] = 65;
        }
    }

    public void endFrame() {
        int i, x, y;
        int[] buffer = this.buffer;

        // Draw spr#0 hit coordinates:
        if (this.showSpr0Hit) {
            // Spr 0 position:
            if (
                    this.sprX[0] >= 0 &&
                            this.sprX[0] < 256 &&
                            this.sprY[0] >= 0 &&
                            this.sprY[0] < 240
            ) {
                for (i = 0; i < 256; i++) {
                    buffer[(this.sprY[0] << 8) + i] = 0xff5555;
                }
                for (i = 0; i < 240; i++) {
                    buffer[(i << 8) + this.sprX[0]] = 0xff5555;
                }
            }
            // Hit position:
            if (
                    this.spr0HitX >= 0 &&
                            this.spr0HitX < 256 &&
                            this.spr0HitY >= 0 &&
                            this.spr0HitY < 240
            ) {
                for (i = 0; i < 256; i++) {
                    buffer[(this.spr0HitY << 8) + i] = 0x55ff55;
                }
                for (i = 0; i < 240; i++) {
                    buffer[(i << 8) + this.spr0HitX] = 0x55ff55;
                }
            }
        }

        // This is a bit lazy..
        // if either the sprites or the background should be clipped,
        // both are clipped after rendering is finished.
        if (
                this.clipToTvSize ||
                        this.f_bgClipping == 0 ||
                        this.f_spClipping == 0
        ) {
            // Clip left 8-pixels column:
            for (y = 0; y < 240; y++) {
                for (x = 0; x < 8; x++) {
                    buffer[(y << 8) + x] = 0;
                }
            }
        }

        if (this.clipToTvSize) {
            // Clip right 8-pixels column too:
            for (y = 0; y < 240; y++) {
                for (x = 0; x < 8; x++) {
                    buffer[(y << 8) + 255 - x] = 0;
                }
            }
        }

        // Clip top and bottom 8 pixels:
        if (this.clipToTvSize) {
            for (y = 0; y < 8; y++) {
                for (x = 0; x < 256; x++) {
                    buffer[(y << 8) + x] = 0;
                    buffer[((239 - y) << 8) + x] = 0;
                }
            }
        }

        this.nes.graphics.writeFrame(buffer);

    }

    public void updateControlReg1(int value) {
        this.triggerRendering();

        this.f_nmiOnVblank = (value >> 7) & 1;
        this.f_spriteSize = (value >> 5) & 1;
        this.f_bgPatternTable = (value >> 4) & 1;
        this.f_spPatternTable = (value >> 3) & 1;
        this.f_addrInc = (value >> 2) & 1;
        this.f_nTblAddress = value & 3;

        this.regV = (value >> 1) & 1;
        this.regH = value & 1;
        this.regS = (value >> 4) & 1;
    }

    public void updateControlReg2(int value) {
        this.triggerRendering();

        this.f_color = (value >> 5) & 7;
        this.f_spVisibility = (value >> 4) & 1;
        this.f_bgVisibility = (value >> 3) & 1;
        this.f_spClipping = (value >> 2) & 1;
        this.f_bgClipping = (value >> 1) & 1;
        this.f_dispType = value & 1;

        if (this.f_dispType == 0) {
            this.palTable.setEmphasis(this.f_color);
        }
        this.updatePalettes();
    }

    public void setStatusFlag: function(int flag, boolean value) {
        int n = 1 << flag;
        this.nes.cpu.memory[0x2002] =
                (this.nes.cpu.memory[0x2002] & (255 - n)) | (value ? n : 0);
    }

    // CPU Register $2002:
    // Read the Status Register.
    public int readStatusRegister() {
        int tmp = this.nes.cpu.memory[0x2002];

        // Reset scroll & VRAM Address toggle:
        this.firstWrite = true;

        // Clear VBlank flag:
        this.setStatusFlag(this.STATUS_VBLANK, false);

        // Fetch status data:
        return tmp;
    }

    // CPU Register $2003:
    // Write the SPR-RAM address that is used for sramWrite (Register 0x2004 in CPU memory map)
    public void writeSRAMAddress(int address) {
        this.sramAddress = address;
    }

    // CPU Register $2004 (R):
    // Read from SPR-RAM (Sprite RAM).
    // The address should be set first.
    public int sramLoad() {
    /*short tmp = sprMem.load(sramAddress);
        sramAddress++; // Increment address
        sramAddress%=0x100;
        return tmp;*/
        return this.spriteMem[this.sramAddress];
    }

    // CPU Register $2004 (W):
    // Write to SPR-RAM (Sprite RAM).
    // The address should be set first.
    public void sramWrite(int value) {
        this.spriteMem[this.sramAddress] = value;
        this.spriteRamWriteUpdate(this.sramAddress, value);
        this.sramAddress++; // Increment address
        this.sramAddress %= 0x100;
    }

    // CPU Register $2005:
    // Write to scroll registers.
    // The first write is the vertical offset, the second is the
    // horizontal offset:
    public void scrollWrite(int value) {
        this.triggerRendering();

        if (this.firstWrite) {
            // First write, horizontal scroll:
            this.regHT = (value >> 3) & 31;
            this.regFH = value & 7;
        } else {
            // Second write, vertical scroll:
            this.regFV = value & 7;
            this.regVT = (value >> 3) & 31;
        }
        this.firstWrite = !this.firstWrite;
    }

    // CPU Register $2006:
    // Sets the adress used when reading/writing from/to VRAM.
    // The first write sets the high byte, the second the low byte.
    public void writeVRAMAddress(int address) {
        if (this.firstWrite) {
            this.regFV = (address >> 4) & 3;
            this.regV = (address >> 3) & 1;
            this.regH = (address >> 2) & 1;
            this.regVT = (this.regVT & 7) | ((address & 3) << 3);
        } else {
            this.triggerRendering();

            this.regVT = (this.regVT & 24) | ((address >> 5) & 7);
            this.regHT = address & 31;

            this.cntFV = this.regFV;
            this.cntV = this.regV;
            this.cntH = this.regH;
            this.cntVT = this.regVT;
            this.cntHT = this.regHT;

            this.checkSprite0(this.scanline - 20);
        }

        this.firstWrite = !this.firstWrite;

        // Invoke mapper latch:
        this.cntsToAddress();
        if (this.vramAddress < 0x2000) {
            this.nes.mapper.latchAccess(this.vramAddress);
        }
    }

    // CPU Register $2007(R):
    // Read from PPU memory. The address should be set first.
    public int vramLoad() {
        int tmp;

        this.cntsToAddress();
        this.regsToAddress();

        // If address is in range 0x0000-0x3EFF, return buffered values:
        if (this.vramAddress <= 0x3eff) {
            tmp = this.vramBufferedReadValue;

            // Update buffered value:
            if (this.vramAddress < 0x2000) {
                this.vramBufferedReadValue = this.vramMem[this.vramAddress];
            } else {
                this.vramBufferedReadValue = this.mirroredLoad(this.vramAddress);
            }

            // Mapper latch access:
            if (this.vramAddress < 0x2000) {
                this.nes.mapper.latchAccess(this.vramAddress);
            }

            // Increment by either 1 or 32, depending on d2 of Control Register 1:
            this.vramAddress += this.f_addrInc == 1 ? 32 : 1;

            this.cntsFromAddress();
            this.regsFromAddress();

            return tmp; // Return the previous buffered value.
        }

        // No buffering in this mem range. Read normally.
        tmp = this.mirroredLoad(this.vramAddress);

        // Increment by either 1 or 32, depending on d2 of Control Register 1:
        this.vramAddress += this.f_addrInc == 1 ? 32 : 1;

        this.cntsFromAddress();
        this.regsFromAddress();

        return tmp;
    }

    // CPU Register $2007(W):
    // Write to PPU memory. The address should be set first.
    public void vramWrite(int value) {
        this.triggerRendering();
        this.cntsToAddress();
        this.regsToAddress();

        if (this.vramAddress >= 0x2000) {
            // Mirroring is used.
            this.mirroredWrite(this.vramAddress, value);
        } else {
            // Write normally.
            this.writeMem(this.vramAddress, value);

            // Invoke mapper latch:
            this.nes.mapper.latchAccess(this.vramAddress);
        }

        // Increment by either 1 or 32, depending on d2 of Control Register 1:
        this.vramAddress += this.f_addrInc == 1 ? 32 : 1;
        this.regsFromAddress();
        this.cntsFromAddress();
    }

    // CPU Register $4014:
    // Write 256 bytes of main memory
    // into Sprite RAM.
    public void sramDMA(int value) {
        int baseAddress = value * 0x100;
        int data;
        for (int i = this.sramAddress; i < 256; i++) {
            data = this.nes.cpu.memory[baseAddress + i];
            this.spriteMem[i] = data;
            this.spriteRamWriteUpdate(i, data);
        }

        //this.nes.cpu.haltCycles(513);
    }

    public void regsFromAddress() {
        int address = (this.vramTmpAddress >> 8) & 0xff;
        this.regFV = (address >> 4) & 7;
        this.regV = (address >> 3) & 1;
        this.regH = (address >> 2) & 1;
        this.regVT = (this.regVT & 7) | ((address & 3) << 3);

        address = this.vramTmpAddress & 0xff;
        this.regVT = (this.regVT & 24) | ((address >> 5) & 7);
        this.regHT = address & 31;
    }

    // Updates the scroll registers from a new VRAM address.
    public void cntsFromAddress() {
        int address = (this.vramAddress >> 8) & 0xff;
        this.cntFV = (address >> 4) & 3;
        this.cntV = (address >> 3) & 1;
        this.cntH = (address >> 2) & 1;
        this.cntVT = (this.cntVT & 7) | ((address & 3) << 3);

        address = this.vramAddress & 0xff;
        this.cntVT = (this.cntVT & 24) | ((address >> 5) & 7);
        this.cntHT = address & 31;
    }

    public void regsToAddress() {
        int b1 = (this.regFV & 7) << 4;
        b1 |= (this.regV & 1) << 3;
        b1 |= (this.regH & 1) << 2;
        b1 |= (this.regVT >> 3) & 3;

        int b2 = (this.regVT & 7) << 5;
        b2 |= this.regHT & 31;

        this.vramTmpAddress = ((b1 << 8) | b2) & 0x7fff;
    }

    public void cntsToAddress() {
        int b1 = (this.cntFV & 7) << 4;
        b1 |= (this.cntV & 1) << 3;
        b1 |= (this.cntH & 1) << 2;
        b1 |= (this.cntVT >> 3) & 3;

        int b2 = (this.cntVT & 7) << 5;
        b2 |= this.cntHT & 31;

        this.vramAddress = ((b1 << 8) | b2) & 0x7fff;
    }

    public void incTileCounter(int count) {
        for (int i = count; i != 0; i--) {
            this.cntHT++;
            if (this.cntHT == 32) {
                this.cntHT = 0;
                this.cntVT++;
                if (this.cntVT >= 30) {
                    this.cntH++;
                    if (this.cntH == 2) {
                        this.cntH = 0;
                        this.cntV++;
                        if (this.cntV == 2) {
                            this.cntV = 0;
                            this.cntFV++;
                            this.cntFV &= 0x7;
                        }
                    }
                }
            }
        }
    }

    // Reads from memory, taking into account
    // mirroring/mapping of address ranges.
    public int mirroredLoad(int address) {
        return this.vramMem[this.vramMirrorTable[address]];
    }

    // Writes to memory, taking into account
    // mirroring/mapping of address ranges.
    public void mirroredWrite(int address, int value) {
        if (address >= 0x3f00 && address < 0x3f20) {
            // Palette write mirroring.
            if (address == 0x3f00 || address == 0x3f10) {
                this.writeMem(0x3f00, value);
                this.writeMem(0x3f10, value);
            } else if (address == 0x3f04 || address == 0x3f14) {
                this.writeMem(0x3f04, value);
                this.writeMem(0x3f14, value);
            } else if (address == 0x3f08 || address == 0x3f18) {
                this.writeMem(0x3f08, value);
                this.writeMem(0x3f18, value);
            } else if (address == 0x3f0c || address == 0x3f1c) {
                this.writeMem(0x3f0c, value;
                this.writeMem(0x3f1c, value);
            } else {
                this.writeMem(address, value);
            }
        } else {
            // Use lookup table for mirrored address:
            if (address < this.vramMirrorTable.length) {
                this.writeMem(this.vramMirrorTable[address], value);
            } else {
                throw new Error("Invalid VRAM address: " + address.toString(16));
            }
        }
    }

    public void triggerRendering() {
        if (this.scanline >= 21 && this.scanline <= 260) {
            // Render sprites, and combine:
            this.renderFramePartially(
                    this.lastRenderedScanline + 1,
                    this.scanline - 21 - this.lastRenderedScanline
            );

            // Set last rendered scanline:
            this.lastRenderedScanline = this.scanline - 21;
        }
    }

    public void renderFramePartially(int startScan, int scanCount) {
        if (this.f_spVisibility == 1) {
            this.renderSpritesPartially(startScan, scanCount, true);
        }

        if (this.f_bgVisibility == 1) {
            int si = startScan << 8;
            int ei = (startScan + scanCount) << 8;
            if (ei > 0xf000) {
                ei = 0xf000;
            }
            int[] buffer = this.buffer;
            int[] bgbuffer = this.bgbuffer;
            int[] pixrendered = this.pixrendered;
            for (int destIndex = si; destIndex < ei; destIndex++) {
                if (pixrendered[destIndex] > 0xff) {
                    buffer[destIndex] = bgbuffer[destIndex];
                }
            }
        }

        if (this.f_spVisibility == 1) {
            this.renderSpritesPartially(startScan, scanCount, false);
        }

        this.validTileData = false;
    }

    public int renderBgScanline(boolean bgbuffer, int scan) {
        int baseTile = this.regS == 0 ? 0 : 256;
        int destIndex = (scan << 8) - this.regFH;

        this.curNt = this.ntable1[this.cntV + this.cntV + this.cntH];

        this.cntHT = this.regHT;
        this.cntH = this.regH;
        this.curNt = this.ntable1[this.cntV + this.cntV + this.cntH];

        if (scan < 240 && scan - this.cntFV >= 0) {
            int tscanoffset = this.cntFV << 3;
            ArrayList<Tile> scantile = this.scantile;
            int[] attrib = this.attrib;
            ArrayList<Tile> ptTile = this.ptTile;
            ArrayList<NameTable> nameTable = this.nameTable;
            int[] imgPalette = this.imgPalette;
            int[] pixrendered = this.pixrendered;
            int[] targetBuffer = bgbuffer ? this.bgbuffer : this.buffer;

            Tile t;
            int[] tpix;
            int att, col;

            for (int tile = 0; tile < 32; tile++) {
                if (scan >= 0) {
                    // Fetch tile & attrib data:
                    if (this.validTileData) {
                        // Get data from array:
                        t = scantile.get(tile);
                        if (typeof t == "undefined") {
                            continue;
                        }
                        tpix = t.pixels;
                        att = attrib[tile];
                    } else {
                        // Fetch data:
                        t = ptTile.get(baseTile + nameTable.get(this.curNt).getTileIndex(this.cntHT, this.cntVT));
                        if (typeof t == "undefined") {
                            continue;
                        }
                        tpix = t.pixels;
                        att = nameTable.get(this.curNt).getAttrib(this.cntHT, this.cntVT);
                        scantile.set(tile, t);
                        attrib[tile] = att;
                    }

                    // Render tile scanline:
                    int sx = 0;
                    int x = (tile << 3) - this.regFH;

                    if (x > -8) {
                        if (x < 0) {
                            destIndex -= x;
                            sx = -x;
                        }
                        if (t.opaque[this.cntFV]) {
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
                if (++this.cntHT == 32) {
                    this.cntHT = 0;
                    this.cntH++;
                    this.cntH %= 2;
                    this.curNt = this.ntable1[(this.cntV << 1) + this.cntH];
                }
            }

            // Tile data for one row should now have been fetched,
            // so the data in the array is valid.
            this.validTileData = true;
        }

        // update vertical scroll:
        this.cntFV++;
        if (this.cntFV == 8) {
            this.cntFV = 0;
            this.cntVT++;
            if (this.cntVT == 30) {
                this.cntVT = 0;
                this.cntV++;
                this.cntV %= 2;
                this.curNt = this.ntable1[(this.cntV << 1) + this.cntH];
            } else if (this.cntVT == 32) {
                this.cntVT = 0;
            }

            // Invalidate fetched data:
            this.validTileData = false;
        }
    }

    public void renderSpritesPartially(int startscan, int scancount, boolean bgPri) {
        if (this.f_spVisibility == 1) {
            for (int i = 0; i < 64; i++) {
                if (
                        this.bgPriority[i] == bgPri &&
                                this.sprX[i] >= 0 &&
                                this.sprX[i] < 256 &&
                                this.sprY[i] + 8 >= startscan &&
                                this.sprY[i] < startscan + scancount
                ) {
                    // Show sprite.
                    if (this.f_spriteSize == 0) {
                        // 8x8 sprites

                        int srcy1 = 0;
                        int srcy2 = 8;

                        if (this.sprY[i] < startscan) {
                            srcy1 = startscan - this.sprY[i] - 1;
                        }

                        if (this.sprY[i] + 8 > startscan + scancount) {
                            srcy2 = startscan + scancount - this.sprY[i] + 1;
                        }

                        if (this.f_spPatternTable == 0) {
                            this.ptTile.get(this.sprTile[i]).render(
                                    this.buffer,
                                    0,
                                    srcy1,
                                    8,
                                    srcy2,
                                    this.sprX[i],
                                    this.sprY[i] + 1,
                                    this.sprCol[i],
                                    this.sprPalette,
                                    this.horiFlip[i],
                                    this.vertFlip[i],
                                    i,
                                    this.pixrendered
                            );
                        } else {
                            this.ptTile.get(this.sprTile[i] + 256).render(
                                    this.buffer,
                                    0,
                                    srcy1,
                                    8,
                                    srcy2,
                                    this.sprX[i],
                                    this.sprY[i] + 1,
                                    this.sprCol[i],
                                    this.sprPalette,
                                    this.horiFlip[i],
                                    this.vertFlip[i],
                                    i,
                                    this.pixrendered
                            );
                        }
                    } else {
                        // 8x16 sprites
                        int top = this.sprTile[i];
                        if ((top & 1) != 0) {
                            top = this.sprTile[i] - 1 + 256;
                        }

                        int srcy1 = 0;
                        int srcy2 = 8;

                        if (this.sprY[i] < startscan) {
                            srcy1 = startscan - this.sprY[i] - 1;
                        }

                        if (this.sprY[i] + 8 > startscan + scancount) {
                            srcy2 = startscan + scancount - this.sprY[i];
                        }

                        this.ptTile.get(top + (this.vertFlip[i] ? 1 : 0)).render(
                                this.buffer,
                                0,
                                srcy1,
                                8,
                                srcy2,
                                this.sprX[i],
                                this.sprY[i] + 1,
                                this.sprCol[i],
                                this.sprPalette,
                                this.horiFlip[i],
                                this.vertFlip[i],
                                i,
                                this.pixrendered
                        );

                        srcy1 = 0;
                        srcy2 = 8;

                        if (this.sprY[i] + 8 < startscan) {
                            srcy1 = startscan - (this.sprY[i] + 8 + 1);
                        }

                        if (this.sprY[i] + 16 > startscan + scancount) {
                            srcy2 = startscan + scancount - (this.sprY[i] + 8);
                        }

                        this.ptTile.get(top + (this.vertFlip[i] ? 0 : 1)).render(
                                this.buffer,
                                0,
                                srcy1,
                                8,
                                srcy2,
                                this.sprX[i],
                                this.sprY[i] + 1 + 8,
                                this.sprCol[i],
                                this.sprPalette,
                                this.horiFlip[i],
                                this.vertFlip[i],
                                i,
                                this.pixrendered
                        );
                    }
                }
            }
        }
    }

    public boolean checkSprite0(int scan) {
        this.spr0HitX = -1;
        this.spr0HitY = -1;

        int toffset;
        int tIndexAdd = this.f_spPatternTable == 0 ? 0 : 256;
        int x, y, i;
        Tile t;
        int bufferIndex;

        x = this.sprX[0];
        y = this.sprY[0] + 1;

        if (this.f_spriteSize == 0) {
            // 8x8 sprites.

            // Check range:
            if (y <= scan && y + 8 > scan && x >= -7 && x < 256) {
                // Sprite is in range.
                // Draw scanline:
                t = this.ptTile.get(this.sprTile[0] + tIndexAdd);

                if (this.vertFlip[0]) {
                    toffset = 7 - (scan - y);
                } else {
                    toffset = scan - y;
                }
                toffset *= 8;

                bufferIndex = scan * 256 + x;
                if (this.horiFlip[0]) {
                    for (i = 7; i >= 0; i--) {
                        if (x >= 0 && x < 256) {
                            if (
                                    bufferIndex >= 0 &&
                                            bufferIndex < 61440 &&
                                            this.pixrendered[bufferIndex] != 0
                            ) {
                                if (t.pixels[toffset + i] != 0) {
                                    this.spr0HitX = bufferIndex % 256;
                                    this.spr0HitY = scan;
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
                                            this.pixrendered[bufferIndex] != 0
                            ) {
                                if (t.pixels[toffset + i] != 0) {
                                    this.spr0HitX = bufferIndex % 256;
                                    this.spr0HitY = scan;
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

                if (this.vertFlip[0]) {
                    toffset = 15 - (scan - y);
                } else {
                    toffset = scan - y;
                }

                if (toffset < 8) {
                    // first half of sprite.
                    t = this.ptTile.get(
                            this.sprTile[0] +
                                    (this.vertFlip[0] ? 1 : 0) +
                                    ((this.sprTile[0] & 1) != 0 ? 255 : 0)
                    );
                } else {
                    // second half of sprite.
                    t = this.ptTile.get(
                            this.sprTile[0] +
                                    (this.vertFlip[0] ? 0 : 1) +
                                    ((this.sprTile[0] & 1) != 0 ? 255 : 0)
                    );
                    if (this.vertFlip[0]) {
                        toffset = 15 - toffset;
                    } else {
                        toffset -= 8;
                    }
                }
                toffset *= 8;

                bufferIndex = scan * 256 + x;
                if (this.horiFlip[0]) {
                    for (i = 7; i >= 0; i--) {
                        if (x >= 0 && x < 256) {
                            if (
                                    bufferIndex >= 0 &&
                                            bufferIndex < 61440 &&
                                            this.pixrendered[bufferIndex] != 0
                            ) {
                                if (t.pixels[toffset + i] != 0) {
                                    this.spr0HitX = bufferIndex % 256;
                                    this.spr0HitY = scan;
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
                                            this.pixrendered[bufferIndex] != 0
                            ) {
                                if (t.pixels[toffset + i] != 0) {
                                    this.spr0HitX = bufferIndex % 256;
                                    this.spr0HitY = scan;
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

    public void writeMem(int address, int value) {
        this.vramMem[address] = value;

        // Update internally buffered data:
        if (address < 0x2000) {
            this.vramMem[address] = value;
            this.patternWrite(address, value);
        } else if (address >= 0x2000 && address < 0x23c0) {
            this.nameTableWrite(this.ntable1[0], address - 0x2000, value);
        } else if (address >= 0x23c0 && address < 0x2400) {
            this.attribTableWrite(this.ntable1[0], address - 0x23c0, value);
        } else if (address >= 0x2400 && address < 0x27c0) {
            this.nameTableWrite(this.ntable1[1], address - 0x2400, value);
        } else if (address >= 0x27c0 && address < 0x2800) {
            this.attribTableWrite(this.ntable1[1], address - 0x27c0, value);
        } else if (address >= 0x2800 && address < 0x2bc0) {
            this.nameTableWrite(this.ntable1[2], address - 0x2800, value);
        } else if (address >= 0x2bc0 && address < 0x2c00) {
            this.attribTableWrite(this.ntable1[2], address - 0x2bc0, value);
        } else if (address >= 0x2c00 && address < 0x2fc0) {
            this.nameTableWrite(this.ntable1[3], address - 0x2c00, value);
        } else if (address >= 0x2fc0 && address < 0x3000) {
            this.attribTableWrite(this.ntable1[3], address - 0x2fc0, value);
        } else if (address >= 0x3f00 && address < 0x3f20) {
            this.updatePalettes();
        }
    }

    // Reads data from $3f00 to $f20
    // into the two buffered palettes.
    public void updatePalettes() {
        int i;

        for (i = 0; i < 16; i++) {
            if (this.f_dispType == 0) {
                this.imgPalette[i] = this.palTable.getEntry(
                        this.vramMem[0x3f00 + i] & 63
                );
            } else {
                this.imgPalette[i] = this.palTable.getEntry(
                        this.vramMem[0x3f00 + i] & 32
                );
            }
        }
        for (i = 0; i < 16; i++) {
            if (this.f_dispType == 0) {
                this.sprPalette[i] = this.palTable.getEntry(
                        this.vramMem[0x3f10 + i] & 63
                );
            } else {
                this.sprPalette[i] = this.palTable.getEntry(
                        this.vramMem[0x3f10 + i] & 32
                );
            }
        }
    }

    // Updates the internal pattern
    // table buffers with this new byte.
    // In vNES, there is a version of this with 4 arguments which isn't used.
    public void patternWrite(int address, int value) {
        int tileIndex = (int) Math.floor(address / 16);
        int leftOver = address % 16;
        if (leftOver < 8) {
            this.ptTile.get(tileIndex).setScanline(
                    leftOver,
                    value,
                    this.vramMem[address + 8]
            );
        } else {
            this.ptTile.get(tileIndex).setScanline(
                    leftOver - 8,
                    this.vramMem[address - 8],
                    value
            );
        }
    }

    // Updates the internal name table buffers
    // with this new byte.
    public void nameTableWrite(int index, int address, int value) {
        this.nameTable.get(index).tile[address] = value;

        // Update Sprite #0 hit:
        //updateSpr0Hit();
        this.checkSprite0(this.scanline - 20);
    }

    // Updates the internal pattern
    // table buffers with this new attribute
    // table byte.
    public void attribTableWrite(int index, int address, int value) {
        this.nameTable.get(index).writeAttrib(address, value);
    }

    // Updates the internally buffered sprite
    // data with this new byte of info.
    public void spriteRamWriteUpdate(int address, int value) {
        int tIndex = (int) Math.floor(address / 4);

        if (tIndex == 0) {
            //updateSpr0Hit();
            this.checkSprite0(this.scanline - 20);
        }

        if (address % 4 == 0) {
            // Y coordinate
            this.sprY[tIndex] = value;
        } else if (address % 4 == 1) {
            // Tile index
            this.sprTile[tIndex] = value;
        } else if (address % 4 == 2) {
            // Attributes
            this.vertFlip[tIndex] = (value & 0x80) != 0;
            this.horiFlip[tIndex] = (value & 0x40) != 0;
            this.bgPriority[tIndex] = (value & 0x20) != 0;
            this.sprCol[tIndex] = (value & 3) << 2;
        } else if (address % 4 == 3) {
            // X coordinate
            this.sprX[tIndex] = value;
        }
    }

    public void doNMI() {
        // Set VBlank flag:
        this.setStatusFlag(this.STATUS_VBLANK, true);
        //nes.getCpu().doNonMaskableInterrupt();
        this.nes.cpu.requestIrq(Cpu.Interrupt.NMI);
    }

    public boolean isPixelWhite(int x, int y) {
        this.triggerRendering();
        return this.nes.ppu.buffer[(y << 8) + x] == 0xffffff;
    }


    /*
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
/*
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
        if(address < 0x2000 && this.rom.header.chr_num != 0) {
            var mappedAddr = this.mapper.chrMap(address, this.rom.header);
            return this.rom.chr[mappedAddr];
        }
         */
/*
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
        if(address < 0x2000 && this.rom.header.chr_num != 0) {
            var mappedAddr = this.mapper.chrMap(address, this.rom.header);
            this.rom.chr[mappedAddr] = value;
            return;
        }
        */
/*
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
    */
}
