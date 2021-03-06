package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class NOP extends Instruction {

    public int run(int addr, int addCycles) {
        // NOP
        return 0;
    }

    NOP(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }

}