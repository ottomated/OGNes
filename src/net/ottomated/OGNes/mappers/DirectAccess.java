package net.ottomated.OGNes.mappers;

public class DirectAccess extends Mapper {
    @Override
    public int read(int addr) {
        addr &= 0xffff;
        if (addr < 0x2000) {
            return nes.cpu.memory[addr & 0x7ff];
        }
        if (addr > 0x4017) {
            return nes.cpu.memory[addr];
        } else {
            return regRead(addr);
        }
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
                        return nes.ppu.status();
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
                        // TODO: Sound channel enable, DMC Status
                        return 0;
                    case 1:
                        // 0x4016:
                        // TODO: Joystick 1 + Strobe
                        return 0;
                    case 2:
                        // 0x4017:
                        // TODO: Joystick 2 + Strobe
                        return 0;
                    break;
                }
        }
        return 0;
    }

    private int regWrite(int addr, int val) {
        switch (addr) {
            case 0x2000:
                nes.cpu.memory[addr] = val;
                nes.ppu.setCtrl1(val);
                break;
            case 0x2001:
                this.nes.cpu.memory[addr] = val;
                nes.ppu.setCtrl2(val);
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
                nes.ppu.sramDNA(val);
                break;
            case 0x4015:
                // TODO: APU Sound channel switch
                break;
            case 0x4016:
                // TODO: Joystick??
                break;
            case 0x4017:
                // TODO: APU
            default:
                if (addr >= 0x4000 && addr <= 0x4017) {
                    // TODO: APU Register write
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
}
