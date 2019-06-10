package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class ANC extends Instruction {

    @Override
    public int run(int addr, int cycleAdd) {
        cpu.a = cpu.a & cpu.load(addr);
        cpu.setCarry(((cpu.a >> 7) & 1)== 1);
        cpu.setNegative(((cpu.a >> 7) & 1)== 1);
        cpu.setZero(cpu.a == 0);
        return 0;
    }

    ANC(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}