package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class SAX extends Instruction {

    public int run(int addr, int addCycles) {
        cpu.write(addr, cpu.a & cpu.x);
        return 0;
    }

    SAX(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }

}