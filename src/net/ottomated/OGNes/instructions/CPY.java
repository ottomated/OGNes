package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class CPY extends Instruction {

    @Override
    public int run(int addr, int cycleAdd) {
        int res = cpu.y - cpu.load(addr);
        cpu.setCarry(res >= 0);
        cpu.setNegative(((res >> 7) & 1) == 1); // If the 7th bit is 1
        cpu.setZero(res == 0);
        return 0;
    }

    CPY(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }

}