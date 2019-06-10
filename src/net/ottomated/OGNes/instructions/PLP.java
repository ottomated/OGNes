package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class PLP extends Instruction {

    @Override
    public int run(int addr, int cycleAdd) {
        cpu.status = cpu.popStack();
        return 0;
    }

    PLP(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

