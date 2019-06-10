package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class ALR extends Instruction {

    @Override
    public int run(int addr, int cycleAdd) {
        int res = cpu.a & cpu.load(addr);
        cpu.setCarry((res & 1) == 1);
        cpu.setNegative(false);
        cpu.a = res >> 1;
        cpu.setZero(cpu.a == 0);
        return 0;
    }

    ALR(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}