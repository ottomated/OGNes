package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class SEC extends Instruction {

    public int run(int addr, int cycleAdd) {
        cpu.setCarry(true);
        return 0;
    }

    SEC(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

