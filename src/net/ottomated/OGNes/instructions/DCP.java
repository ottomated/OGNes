package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class DCP extends Instruction {

    public int run(int addr, int addCycles) {
        int res = (cpu.load(addr) - 1) & 0xff;
        cpu.write(addr, res);

        res = cpu.a - res;
        cpu.setCarry(res >= 0);
        cpu.setNegative(((res >> 7) & 1) == 1);
        cpu.setZero((res & 0xff) == 0);
        if (mode != AddressingMode.INDEXED_INDIRECT) return addCycles;
        else return 0;
    }

    DCP(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }

}