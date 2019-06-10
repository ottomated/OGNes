package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class STY extends Instruction {

    public int run(int addr, int cycleAdd) {
        cpu.write(addr, cpu.y);
        return 0;
    }

    STY(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

