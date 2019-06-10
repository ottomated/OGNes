package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class INC extends Instruction {

    @Override
    public int run(int addr, int cycleAdd) {
        int res = (cpu.load(addr) + 1) & 0xff;
        cpu.setZero(res == 0);
        cpu.setNegative(((res >> 7) & 1) == 1); // If the 7th bit is 1
        cpu.write(addr, res);
        return 0;
    }

    INC(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}