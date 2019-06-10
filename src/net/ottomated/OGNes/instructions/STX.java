package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class STX extends Instruction {

    public int run(int addr, int cycleAdd) {
        cpu.write(addr, cpu.x);
        return 0;
    }

    STX(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

