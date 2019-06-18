package net.ottomated.OGNes.mappers;

import net.ottomated.OGNes.Cpu;
import net.ottomated.OGNes.Rom;
import net.ottomated.OGNes.Tile;

public class MMC1 extends DirectAccess {

    int regBuffer;
    int regBufferCounter;
    int mirroring;
    int oneScreenMirroring;
    int prgSwitchingArea;
    int prgSwitchingSize;
    int vromSwitchingSize;

    int romSelectionReg0;
    int romSelectionReg1;
    int romBankSelect;

    MMC1() {

    }

    @Override
    public void write(int addr, int val) {
        if (addr < 0x8000) {
            super.write(addr, val);
            return;
        }
        if ((val & 128) != 0) {
            regBufferCounter = 0;
            regBuffer = 0;

            if (getRegNumber(addr) == 0) {
                prgSwitchingSize = 1;
                prgSwitchingArea = 1;
            }
        } else {
            regBuffer =
                    (regBuffer & (0xff - (1 << regBufferCounter))) |
                            ((val & 1) << this.regBufferCounter);
            regBufferCounter++;
            if (regBufferCounter == 5) {
                setReg(getRegNumber(addr), regBuffer);
                regBuffer = 0;
                regBufferCounter = 0;
            }
        }
    }

    private void setReg(int reg, int val) {
        int tmp;
        switch (reg) {
            case 0:
                tmp = val & 3;
                if (tmp != mirroring) {
                    mirroring = tmp;
                    if ((mirroring & 2) == 0) {
                        nes.ppu.setMirroring(Rom.Mirroring.SINGLESCREEN);
                    } else if ((mirroring & 1) != 0) {
                        nes.ppu.setMirroring(Rom.Mirroring.HORIZONTAL);
                    } else {
                        nes.ppu.setMirroring(Rom.Mirroring.VERTICAL);
                    }
                }

                prgSwitchingArea = (val >> 2) & 1;
                prgSwitchingSize = (val >> 3) & 1;

                vromSwitchingSize = (val >> 4) & 1;
                break;
            case 1:
                romSelectionReg0 = (val >> 4) & 1;

                if (nes.rom.vromCount > 0) {
                    if (vromSwitchingSize == 0) {
                        if (romSelectionReg0 == 0) {
                            load8kRomBank(val & 0xf, 0);
                        } else {
                            load8kRomBank(this.nes.rom.vromCount / 2 + (val & 0xf), 0);
                        }
                    } else {
                        if (romSelectionReg0 == 0) {
                            super.loadVromBank(val & 0xf, 0);
                        } else {
                            loadVromBank(nes.rom.vromCount / 2 + (val & 0xf), 0);
                        }
                    }
                }
                break;
            case 2:
                romSelectionReg1 = (val >> 4) & 1;
                if (nes.rom.vromCount > 0) {
                    if (vromSwitchingSize == 1) {
                        if (romSelectionReg1 == 0) {
                            loadVromBank(val & 0xf, 0x1000);
                        } else {
                            loadVromBank(nes.rom.vromCount / 2 + (val & 0xf), 0x1000);
                        }
                    }
                }
                break;
            default:
                tmp = val & 0xf;
                int bank;
                int base = 0;
                if (nes.rom.romCount >= 32) {
                    // 1024 kB cart
                    if (vromSwitchingSize == 0) {
                        if (romSelectionReg0 == 1) {
                            base = 16;
                        }
                    } else {
                        base = (romSelectionReg0 | (romSelectionReg1 << 1)) << 3;
                    }
                } else if (nes.rom.romCount >= 16) {
                    // 512 kB cart
                    if (romSelectionReg0 == 1) {
                        base = 8;
                    }
                }

                if (prgSwitchingSize == 0) {
                    // 32kB
                    bank = base + (val & 0xf);
                    load32kRomBank(bank, 0x8000);
                } else {
                    // 16kB
                    bank = base * 2 + (val & 0xf);
                    if (prgSwitchingArea == 0) {
                        loadRomBank(bank, 0xc000);
                    } else {
                        loadRomBank(bank, 0x8000);
                    }
                }
        }
    }

    private int getRegNumber(int addr) {
        if (addr >= 0x8000 && addr <= 0x9fff) {
            return 0;
        } else if (addr >= 0xa000 && addr <= 0xbfff) {
            return 1;
        } else if (addr >= 0xc000 && addr <= 0xdfff) {
            return 2;
        } else {
            return 3;
        }
    }

    private void load8kRomBank(int bank, int addr) {
        int bank16k = bank / 2 % nes.rom.romCount;
        int offset = (bank % 2) * 8192;

        System.arraycopy(nes.rom.rom[bank16k],
                offset,
                nes.cpu.memory,
                addr,
                8192
        );
    }

    @Override
    public void clockIrqCounter() {
    }

    @Override
    public void reset() {
        super.reset();
        regBuffer = 0;
        regBufferCounter = 0;
        mirroring = 0;
        oneScreenMirroring = 0;
        prgSwitchingArea = 1;
        prgSwitchingSize = 1;
        vromSwitchingSize = 0;
        romSelectionReg0 = 0;
        romSelectionReg1 = 0;
        romBankSelect = 0;

    }

    public void loadROM() {
        loadRomBank(0, 0x8000);
        loadRomBank(nes.rom.romCount - 1, 0xc000);

        super.loadCHRROM();
        nes.cpu.requestIrq(Cpu.Interrupt.RESET);
    }

    private void load32kRomBank(int bank, int addr) {

        loadRomBank((bank * 2) % nes.rom.romCount, addr);
        loadRomBank((bank * 2 + 1) % nes.rom.romCount, addr + 16384);

    }
}
