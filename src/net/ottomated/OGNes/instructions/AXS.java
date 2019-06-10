package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class AXS extends Instruction {

    @Override
    public int run(int addr, int cycleAdd) {
        int res = (cpu.a & cpu.x) - cpu.load(addr);
        cpu.setNegative(((res >> 7) & 1) == 1);
        cpu.setZero((res & 0xff) == 0);

        cpu.setOverflow(((cpu.x ^ res) & 0x80) != 0 &&
                ((cpu.x ^ cpu.load(addr)) & 0x80) != 0);
        cpu.setCarry(res >= 0);
        cpu.x = res & 0xff;
        return 0;
    }

    AXS(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}