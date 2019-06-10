package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class RRA extends Instruction {

    public int run(int addr, int cycleAdd) {
        int tmp = cpu.load(addr);
        int add = cpu.getCarry() ? 1 : 0;
        add <<= 7;
        cpu.setCarry((tmp & 1) == 1);
        tmp = (tmp >> 1) + add;
        cpu.write(addr, tmp);

        tmp = cpu.a + cpu.load(addr) + (cpu.getCarry() ? 1 : 0);
        cpu.setOverflow(((cpu.a ^ cpu.load(addr)) & 0x80) == 0 &&
                ((cpu.a ^ tmp) & 0x80) != 0);
        cpu.setCarry(tmp > 255);
        cpu.setNegative(((tmp >> 7) & 1) == 1);
        cpu.setZero((tmp & 0xff) == 0);

        cpu.a = tmp & 0xff;
        if (mode != AddressingMode.INDEXED_INDIRECT) return cycleAdd;
        else return 0;
    }

    RRA(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}
