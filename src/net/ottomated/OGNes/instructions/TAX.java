package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class TAX extends Instruction {

    public int run(int addr, int cycleAdd) {
        cpu.x = cpu.a;

        cpu.setZero(cpu.a == 0);
        cpu.setNegative(((cpu.a >> 7) & 1) == 1); // If the 7th bit is 1
        return 0;
    }

    TAX(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

