package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class SEI extends Instruction {

    public int run(int addr, int cycleAdd) {
        cpu.setInterruptDisable(true);
        return 0;
    }

    SEI(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

