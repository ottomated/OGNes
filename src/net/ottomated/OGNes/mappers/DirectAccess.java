package net.ottomated.OGNes.mappers;

import net.ottomated.OGNes.Cpu;
import net.ottomated.OGNes.Tile;

import java.util.Arrays;

public class DirectAccess extends Mapper {

    private int[] joyStrobeState = new int[]{0, 0};
    private int joypadLastWrite;

    public void reset() {
        joypadLastWrite = 0;
        joyStrobeState = new int[]{0, 0};
    }

    @Override
    public int read(int addr) {
        int res;
        addr &= 0xffff;
        //System.out.println("Read " + addr);
        if (addr > 0x4017) {
            res = nes.cpu.memory[addr];
        } else if (addr >= 0x2000) {
            // I/O Ports.
            res = regRead(addr);
        } else {
            // RAM (mirrored)
            res = nes.cpu.memory[addr & 0x7ff];
        }
        //System.out.println("Read $" + Integer.toHexString(addr) + " : " + res);
        return res;
    }

    private int regRead(int addr) {
        switch (addr >> 12) {
            case 0:
            case 1:
                break;
            case 2:
            case 3:
                switch (addr & 0x7) {
                    case 0:
                        return nes.cpu.memory[0x2000];
                    case 1:
                        return nes.cpu.memory[0x2001];
                    case 2:
                        return nes.ppu.readStatusRegister();
                    case 3:
                    case 5:
                    case 6:
                        return 0;
                    case 4:
                        return nes.ppu.sramLoad();
                    case 7:
                        return nes.ppu.vramLoad();
                }
                break;
            case 4:
                switch (addr - 0x4015) {
                    case 0:
                        // 0x4015:
                        return nes.apu.readReg(addr);

                    case 1:
                        // 0x4016:
                        return nes.controllers[0].read();
                        //return joyRead(0);
                    case 2:
                        // 0x4017:
                        return nes.controllers[1].read();
                        //return joyRead(1);
                }
        }
        return 0;
    }

    private int joyRead(int stick) {
        int ret;
        switch (joyStrobeState[stick]) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                ret = 0;//this.nes.controllers[stick].state[joyStrobeState[stick]];
                break;
            case 19:
                ret = 1;
                break;
            default:
                ret = 0;
        }
        joyStrobeState[stick]++;
        if (joyStrobeState[stick] == 24) {
            joyStrobeState[stick] = 0;
        }
        //System.out.println("Controller read: " + ret);
        return ret;
    }

    private void regWrite(int addr, int val) {
        switch (addr) {
            case 0x2000:
                nes.cpu.memory[addr] = val;
                nes.ppu.updateControlReg1(val);
                break;
            case 0x2001:
                this.nes.cpu.memory[addr] = val;
                nes.ppu.updateControlReg2(val);
                break;
            case 0x2003:
                nes.ppu.writeSRAMAddress(val);
                break;
            case 0x2004:
                nes.ppu.sramWrite(val);
                break;
            case 0x2005:
                nes.ppu.scrollWrite(val);
                break;
            case 0x2006:
                nes.ppu.writeVRAMAddress(val);
                break;
            case 0x2007:
                nes.ppu.vramWrite(val);
                break;
            case 0x4014:
                nes.ppu.sramDMA(val);
                break;
            case 0x4015:
                nes.apu.writeReg(addr, val);
                break;
            case 0x4016:
                nes.controllers[0].write(val);
                nes.controllers[1].write(val);
                break;
            case 0x4017:
                nes.apu.writeReg(addr, val);
            default:
                if (addr >= 0x4000 && addr <= 0x4017) {
                    nes.apu.writeReg(addr, val);
                }
        }
    }

    @Override
    public void write(int addr, int val) {
        if (addr < 0x2000) {
            nes.cpu.memory[addr & 0x7ff] = val;
        } else if (addr > 0x4017) {
            nes.cpu.memory[addr] = val;
            // TODO: write to persistent memory?
        } else if (addr > 0x2007 && addr < 0x4000) {
            regWrite(0x2000 + (addr & 0x7), val);
        } else {
            regWrite(addr, val);
        }
    }

    @Override
    public void clockIrqCounter() {

    }

    @Override
    public void latchAccess(int addr) {

    }

    public void loadROM() {
        loadPRGROM();
        loadCHRROM();
        nes.cpu.requestIrq(Cpu.Interrupt.RESET);
    }

    private void loadPRGROM() {
        if (nes.rom.romCount > 1) {
            // Load the two first banks into memory.
            loadRomBank(0, 0x8000);
            loadRomBank(1, 0xc000);
        } else {
            // Load the one bank into both memory locations:
            loadRomBank(0, 0x8000);
            loadRomBank(0, 0xc000);
        }
    }

    private void loadCHRROM() {
        if (nes.rom.vromCount > 0) {
            if (nes.rom.vromCount == 1) {
                loadVromBank(0, 0x0000);
                loadVromBank(0, 0x1000);
            } else {
                loadVromBank(0, 0x0000);
                loadVromBank(1, 0x1000);
            }
        }
    }

    private void loadRomBank(int bank, int address) {
        // Loads a ROM bank into the specified address.
        bank %= nes.rom.romCount;
        System.arraycopy(
                this.nes.rom.rom[bank],
                0,
                nes.cpu.memory,
                address,
                16384
        );
    }

    private void loadVromBank(int bank, int address) {
        if (nes.rom.vromCount == 0) {
            return;
        }
        nes.ppu.triggerRendering();

        System.arraycopy(
                this.nes.rom.vrom[bank % this.nes.rom.vromCount],
                0,
                this.nes.ppu.vramMem,
                address,
                4096
        );

        Tile[] vromTile = this.nes.rom.vromTile[bank % this.nes.rom.vromCount];
        System.arraycopy(vromTile, 0, nes.ppu.ptTile, address >> 4, 256);
    }
}
