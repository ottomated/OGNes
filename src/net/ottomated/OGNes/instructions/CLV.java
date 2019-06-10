package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class CLV extends Instruction {

    public int run(int addr, int cycleAdd) {
        cpu.setOverflow(false);
        return 0;
    }

    CLV(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

