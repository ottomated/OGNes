package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class ORA extends Instruction {

    @Override
    public int run(int addr, int cycleAdd) {
        int tmp = (cpu.load(addr) | cpu.a) & 0xff;
        cpu.setNegative(((tmp >> 7) & 1) == 1); // If the 7th bit is 1

        cpu.setZero(tmp == 0);
        cpu.a = tmp;
        if (mode != AddressingMode.INDEXED_INDIRECT) return cycleAdd;
        else return 0;
    }


    ORA(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}
