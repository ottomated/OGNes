package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class PHP extends Instruction {

    @Override
    public int run(int addr, int cycleAdd) {
        cpu.pushStack(cpu.status);
        return 0;
    }

    PHP(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

