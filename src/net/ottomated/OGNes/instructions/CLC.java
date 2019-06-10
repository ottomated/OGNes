package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class CLC extends Instruction {

    public int run(int addr, int cycleAdd) {
        cpu.setCarry(false);
        return 0;
    }

    CLC(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

