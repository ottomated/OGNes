package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class PHA extends Instruction {

    @Override
    public int run(int addr, int cycleAdd) {
        cpu.pushStack(cpu.a);
        return 0;
    }

    PHA(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

