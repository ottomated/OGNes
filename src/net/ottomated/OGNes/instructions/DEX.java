package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class DEX extends Instruction {

    public int run(int addr, int addCycles) {
        cpu.x = (cpu.x - 1) & 0xff;
        cpu.setZero(cpu.x == 0);
        cpu.setNegative(((cpu.x >> 7) & 1) == 1); // If the 7th bit is 1
        return 0;
    }

    DEX(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}