package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class STA extends Instruction {

    public int run(int addr, int cycleAdd) {
        cpu.write(addr, cpu.a);
        return 0;
    }

    STA(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

