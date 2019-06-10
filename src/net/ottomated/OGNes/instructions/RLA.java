package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class RLA extends Instruction {

    public int run(int addr, int cycleAdd) {
        int tmp = cpu.load(addr);
        int add = cpu.getCarry() ? 1 : 0;
        cpu.setCarry(((tmp >> 7) & 1) == 1);
        tmp = ((tmp << 1) & 0xff) + add;
        cpu.write(addr, tmp);

        cpu.a = cpu.a & tmp;
        cpu.setNegative(((cpu.a >> 7) & 1) == 1);
        cpu.setZero(cpu.a == 0);
        if (mode != AddressingMode.INDEXED_INDIRECT) return cycleAdd;
        else return 0;
    }

    RLA(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}
