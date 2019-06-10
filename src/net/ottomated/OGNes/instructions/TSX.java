package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class TSX extends Instruction {

    public int run(int addr, int cycleAdd) {
        cpu.x = cpu.sp - 0x0100;

        cpu.setZero(cpu.x == 0);
        cpu.setNegative(((cpu.sp >> 7) & 1) == 1); // If the 7th bit is 1
        return 0;
    }

    TSX(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

