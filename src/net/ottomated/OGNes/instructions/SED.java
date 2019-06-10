package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class SED extends Instruction {

    public int run(int addr, int cycleAdd) {
        cpu.setDecimalMode(true);
        return 0;
    }

    SED(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

