package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class CLI extends Instruction {

    public int run(int addr, int cycleAdd) {
        cpu.setInterruptDisable(false);
        return 0;
    }

    CLI(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

