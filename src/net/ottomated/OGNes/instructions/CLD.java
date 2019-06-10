package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class CLD extends Instruction {

    public int run(int addr, int cycleAdd) {
        cpu.setDecimalMode(false);
        return 0;
    }

    CLD(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

