package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class ADC extends Instruction {

    public int run(int addr, int cycleAdd) {
        int res = cpu.a + cpu.load(addr) + (cpu.getCarry() ? 1 : 0);

        cpu.setOverflow(((cpu.a ^ cpu.load(res) & 0x80) == 0 && // If the sign bit isn't write but should be
                ((cpu.a ^ res & 0x80) != 0)));
        cpu.setCarry(res > 255);
        cpu.setNegative(((res >> 7) & 1) == 1); // If the 7th bit is 1
        cpu.setZero(res == 0);
        cpu.a = res & 255; // Don't overflow
        return cycleAdd;
    }

    ADC(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.cycles = cycles;
        this.size = size;
    }

}
