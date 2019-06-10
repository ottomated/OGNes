package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class TXS extends Instruction {

    public int run(int addr, int cycleAdd) {
        cpu.sp = cpu.x + 0x0100;
        cpu.sp = 0x0100 | (cpu.sp & 0xff);
        return 0;
    }

    TXS(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

