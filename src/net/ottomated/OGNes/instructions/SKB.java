package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class SKB extends Instruction {

    public int run(int addr, int addCycles) {
        // NOP
        return 0;
    }

    SKB(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }

}