package net.ottomated.OGNes.mappers;

import net.ottomated.OGNes.Cpu;
import net.ottomated.OGNes.Rom;
import net.ottomated.OGNes.Tile;

public class MMC3 extends DirectAccess {

    private enum Command {
        SEL_2_1K_VROM_0000,
        SEL_2_1K_VROM_0800,
        SEL_1K_VROM_1000,
        SEL_1K_VROM_1400,
        SEL_1K_VROM_1800,
        SEL_1K_VROM_1C00,
        SEL_ROM_PAGE1,
        SEL_ROM_PAGE2,
    }

    private Command command;
    private int prgAddressSelect;
    private boolean prgAddressChanged;
    private int chrAddressSelect;
    private int irqCounter;
    private int irqLatchValue;
    private int irqEnable;

    MMC3() {

    }

    @Override
    public void write(int addr, int val) {
        if (addr < 0x8000) {
            super.write(addr, val);
            return;
        }
        switch (addr) {
            case 0x8000:
                command = Command.values()[val & 7];
                int tmp = (val >> 6) & 1;
                if (tmp != prgAddressSelect) {
                    prgAddressChanged = true;
                }
                prgAddressSelect = tmp;
                chrAddressSelect = (val >> 7) & 1;
                break;
            case 0x8001:
                executeCommand(command, val);
                break;
            case 0xa000:
                if ((val & 1) != 0) {
                    nes.ppu.setMirroring(Rom.Mirroring.HORIZONTAL);
                } else {
                    nes.ppu.setMirroring(Rom.Mirroring.VERTICAL);
                }
                break;
            case 0xa001:
                // TODO saveRAM
                break;
            case 0xc000:
                irqCounter = val;
                break;
            case 0xc001:
                irqLatchValue = val;
                break;
            case 0xe000:
                irqEnable = 0;
                break;
            case 0xe001:
                irqEnable = 1;
                break;
            default:
                System.out.println("MMC3: Weird write");
        }
    }

    private void executeCommand(Command cmd, int arg) {
        switch (cmd) {
            case SEL_2_1K_VROM_0000:
                if (chrAddressSelect == 0) {
                    load1kVromBank(arg, 0);
                    load1kVromBank(arg + 1, 0x0400);
                } else {
                    load1kVromBank(arg, 0x1000);
                    load1kVromBank(arg + 1, 0x1400);
                }
                break;
            case SEL_2_1K_VROM_0800:
                if (chrAddressSelect == 0) {
                    load1kVromBank(arg, 0x0800);
                    load1kVromBank(arg + 1, 0x0c00);
                } else {
                    load1kVromBank(arg, 0x1800);
                    load1kVromBank(arg + 1, 0x1c00);
                }
                break;
            case SEL_1K_VROM_1000:
                if (chrAddressSelect == 0) {
                    load1kVromBank(arg, 0x1000);
                } else {
                    load1kVromBank(arg, 0);
                }
                break;
            case SEL_1K_VROM_1400:
                if (chrAddressSelect == 0) {
                    load1kVromBank(arg, 0x1400);
                } else {
                    load1kVromBank(arg, 0x0400);
                }
                break;
            case SEL_1K_VROM_1800:
                if (chrAddressSelect == 0) {
                    load1kVromBank(arg, 0x1800);
                } else {
                    load1kVromBank(arg, 0x0800);
                }
                break;
            case SEL_1K_VROM_1C00:
                if (chrAddressSelect == 0) {
                    load1kVromBank(arg, 0x1c00);
                } else {
                    load1kVromBank(arg, 0x0c00);
                }
                break;
            case SEL_ROM_PAGE1:
                if (prgAddressChanged) {
                    if (prgAddressSelect == 0) {
                        load8kRomBank((nes.rom.romCount - 1) * 2, 0xc000);
                    } else {
                        load8kRomBank((nes.rom.romCount - 1) * 2, 0x8000);
                    }
                    prgAddressChanged = false;
                }
                if (prgAddressSelect == 0) {
                    load8kRomBank(arg, 0x8000);
                } else {
                    load8kRomBank(arg, 0xc000);
                }
                break;
            case SEL_ROM_PAGE2:
                load8kRomBank(arg, 0xa000);

                if (prgAddressChanged) {
                    if (prgAddressSelect == 0) {
                        load8kRomBank((nes.rom.romCount - 1) * 2, 0xc000);
                    } else {
                        load8kRomBank((nes.rom.romCount - 1) * 2, 0x8000);
                    }
                    prgAddressChanged = false;
                }
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
        if (irqEnable == 1) {
            irqCounter--;
            if (irqCounter < 0) {
                nes.cpu.requestIrq(Cpu.Interrupt.IRQ);
                irqCounter = irqLatchValue;
            }
        }
    }


    public void loadROM() {
        load8kRomBank((nes.rom.romCount - 1) * 2, 0xc000);
        load8kRomBank((nes.rom.romCount - 1) * 2 + 1, 0xe000);

        load8kRomBank(0, 0x8000);
        load8kRomBank(1, 0xa000);

        super.loadCHRROM();
        nes.cpu.requestIrq(Cpu.Interrupt.RESET);
    }

    private void load1kVromBank(int bank, int addr) {
        if (nes.rom.vromCount == 0) {
            return;
        }
        this.nes.ppu.triggerRendering();

        int bank4k = bank / 4 % nes.rom.vromCount;
        int bankoffset = (bank % 4) * 1024;
        System.arraycopy(
                this.nes.rom.vrom[bank4k],
                bankoffset,
                this.nes.ppu.vramMem,
                addr,
                1024
        );

        // Update tiles:
        Tile[] vromTile = nes.rom.vromTile[bank4k];
        System.arraycopy(vromTile, (bank % 4 << 6), this.nes.ppu.ptTile, addr >> 4, 64);
    }
}
