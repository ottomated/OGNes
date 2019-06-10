package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class TYA extends Instruction {

    public int run(int addr, int cycleAdd) {
        cpu.a = cpu.y;

        cpu.setZero(cpu.y == 0);
        cpu.setNegative(((cpu.y >> 7) & 1) == 1); // If the 7th bit is 1
        return 0;
    }

    TYA(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

