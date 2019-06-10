package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class AND extends Instruction {

    public int run(int addr, int cycleAdd) {
        int res = cpu.a & cpu.load(addr);

        cpu.setNegative(((res >> 7) & 1) == 1); // If the 7th bit is 1
        cpu.setZero(res == 0);
        cpu.a = res & 255; // Don't overflow
        if (mode != AddressingMode.INDEXED_INDIRECT) return cycleAdd;
        else return 0;
    }

    AND(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

