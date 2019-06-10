package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class BIT extends Instruction {

    @Override
    public int run(int addr, int cycleAdd) {
        int tmp = cpu.load(addr);
        cpu.setNegative(((tmp >> 7) & 1) == 1); // If the 7th bit is 1
        cpu.setOverflow(((tmp >> 6) & 1) == 1); // If the 6th bit is 1
        tmp &= cpu.a;
        cpu.setZero(tmp == 0);
        return 0;
    }


    BIT(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

