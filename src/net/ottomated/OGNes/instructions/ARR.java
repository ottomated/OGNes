package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class ARR extends Instruction {

    @Override
    public int run(int addr, int cycleAdd) {
        int res = cpu.a & cpu.load(addr);
        cpu.a = (res >> 1) + ((cpu.getCarry() ? 1 : 0) << 7);
        cpu.setNegative(cpu.getCarry());
        cpu.setCarry(((res >> 7) & 1) == 1);
        cpu.setOverflow((((res >> 7) ^ (res >> 6)) & 1) == 1);
        return 0;
    }

    ARR(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}