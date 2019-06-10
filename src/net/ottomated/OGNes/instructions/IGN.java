package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class IGN extends Instruction {

    public int run(int addr, int addCycles) {
        // Just load (TODO)
        cpu.load(addr);
        if (mode != AddressingMode.INDEXED_INDIRECT) return addCycles;
        else return 0;
    }

    IGN(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }

}