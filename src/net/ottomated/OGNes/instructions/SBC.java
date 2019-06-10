package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class SBC extends Instruction {

    @Override
    public int run(int addr, int cycleAdd) {
        int res = cpu.a - cpu.load(addr) - (1 - (cpu.getCarry() ? 1 : 0));
        cpu.setZero(res == 0);
        cpu.setNegative(((res >> 7) & 1) == 1); // If the 7th bit is 1

        cpu.setOverflow(((cpu.a ^ cpu.load(res)) & 0x80) == 0 && // If the sign bit isn't write but should be
                ((cpu.a ^ res & 0x80) != 0));
        cpu.setCarry(res < 0);
        cpu.a = res & 0xff;
        if (mode != AddressingMode.INDEXED_INDIRECT) return cycleAdd;
        else return 0;
    }

    SBC(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}